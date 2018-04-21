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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BitTest {

    @Mock
    private Enumerations enumerations;
    @InjectMocks
    private Bit bit = new Bit();

    @Test
    public void testGetRequires() throws Exception {
        List<Enumeration> enums = new ArrayList<>();
        enums.add(mockEnumeration(1, "C1"));
        enums.add(mockEnumeration(2, "C2"));
        enums.add(mockEnumeration(3, null));
        when(enumerations.getEnumerations()).thenReturn(enums);

        assertEquals("C1", bit.getFlag((byte) 1));
        assertEquals("C2", bit.getFlag((byte) 2));
        assertEquals(null, bit.getFlag((byte) 3));
        assertEquals(null, bit.getFlag((byte) 4));
    }

    private Enumeration mockEnumeration(Integer key, String requires) {
        Enumeration enumeration = mock(Enumeration.class);
        when(enumeration.getKey()).thenReturn(BigInteger.valueOf(key));
        when(enumeration.getRequires()).thenReturn(requires);
        return enumeration;
    }
}
