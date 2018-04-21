package org.sputnikdev.bluetooth.gattparser.num;

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
import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwosComplementNumberFormatterTest {

    private TwosComplementNumberFormatter formatter = new TwosComplementNumberFormatter();

    @Test
    public void testDeserialize1Bit() throws Exception {
        assertDeserializeSerialize(0b01010111, 1, false, 1);
        assertDeserializeSerialize(0b01010110, 1, false, 0);
        assertDeserializeSerialize(0b01010111, 1, true, 1);
        assertDeserializeSerialize(0b01010110, 1, true, 0);
    }

    @Test
    public void testDeserialize2Bits() throws Exception {
        //assertDeserializeSerialize(0b01010100000000, 2, false, 0);
        assertDeserializeSerialize(0b01010100000001, 2, false, 1);
        assertDeserializeSerialize(0b01010100000010, 2, false, 2);
        assertDeserializeSerialize(0b01010100000011, 2, false, 3);

        assertDeserializeSerialize(0b01010100000011, 2, true, -1);
        assertDeserializeSerialize(0b01010100000010, 2, true, -2);
        assertDeserializeSerialize(0b01010100000001, 2, true, 1);
        assertDeserializeSerialize(0b01010100000000, 2, true, 0);
    }

    @Test
    public void testDeserializeNibbleBits() throws Exception {
        assertDeserializeSerialize(0b010100000000, 4, false, 0);
        assertDeserializeSerialize(0b010100000001, 4, false, 1);
        assertDeserializeSerialize(0b010100001111, 4, false, 15);

        assertDeserializeSerialize(0b010100001111, 4, true, -1);
        assertDeserializeSerialize(0b010100001110, 4, true, -2);
        assertDeserializeSerialize(0b010100001000, 4, true, -8);
        assertDeserializeSerialize(0b010100000111, 4, true, 7);
        assertDeserializeSerialize(0b010100000000, 4, true, 0);
    }

    @Test
    public void testDeserialize8Bits() throws Exception {
        assertDeserializeSerialize(0b0101010100000000, 8, false, 0);
        assertDeserializeSerialize(0b0101010100000001, 8, false, 1);
        assertDeserializeSerialize(0b0101010111111111, 8, false, 255);

        assertDeserializeSerialize(0b0101010111111111, 8, true, -1);
        assertDeserializeSerialize(0b0101010111111110, 8, true, -2);
        assertDeserializeSerialize(0b0101010110000000, 8, true, -128);
        assertDeserializeSerialize(0b0101010100000001, 8, true, 1);
        assertDeserializeSerialize(0b0101010101111111, 8, true, 127);
        assertDeserializeSerialize(0b0101010100000000, 8, true, 0);
    }

    @Test
    public void testDeserialize12Bits() throws Exception {
        assertDeserializeSerialize(0b00000000, 0b00000000, 12, false, 0);
        assertDeserializeSerialize(0b00000000, 0b00000001, 12, false, 1);
        assertDeserializeSerialize(0b00001111, 0b11111111, 12, false, 4095);

        assertDeserializeSerialize(0b00001111, 0b11111111, 12, true, -1);
        assertDeserializeSerialize(0b00001111, 0b11111110, 12, true, -2);
        assertDeserializeSerialize(0b00001000, 0b00000000, 12, true, -2048);
        assertDeserializeSerialize(0b00000000, 0b00000001, 12, true, 1);
        assertDeserializeSerialize(0b00000111, 0b11111111, 12, true, 2047);
        assertDeserializeSerialize(0b00000000, 0b00000000, 12, true, 0);
    }

    @Test
    public void testDeserialize16Bits() throws Exception {
        assertDeserializeSerialize(0b00000000, 0b00000000, 16, false, 0);
        assertDeserializeSerialize(0b00000000, 0b00000001, 16, false, 1);
        assertDeserializeSerialize(0b11111111, 0b11111111, 16, false, Short.MAX_VALUE * 2 + 1);

        assertDeserializeSerialize(0b11111111, 0b11111111, 16, true, -1);
        assertDeserializeSerialize(0b11111111, 0b11111110, 16, true, -2);
        assertDeserializeSerialize(0b10000000, 0b00000000, 16, true, Short.MIN_VALUE);
        assertDeserializeSerialize(0b00000000, 0b00000001, 16, true, 1);
        assertDeserializeSerialize(0b01111111, 0b11111111, 16, true, Short.MAX_VALUE);
        assertDeserializeSerialize(0b00000000, 0b00000000, 16, true, 0);
    }

    @Test
    public void testDeserialize24Bits() throws Exception {
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000000, 24, false, 0);
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000001, 24, false, 1);
        assertDeserializeSerialize(0b11111111, 0b11111111, 0b11111111, 24, false, 16777215);

        assertDeserializeSerialize(0b11111111, 0b11111111, 0b11111111, 24, true, -1);
        assertDeserializeSerialize(0b11111111, 0b11111111, 0b11111110, 24, true, -2);
        assertDeserializeSerialize(0b10000000, 0b00000000, 0b00000000, 24, true, -8388608);
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000001, 24, true, 1);
        assertDeserializeSerialize(0b01111111, 0b11111111, 0b11111111, 24, true, 8388607);
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000000, 24, true, 0);
    }

    @Test
    public void testDeserialize32Bits() throws Exception {
        assertDeserializeInteger(0b00000000, 0b00000000, 0b00000000, 0b00000000, 32, false, 0);
        assertDeserializeInteger(0b00000000, 0b00000000, 0b00000000, 0b00000001, 32, false, 1);
        assertDeserializeInteger(0b01111111, 0b11111111, 0b11111111, 0b11111111, 32, false, Integer.MAX_VALUE);
        assertDeserializeInteger(0b11111111, 0b11111111, 0b11111111, 0b11111111, 32, false, -1);
        assertDeserializeInteger(0b10000000, 0b00000000, 0b00000000, 0b00000000, 32, false, Integer.MIN_VALUE);

        assertDeserializeInteger(0b11111111, 0b11111111, 0b11111111, 0b11111111, 32, true, -1);
        assertDeserializeInteger(0b11111111, 0b11111111, 0b11111111, 0b11111110, 32, true, -2);
        assertDeserializeInteger(0b10000000, 0b00000000, 0b00000000, 0b00000000, 32, true, Integer.MIN_VALUE);
        assertDeserializeInteger(0b00000000, 0b00000000, 0b00000000, 0b00000001, 32, true, 1);
        assertDeserializeInteger(0b01111111, 0b11111111, 0b11111111, 0b11111111, 32, true, Integer.MAX_VALUE);
        assertDeserializeInteger(0b00000000, 0b00000000, 0b00000000, 0b00000000, 32, true, 0);

        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000000, 0b00000000, 32, false, 0);
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000000, 0b00000001, 32, false, 1);
        assertDeserializeSerialize(0b01111111, 0b11111111, 0b11111111, 0b11111111, 32, false, Integer.MAX_VALUE);
        assertDeserializeSerialize(0b11111111, 0b11111111, 0b11111111, 0b11111111, 32, false,
                ((long) Integer.MAX_VALUE) * 2L + 1L);

        assertDeserializeSerialize(0b11111111, 0b11111111, 0b11111111, 0b11111111, 32, true, -1);
        assertDeserializeSerialize(0b11111111, 0b11111111, 0b11111111, 0b11111110, 32, true, -2);
        assertDeserializeSerialize(0b10000000, 0b00000000, 0b00000000, 0b00000000, 32, true, Integer.MIN_VALUE);
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000000, 0b00000001, 32, true, 1);
        assertDeserializeSerialize(0b01111111, 0b11111111, 0b11111111, 0b11111111, 32, true, Integer.MAX_VALUE);
        assertDeserializeSerialize(0b00000000, 0b00000000, 0b00000000, 0b00000000, 32, true, 0);
    }

    @Test
    public void testDeserialize64Bits() throws Exception {
        BitSet bitSet = formatter.serialize(BigInteger.ONE, 64, false);
        assertEquals(1, bitSet.length());
        assertTrue(bitSet.get(0));

        BitSet bitSetBigInteger = formatter.serialize(BigInteger.valueOf(12345678), 64, false);
        BitSet bitSetInt = formatter.serialize(12345678, 64, false);
        assertEquals(bitSetInt, bitSetBigInteger);
    }

    private void assertDeserializeSerialize(int byte1, int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1);
        assertDeserializeSerialize(bitSet, size, signed, expected);
    }


    private void assertDeserializeSerialize(int byte1, int byte2, int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1, byte2);
        assertDeserializeSerialize(bitSet, size, signed, expected);
    }

    private void assertDeserializeSerialize(int byte1, int byte2, int byte3, int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1, byte2, byte3);
        assertDeserializeSerialize(bitSet, size, signed, expected);
    }

    private void assertDeserializeInteger(int byte1, int byte2, int byte3, int byte4,
            int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1, byte2, byte3, byte4);
        int result = formatter.deserializeInteger(bitSet, size, signed);
        assertEquals(expected, result);

        assertEquals(bitSet, formatter.serialize(result, size, signed));
        assertEquals(bitSet.get(0, size), formatter.serialize(result, size, signed));
    }

    private void assertDeserializeSerialize(int byte1, int byte2, int byte3, int byte4,
            int size, boolean signed, long expected) {
        BitSet bitSet = getBytes(byte1, byte2, byte3, byte4);

        long deserializedLong = formatter.deserializeLong(bitSet, size, signed);
        assertEquals(expected, deserializedLong);
        BigInteger deserializedBigInteger = formatter.deserializeBigInteger(bitSet, size, signed);
        assertEquals(expected, deserializedBigInteger.longValue());

        assertEquals(bitSet.get(0, size), formatter.serialize(deserializedLong, size, signed));
        assertEquals(bitSet.get(0, size), formatter.serialize(deserializedBigInteger, size, signed));
    }

    private void assertDeserializeSerialize(BitSet bitSet, int size, boolean signed, int expected) {
        int deserializedInteger = formatter.deserializeInteger(bitSet, size, signed);
        assertEquals(expected, deserializedInteger);
        long deserializedLong = formatter.deserializeLong(bitSet, size, signed);
        assertEquals(expected, deserializedLong);
        BigInteger deserializedBigInteger = formatter.deserializeBigInteger(bitSet, size, signed);
        assertEquals(expected, deserializedBigInteger.intValue());

        assertEquals(bitSet.get(0, size), formatter.serialize(deserializedInteger, size, signed));
        assertEquals(bitSet.get(0, size), formatter.serialize(deserializedLong, size, signed));
        assertEquals(bitSet.get(0, size), formatter.serialize(deserializedBigInteger, size, signed));
    }

    private BitSet getBytes(int byte1) {
        return BitSet.valueOf(new byte[] {(byte) byte1});
    }

    private BitSet getBytes(int byte1, int byte2) {
        return BitSet.valueOf(new byte[] {(byte) byte2, (byte) byte1});
    }

    private BitSet getBytes(int byte1, int byte2, int byte3) {
        return BitSet.valueOf(new byte[] {(byte) byte3, (byte) byte2, (byte) byte1});
    }

    private BitSet getBytes(int byte1, int byte2, int byte3, int byte4) {
        return BitSet.valueOf(new byte[] {(byte) byte4, (byte) byte3, (byte) byte2, (byte) byte1});
    }
}
