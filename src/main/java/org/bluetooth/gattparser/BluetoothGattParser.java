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
import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.bluetooth.gattparser.spec.Characteristic;
import org.bluetooth.gattparser.spec.Field;
import org.bluetooth.gattparser.spec.FieldFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothGattParser {

    private final Logger logger = LoggerFactory.getLogger(BluetoothGattParser.class);

    private BluetoothGattSpecificationReader specReader;
    private RealNumberFormatter realNumberFormatter;
    private FloatingPointNumberFormatter floatingPointNumberFormatter;

    BluetoothGattParser(BluetoothGattSpecificationReader specReader, RealNumberFormatter realNumberFormatter,
            FloatingPointNumberFormatter floatingPointNumberFormatter) {
        this.specReader = specReader;
        this.realNumberFormatter = realNumberFormatter;
        this.floatingPointNumberFormatter = floatingPointNumberFormatter;
    }

    public Map<String, Object> parse(String characteristicUUID, byte[] raw) throws CharacteristicFormatException {
        Map<String, Object> result = new HashMap<>();
        Characteristic characteristic = specReader.getCharacteristic(characteristicUUID);

        if (!characteristic.isValidForRead()) {
            logger.error("Characteristic cannot be parsed: \"{}\".", characteristic.getName());
            throw new CharacteristicFormatException("Characteristic cannot be parsed: \"" +
                    characteristic.getName() + "\".");
        }

        int offset = 0;
        Set<String> requires = getRequires(characteristic, raw);
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
            result.put(field.getName(), value);
            if (fieldFormat.getSize() == FieldFormat.FULL_SIZE) {
                // full size field, e.g. a string
                break;
            }
            offset += field.getFormat().getSize();
        }
        return result;
    }

    private Set<String> getRequires(Characteristic characteristic, byte[] raw) {
        Set<String> requires = new HashSet<>();
        Field flagsField = characteristic.getValue().getFlags();
        if (flagsField != null && flagsField.getBitField() != null) {
            BitSet flags = BitSet.valueOf(new long[] {(long) parse(flagsField, raw, 0)});
            for (Bit bit : flagsField.getBitField().getBits()) {
                String value = bit.getRequires((byte) (flags.get(bit.getIndex()) ? 1 : 0));
                if (value != null) {
                    requires.add(value);
                }
            }
        }
        return requires;
    }

    private Object parse(Field field, byte[] raw, int offset) {
        FieldFormat fieldFormat = field.getFormat();
        int size = fieldFormat.getSize();
        Integer exponent = field.getDecimalExponent();
        //TODO handle exponent
        switch (fieldFormat.getType()) {
            case BOOLEAN: return raw[offset] == 1;
            case UINT: return deserializeReal(raw, offset, size, false);
            case SINT: return deserializeReal(raw, offset, size, true);
            case FLOAT: return deserializeFloat(raw, offset, size);
            case UTF8S: return deserializeString(raw, "UTF-8");
            case UTF16S: return deserializeString(raw, "UTF-16");
            default:
                throw new IllegalStateException("Unsupported field format: " + fieldFormat.getType());
        }
    }

    private Object deserializeReal(byte[] raw, int offset, int size, boolean signed) {
        if ((signed && size < 32) || (!signed && size <= 32)) {
            return realNumberFormatter.deserializeInteger(BitSet.valueOf(raw).get(offset, raw.length), size, signed);
        } else if ((signed && size < 64) || (!signed && size <= 64)) {
            return realNumberFormatter.deserializeLong(BitSet.valueOf(raw).get(offset, raw.length), size, signed);
        } else {
            return realNumberFormatter.deserializeBigInteger(BitSet.valueOf(raw).get(offset, raw.length), size, signed);
        }
    }

    private Object deserializeFloat(byte[] raw, int offset, int size) {
        if (size == 16) {
            return floatingPointNumberFormatter.deserializeSFloat(BitSet.valueOf(raw).get(offset, raw.length));
        } else if (size == 32) {
            return floatingPointNumberFormatter.deserializeFloat(BitSet.valueOf(raw).get(offset, raw.length));
        } else if (size == 64) {
            return floatingPointNumberFormatter.deserializeDouble(BitSet.valueOf(raw).get(offset, raw.length));
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
