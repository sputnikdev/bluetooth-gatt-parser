package org.openhab.bluetooth.gattparser.spec;

/*-
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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.openhab.bluetooth.gattparser.BluetoothGattParserFactory;
import org.openhab.bluetooth.gattparser.MockUtils;
import org.openhab.bluetooth.gattparser.num.RealNumberFormatter;
import org.openhab.bluetooth.gattparser.spec.Bit;
import org.openhab.bluetooth.gattparser.spec.Enumeration;
import org.openhab.bluetooth.gattparser.spec.Field;
import org.openhab.bluetooth.gattparser.spec.FieldFormat;
import org.openhab.bluetooth.gattparser.spec.FlagUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(BluetoothGattParserFactory.class)
public class FlagUtilsTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Field flagField;

    @Mock
    private RealNumberFormatter twosComplementNumberFormatter;

    @Before
    public void setUp() {
        when(flagField.getName()).thenReturn("fLags");
    }

    @Test
    public void testGetReadFlags() throws Exception {
        List<Bit> bits = new ArrayList<>();
        when(flagField.getBitField().getBits()).thenReturn(bits);
        when(flagField.getFormat().getSize()).thenReturn(15);
        bits.add(MockUtils.mockBit(0, 1, "A"));
        bits.add(MockUtils.mockBit(1, 2, "B"));
        bits.add(MockUtils.mockBit(2, 1, "C"));
        bits.add(MockUtils.mockBit(3, 3, "D"));
        bits.add(MockUtils.mockBit(4, 2, "E"));
        bits.add(MockUtils.mockBit(5, 2, "F"));
        bits.add(MockUtils.mockBit(6, 4, "G"));

        byte[] raw = new byte[] { (byte) 0b10100101, (byte) 0b01010001 };
        int[] flagsValues = FlagUtils.parseReadFlags(flagField, raw, 0);
        assertArrayEquals(new int[] {1, 2, 0, 2, 3, 0, 10}, flagsValues);

        Set<String> flags = FlagUtils.getReadFlags(Arrays.asList(flagField), raw);
        assertEquals(7, flags.size());
        assertTrue(flags.containsAll(Arrays.asList("A1", "B2", "C0", "D2", "E3", "F0", "G10")));
    }

    @Test
    public void testGetRequires() throws Exception {
        List<Enumeration> enumerations = new ArrayList<>();
        enumerations.add(MockUtils.mockEnumeration(1, "C1"));
        enumerations.add(MockUtils.mockEnumeration(2, null));
        enumerations.add(MockUtils.mockEnumeration(3, "C2"));
        when(flagField.getEnumerations().getEnumerations()).thenReturn(enumerations);

        assertEquals("C1", FlagUtils.getRequires(flagField, new BigInteger("1")));
        assertNull(FlagUtils.getRequires(flagField, new BigInteger("2")));
        assertEquals("C2", FlagUtils.getRequires(flagField, new BigInteger("3")));

        assertNull(FlagUtils.getRequires(flagField, null));
        when(flagField.getEnumerations().getEnumerations()).thenReturn(null);
        assertNull(FlagUtils.getRequires(flagField, new BigInteger("1")));
    }

    @Test
    public void testGetEnumerationValue() throws Exception {
        List<Enumeration> enumerations = new ArrayList<>();
        enumerations.add(MockUtils.mockEnumeration(1, "C1", "First"));
        enumerations.add(MockUtils.mockEnumeration(2, "C2", "Second"));
        enumerations.add(MockUtils.mockEnumeration(3, "C2", "Third"));
        when(flagField.getEnumerations().getEnumerations()).thenReturn(enumerations);

        assertEquals("C2", FlagUtils.getEnumeration(flagField, new BigInteger("2")).get().getRequires());
        assertEquals("Second", FlagUtils.getEnumeration(flagField, new BigInteger("2")).get().getValue());

        assertFalse(FlagUtils.getEnumeration(flagField, new BigInteger("4")).isPresent());
    }

    @Test
    public void testGetAllOpCodes() {
        assertTrue(FlagUtils.getAllOpCodes(flagField).isEmpty());

        List<Enumeration> enumerations = new ArrayList<>();
        enumerations.add(MockUtils.mockEnumeration(1, "C1"));
        enumerations.add(MockUtils.mockEnumeration(2, null));
        enumerations.add(MockUtils.mockEnumeration(3, "C2"));
        when(flagField.getEnumerations().getEnumerations()).thenReturn(enumerations);
        assertTrue(FlagUtils.getAllOpCodes(flagField).containsAll(Arrays.asList("C1", "C2")));
    }

    @Test
    public void testGetReadFlagsComplex() {
        PowerMockito.mockStatic(BluetoothGattParserFactory.class);
        when(BluetoothGattParserFactory.getTwosComplementNumberFormatter()).thenReturn(twosComplementNumberFormatter);

        List<Bit> bits = new ArrayList<>();

        bits.add(MockUtils.mockBit(0, 1, "A"));
        bits.add(MockUtils.mockBit(1, 2, "B"));
        bits.add(MockUtils.mockBit(2, 1, "C"));
        bits.add(MockUtils.mockBit(3, 3, "D"));

        bits.add(MockUtils.mockBit(4, 2, "E"));
        bits.add(MockUtils.mockBit(5, 2, "F"));
        bits.add(MockUtils.mockBit(6, 4, "G"));

        when(flagField.getBitField().getBits()).thenReturn(bits);
        when(flagField.getFormat()).thenReturn(FieldFormat.valueOf("15bit"));
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b1}), 1, false)).thenReturn(1);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b10}), 2, false)).thenReturn(2);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b0}), 1, false)).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b010}), 3, false)).thenReturn(2);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b11}), 2, false)).thenReturn(3);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b0}), 2, false)).thenReturn(0);
        when(twosComplementNumberFormatter.deserializeInteger(BitSet.valueOf(new byte[]{0b1010}), 4, false)).thenReturn(10);

        Set<String> flags = FlagUtils.getReadFlags(Arrays.asList(flagField), new byte[] {(byte) 0b10100101, (byte) 0b01010001});
        assertTrue(flags.contains("A1"));
        assertTrue(flags.contains("B2"));
        assertTrue(flags.contains("C0"));
        assertTrue(flags.contains("D2"));
        assertTrue(flags.contains("E3"));
        assertTrue(flags.contains("F0"));
        assertTrue(flags.contains("G10"));
    }

}
