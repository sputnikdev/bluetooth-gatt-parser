package org.bluetooth.gattparser;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bluetooth.gattparser.num.FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.RealNumberFormatter;
import org.bluetooth.gattparser.spec.Bit;
import org.bluetooth.gattparser.spec.Characteristic;
import org.bluetooth.gattparser.spec.Field;
import org.bluetooth.gattparser.spec.FieldFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericCharacteristicParser implements CharacteristicParser {

    private final Logger logger = LoggerFactory.getLogger(GenericCharacteristicParser.class);

    GenericCharacteristicParser() { }

    public Map<String, FieldHolder> parse(Characteristic characteristic, byte[] raw)
            throws CharacteristicFormatException {
        Map<String, FieldHolder> result = new HashMap<>();

        if (!characteristic.isValidForRead()) {
            logger.error("Characteristic cannot be parsed: \"{}\".", characteristic.getName());
            throw new CharacteristicFormatException("Characteristic cannot be parsed: \"" +
                    characteristic.getName() + "\".");
        }

        int offset = 0;
        Set<String> requires = getFlags(characteristic, raw);
        for (Field field : characteristic.getValue().getFields()) {
            if (field.getName().equalsIgnoreCase("flags")) {
                // skipping flags field
                continue;
            }
            List<String> requirements = field.getRequirements();
            if (requirements != null && !requirements.isEmpty() && !requires.containsAll(requirements)) {
                // skipping field as per requirement in the Flags field
                continue;
            }

            FieldFormat fieldFormat = field.getFormat();
            Object value = parse(field, raw, offset);
            result.put(field.getName(), new FieldHolder(field, value));
            if (fieldFormat.getSize() == FieldFormat.FULL_SIZE) {
                // full size field, e.g. a string
                break;
            }
            offset += field.getFormat().getSize();
        }
        return result;
    }

    int[] parseFlags(Field flagsField, byte[] raw) {
        RealNumberFormatter realNumberFormatter = BluetoothGattParserFactory.getTwosComplementNumberFormatter();
        BitSet bitSet = BitSet.valueOf(raw).get(0, flagsField.getFormat().getSize());
        List<Bit> bits = flagsField.getBitField().getBits();
        int[] flags = new int[bits.size()];
        int offset = 0;
        for (int i = 0; i < bits.size(); i++) {
            int size = bits.get(i).getSize();
            flags[i] = realNumberFormatter.deserializeInteger(bitSet.get(offset, offset + size), size, false);
            offset += size;
        }
        return flags;
    }

    Set<String> getFlags(Characteristic characteristic, byte[] raw) {
        Set<String> flags = new HashSet<>();
        Field flagsField = characteristic.getValue().getFlags();
        if (flagsField != null && flagsField.getBitField() != null) {
            int[] values = parseFlags(flagsField, raw);
            int i = 0;
            for (Bit bit : flagsField.getBitField().getBits()) {
                String value = bit.getRequires((byte) values[i++]);
                if (value != null) {
                    flags.add(value);
                }
            }
        }
        return flags;
    }

    Object parse(Field field, byte[] raw, int offset) {
        FieldFormat fieldFormat = field.getFormat();
        int size = fieldFormat.getSize();
        switch (fieldFormat.getType()) {
            case BOOLEAN: return raw[offset] == 1;
            case UINT: return deserializeReal(raw, offset, size, false);
            case SINT: return deserializeReal(raw, offset, size, true);
            case FLOAT_IEE754: return deserializeFloat(
                    BluetoothGattParserFactory.getIEEE754FloatingPointNumberFormatter(), raw, offset, size);
            case FLOAT_IEE11073: return deserializeFloat(
                    BluetoothGattParserFactory.getIEEE11073FloatingPointNumberFormatter(), raw, offset, size);
            case UTF8S: return deserializeString(raw, "UTF-8");
            case UTF16S: return deserializeString(raw, "UTF-16");
            default:
                throw new IllegalStateException("Unsupported field format: " + fieldFormat.getType());
        }
    }

    private Object deserializeReal(byte[] raw, int offset, int size, boolean signed) {
        RealNumberFormatter realNumberFormatter = BluetoothGattParserFactory.getTwosComplementNumberFormatter();
        if ((signed && size <= 32) || (!signed && size < 32)) {
            return realNumberFormatter.deserializeInteger(BitSet.valueOf(raw).get(offset, size), size, signed);
        } else if ((signed && size <= 64) || (!signed && size < 64)) {
            return realNumberFormatter.deserializeLong(BitSet.valueOf(raw).get(offset, size), size, signed);
        } else {
            return realNumberFormatter.deserializeBigInteger(BitSet.valueOf(raw).get(offset, size), size, signed);
        }
    }

    private Object deserializeFloat(FloatingPointNumberFormatter formatter, byte[] raw, int offset, int size) {
        if (size == 16) {
            return formatter.deserializeSFloat(BitSet.valueOf(raw).get(offset, size));
        } else if (size == 32) {
            return formatter.deserializeFloat(BitSet.valueOf(raw).get(offset, size));
        } else if (size == 64) {
            return formatter.deserializeDouble(BitSet.valueOf(raw).get(offset, size));
        } else {
            throw new IllegalStateException("Unknown bit size for float numbers: " + size);
        }
    }

    private String deserializeString(byte[] raw, String encoding) {
        try {
            return new String(raw, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}
