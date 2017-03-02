package org.sputnikdev.bluetooth.gattparser;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.sputnikdev.bluetooth.gattparser.spec.Bit;
import org.sputnikdev.bluetooth.gattparser.spec.Enumeration;
import org.sputnikdev.bluetooth.gattparser.spec.Enumerations;
import org.sputnikdev.bluetooth.gattparser.spec.Field;
import org.sputnikdev.bluetooth.gattparser.spec.FieldFormat;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockUtils {

    public static Field mockControlField(String name, boolean isMandatory, String... enumerations) {
        Field field = mock(Field.class);
        when(field.getName()).thenReturn(name);
        when(field.getEnumerations()).thenReturn(mock(Enumerations.class, RETURNS_DEEP_STUBS));
        when(field.getMultiplier()).thenReturn(null);
        when(field.getDecimalExponent()).thenReturn(null);
        when(field.getBinaryExponent()).thenReturn(null);
        List<Enumeration> enums = new ArrayList<>();
        when(field.getEnumerations().getEnumerations()).thenReturn(enums);
        if (isMandatory) {
            when(field.getRequirements()).thenReturn(Arrays.asList("Mandatory"));
        }
        int i = 1;
        for (String enumeration : enumerations) {
            Enumeration en = mock(Enumeration.class);
            when(en.getKey()).thenReturn(i++);
            when(en.getRequires()).thenReturn(enumeration);
            enums.add(en);
        }
        return field;
    }

    public static Field mockField(String name, String... requirements) {
        Field field = mock(Field.class);
        when(field.getName()).thenReturn(name);
        when(field.getRequirements()).thenReturn(Arrays.asList(requirements));
        return field;
    }

    public static Bit mockBit(int index, int size, String flagPrefix) {
        Bit bit = mock(Bit.class);
        when(bit.getIndex()).thenReturn(index);
        when(bit.getSize()).thenReturn(size);
        for (int i = 0; i <= Math.pow(2, size); i++) {
            when(bit.getFlag((byte) i)).thenReturn(flagPrefix + i);
        }
        return bit;
    }

    public static Bit mockBit(int index, String flag) {
        Bit bit = mock(Bit.class);
        when(bit.getIndex()).thenReturn(index);
        when(bit.getSize()).thenReturn(1);
        when(bit.getFlag((byte) 1)).thenReturn(flag);
        return bit;
    }

    public static Field mockFieldFormat(String name, String format) {
        Field field = mock(Field.class);
        when(field.getFormat()).thenReturn(FieldFormat.valueOf(format));
        when(field.getName()).thenReturn(name);
        when(field.getMultiplier()).thenReturn(null);
        when(field.getDecimalExponent()).thenReturn(null);
        when(field.getBinaryExponent()).thenReturn(null);
        return field;
    }

    public static Field mockFieldFormat(String name, String format, String... requirements) {
        Field field = mockFieldFormat(name, format);
        when(field.getRequirements()).thenReturn(Arrays.asList(requirements));
        return field;
    }

    public static Enumeration mockEnumeration(Integer key, String flag) {
        Enumeration enumeration = mock(Enumeration.class);
        when(enumeration.getKey()).thenReturn(key);
        when(enumeration.getRequires()).thenReturn(flag);
        return enumeration;
    }


}
