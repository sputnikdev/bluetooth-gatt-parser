package org.sputnikdev.bluetooth.gattparser.spec;

/*-
 * #%L
 * org.sputnikdev:bluetooth-gatt-parser
 * %%
 * Copyright (C) 2017 Sputnik Dev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.sputnikdev.bluetooth.gattparser.BluetoothGattParserFactory;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Vlad Kolotov
 */
public final class FlagUtils {

    private FlagUtils() { }

    public static Set<String> getReadFlags(List<Field> fields, byte[] data) {
        Set<String> flags = new HashSet<>();
        int index = 0;
        for (Field field : fields) {
            if (isFlagsField(field)) {
                int[] values = parseReadFlags(field, data, index);
                int bitIndex = 0;
                for (Bit bit : field.getBitField().getBits()) {
                    String requires = bit.getFlag((byte) values[bitIndex++]);
                    if (requires != null) {
                        List<String> flgs = Arrays.asList(requires.split(","));
                        if (!flgs.isEmpty()) {
                            flags.addAll(flgs);
                        }
                    }
                }
                break;
            }
            if (field.getReference() != null) {
                // if flags field goes after a reference field, then it is not possible to parse the such characteristic
                // simply because we don't know if this reference field if optional or not
                break;
            }
            index += field.getFormat().getSize();
        }
        return flags;
    }

    public static String getWriteFlag(Field field, Integer key) {
        if (field.getEnumerations() == null || field.getEnumerations().getEnumerations() == null) {
            return null;
        }

        if (key == null) {
            return null;
        }

        for (Enumeration enumeration : field.getEnumerations().getEnumerations()) {
            if (key.equals(enumeration.getKey())) {
                return enumeration.getRequires();
            }
        }
        return null;
    }

    public static boolean isFlagsField(Field field) {
        return "flags".equalsIgnoreCase(field.getName()) && field.getBitField() != null;
    }

    static Set<String> getAllReadFlags(Field flagsField) {
        Set<String> result = new HashSet<>();
        if (flagsField != null && flagsField.getBitField() != null) {
            for (Bit bit : flagsField.getBitField().getBits()) {
                for (Enumeration enumeration : bit.getEnumerations().getEnumerations()) {
                    if (enumeration.getRequires() != null) {
                        result.add(enumeration.getRequires());
                    }
                }
            }
        }
        return result;
    }

    static Set<String> getAllWriteFlags(Field field) {
        Set<String> result = new HashSet<>();
        if (field.getEnumerations() == null || field.getEnumerations().getEnumerations() == null) {
            return Collections.EMPTY_SET;
        }
        for (Enumeration enumeration : field.getEnumerations().getEnumerations()) {
            result.add(enumeration.getRequires());
        }
        return result;
    }

    static Field getFlags(List<Field> fields) {
        for (Field field : fields) {
            if (isFlagsField(field)) {
                return field;
            }
        }
        return null;
    }

    static int[] parseReadFlags(Field flagsField, byte[] raw, int index) {
        BitSet bitSet = BitSet.valueOf(raw).get(index, index + flagsField.getFormat().getSize());
        List<Bit> bits = flagsField.getBitField().getBits();
        int[] flags = new int[bits.size()];
        int offset = 0;
        for (int i = 0; i < bits.size(); i++) {
            int size = bits.get(i).getSize();
            flags[i] = BluetoothGattParserFactory.getTwosComplementNumberFormatter().deserializeInteger(
                bitSet.get(offset, offset + size), size, false);
            offset += size;
        }
        return flags;
    }

}
