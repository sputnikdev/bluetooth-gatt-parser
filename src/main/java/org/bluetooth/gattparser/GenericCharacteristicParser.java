package org.bluetooth.gattparser;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
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

public class GenericCharacteristicParser implements CharacteristicParser {

    private final Logger logger = LoggerFactory.getLogger(GenericCharacteristicParser.class);
    private final BluetoothGattSpecificationReader reader;

    GenericCharacteristicParser(BluetoothGattSpecificationReader reader) {
        this.reader = reader;
    }

    public LinkedHashMap<String, FieldHolder> parse(Characteristic characteristic, byte[] raw)
            throws CharacteristicFormatException {
        return parse(characteristic, raw, 0);
    }

    LinkedHashMap<String, FieldHolder> parse(Characteristic characteristic, byte[] raw, int index)
            throws CharacteristicFormatException {
        LinkedHashMap<String, FieldHolder> result = new LinkedHashMap<>();

        validate(characteristic);

        int offset = 0;
        Set<String> requires = getFlags(characteristic, raw);
        requires.add("Mandatory");
        for (Field field : characteristic.getValue().getFields()) {
            if (field.getReference() != null) {
                LinkedHashMap<String, FieldHolder> subCharacteristic =
                        parse(reader.getCharacteristicByType(field.getReference().trim()),
                                BitSet.valueOf(raw).get(offset, raw.length * 8).toByteArray(), index);
                result.putAll(subCharacteristic);
                int size = getSize(subCharacteristic.values());
                if (size == FieldFormat.FULL_SIZE) {
                    break;
                }
                offset += size;
                index += subCharacteristic.size();
            } else {

                if (field.getName().equalsIgnoreCase("flags")) {
                    // skipping flags field
                    offset += field.getFormat().getSize();
                    continue;
                }
                List<String> requirements = field.getRequirements();
                if (requirements != null && !requirements.isEmpty() && !requires.containsAll(requirements)) {
                    // skipping field as per requirement in the Flags field
                    continue;
                }

                FieldFormat fieldFormat = field.getFormat();
                result.put(field.getName(), parseField(field, raw, offset, index));
                if (fieldFormat.getSize() == FieldFormat.FULL_SIZE) {
                    // full size field, e.g. a string
                    break;
                }
                offset += field.getFormat().getSize();
                index++;
            }
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

    private FieldHolder parseField(Field field, byte[] raw, int offset, int index) {
        FieldFormat fieldFormat = field.getFormat();
        if (fieldFormat.getSize() != FieldFormat.FULL_SIZE && offset + fieldFormat.getSize() > raw.length * 8) {
            throw new CharacteristicFormatException(
                    "Not enough bits to parse field \"" + field.getName() + "\". "
                            + "Data length: " + raw.length + " bytes. "
                            + "Looks like your device does not conform SIG specification.");
        }
        Object value = parse(field, raw, offset);
        return new FieldHolder(field, value, index);

    }

    private void validate(Characteristic characteristic) {
        if (!characteristic.isValidForRead()) {
            logger.error("Characteristic cannot be parsed: \"{}\".", characteristic.getName());
            throw new CharacteristicFormatException("Characteristic cannot be parsed: \"" +
                    characteristic.getName() + "\".");
        }
    }

    private int getSize(Collection<FieldHolder> holders) {
        int size = 0;
        for (FieldHolder holder : holders) {
            Field field = holder.getField();
            if (field.getFormat().getSize() == FieldFormat.FULL_SIZE) {
                return FieldFormat.FULL_SIZE;
            }
            size += field.getFormat().getSize();
        }
        return size;
    }

    private Object deserializeReal(byte[] raw, int offset, int size, boolean signed) {
        RealNumberFormatter realNumberFormatter = BluetoothGattParserFactory.getTwosComplementNumberFormatter();
        int toIndex = offset + size;
        if ((signed && size <= 32) || (!signed && size < 32)) {
            return realNumberFormatter.deserializeInteger(BitSet.valueOf(raw).get(offset, toIndex), size, signed);
        } else if ((signed && size <= 64) || (!signed && size < 64)) {
            return realNumberFormatter.deserializeLong(BitSet.valueOf(raw).get(offset, toIndex), size, signed);
        } else {
            return realNumberFormatter.deserializeBigInteger(BitSet.valueOf(raw).get(offset, toIndex), size, signed);
        }
    }

    private Object deserializeFloat(FloatingPointNumberFormatter formatter, byte[] raw, int offset, int size) {
        int toIndex = offset + size;
        if (size == 16) {
            return formatter.deserializeSFloat(BitSet.valueOf(raw).get(offset, toIndex));
        } else if (size == 32) {
            return formatter.deserializeFloat(BitSet.valueOf(raw).get(offset, toIndex));
        } else if (size == 64) {
            return formatter.deserializeDouble(BitSet.valueOf(raw).get(offset, toIndex));
        } else {
            throw new IllegalStateException("Unknown bit size for float numbers: " + size);
        }
    }

    private String deserializeString(byte[] raw, String encoding) {
        try {
            return new String(raw, encoding).trim();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}
