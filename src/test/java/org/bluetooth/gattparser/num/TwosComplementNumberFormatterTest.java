package org.bluetooth.gattparser.num;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TwosComplementNumberFormatterTest {

    private TwosComplementNumberFormatter formatter = new TwosComplementNumberFormatter();

    @Test
    public void testDeserialize1Bit() throws Exception {
        assertDeserialize(0b01010111, 1, false, 1);
        assertDeserialize(0b01010110, 1, false, 0);
        assertDeserialize(0b01010111, 1, true, 1);
        assertDeserialize(0b01010110, 1, true, 0);
    }

    @Test
    public void testDeserialize2Bits() throws Exception {
        //assertDeserialize(0b01010100000000, 2, false, 0);
        assertDeserialize(0b01010100000001, 2, false, 1);
        assertDeserialize(0b01010100000010, 2, false, 2);
        assertDeserialize(0b01010100000011, 2, false, 3);

        assertDeserialize(0b01010100000011, 2, true, -1);
        assertDeserialize(0b01010100000010, 2, true, -2);
        assertDeserialize(0b01010100000001, 2, true, 1);
        assertDeserialize(0b01010100000000, 2, true, 0);
    }

    @Test
    public void testDeserializeNibbleBits() throws Exception {
        assertDeserialize(0b010100000000, 4, false, 0);
        assertDeserialize(0b010100000001, 4, false, 1);
        assertDeserialize(0b010100001111, 4, false, 15);

        assertDeserialize(0b010100001111, 4, true, -1);
        assertDeserialize(0b010100001110, 4, true, -2);
        assertDeserialize(0b010100001000, 4, true, -8);
        assertDeserialize(0b010100000111, 4, true, 7);
        assertDeserialize(0b010100000000, 4, true, 0);
    }

    @Test
    public void testDeserialize8Bits() throws Exception {
        assertDeserialize(0b0101010100000000, 8, false, 0);
        assertDeserialize(0b0101010100000001, 8, false, 1);
        assertDeserialize(0b0101010111111111, 8, false, 255);

        assertDeserialize(0b0101010111111111, 8, true, -1);
        assertDeserialize(0b0101010111111110, 8, true, -2);
        assertDeserialize(0b0101010110000000, 8, true, -128);
        assertDeserialize(0b0101010100000001, 8, true, 1);
        assertDeserialize(0b0101010101111111, 8, true, 127);
        assertDeserialize(0b0101010100000000, 8, true, 0);
    }

    @Test
    public void testDeserialize12Bits() throws Exception {
        assertDeserialize(0b00000000, 0b00000000, 12, false, 0);
        assertDeserialize(0b00000000, 0b00000001, 12, false, 1);
        assertDeserialize(0b00001111, 0b11111111, 12, false, 4095);

        assertDeserialize(0b00001111, 0b11111111, 12, true, -1);
        assertDeserialize(0b00001111, 0b11111110, 12, true, -2);
        assertDeserialize(0b00001000, 0b00000000, 12, true, -2048);
        assertDeserialize(0b00000000, 0b00000001, 12, true, 1);
        assertDeserialize(0b00000111, 0b11111111, 12, true, 2047);
        assertDeserialize(0b00000000, 0b00000000, 12, true, 0);
    }

    @Test
    public void testDeserialize16Bits() throws Exception {
        assertDeserialize(0b00000000, 0b00000000, 16, false, 0);
        assertDeserialize(0b00000000, 0b00000001, 16, false, 1);
        assertDeserialize(0b11111111, 0b11111111, 16, false, Short.MAX_VALUE * 2 + 1);

        assertDeserialize(0b11111111, 0b11111111, 16, true, -1);
        assertDeserialize(0b11111111, 0b11111110, 16, true, -2);
        assertDeserialize(0b10000000, 0b00000000, 16, true, Short.MIN_VALUE);
        assertDeserialize(0b00000000, 0b00000001, 16, true, 1);
        assertDeserialize(0b01111111, 0b11111111, 16, true, Short.MAX_VALUE);
        assertDeserialize(0b00000000, 0b00000000, 16, true, 0);
    }

    @Test
    public void testDeserialize24Bits() throws Exception {
        assertDeserialize(0b00000000, 0b00000000, 0b00000000, 24, false, 0);
        assertDeserialize(0b00000000, 0b00000000, 0b00000001, 24, false, 1);
        assertDeserialize(0b11111111, 0b11111111, 0b11111111, 24, false, 16777215);

        assertDeserialize(0b11111111, 0b11111111, 0b11111111, 24, true, -1);
        assertDeserialize(0b11111111, 0b11111111, 0b11111110, 24, true, -2);
        assertDeserialize(0b10000000, 0b00000000, 0b00000000, 24, true, -8388608);
        assertDeserialize(0b00000000, 0b00000000, 0b00000001, 24, true, 1);
        assertDeserialize(0b01111111, 0b11111111, 0b11111111, 24, true, 8388607);
        assertDeserialize(0b00000000, 0b00000000, 0b00000000, 24, true, 0);
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

        assertDeserialize(0b00000000, 0b00000000, 0b00000000, 0b00000000, 32, false, 0);
        assertDeserialize(0b00000000, 0b00000000, 0b00000000, 0b00000001, 32, false, 1);
        assertDeserialize(0b01111111, 0b11111111, 0b11111111, 0b11111111, 32, false, Integer.MAX_VALUE);
        assertDeserialize(0b11111111, 0b11111111, 0b11111111, 0b11111111, 32, false,
                ((long) Integer.MAX_VALUE) * 2L + 1L);

        assertDeserialize(0b11111111, 0b11111111, 0b11111111, 0b11111111, 32, true, -1);
        assertDeserialize(0b11111111, 0b11111111, 0b11111111, 0b11111110, 32, true, -2);
        assertDeserialize(0b10000000, 0b00000000, 0b00000000, 0b00000000, 32, true, Integer.MIN_VALUE);
        assertDeserialize(0b00000000, 0b00000000, 0b00000000, 0b00000001, 32, true, 1);
        assertDeserialize(0b01111111, 0b11111111, 0b11111111, 0b11111111, 32, true, Integer.MAX_VALUE);
        assertDeserialize(0b00000000, 0b00000000, 0b00000000, 0b00000000, 32, true, 0);
    }

    @Test
    public void testDeserializeLong() throws Exception {
        System.out.println((byte)0b11111111);
        System.out.println((byte)0b11111111 & 0b11111111);
        System.out.println(Integer.toBinaryString((byte)0b11111111) + " " + Integer.toBinaryString(0b11111111));
        byte[] bytes = new byte[] {(byte)0b11111111, (byte) 0b10000000};
        BitSet bitSet = BitSet.valueOf(bytes);
        bitSet = bitSet.get(0, 8);
        System.out.println((short) (ByteBuffer.wrap(bitSet.toByteArray()).get() & 0xFF));
        System.out.println(new BigInteger(1, bitSet.get(0, 16).toByteArray()));
    }

    private void assertDeserialize(int byte1, int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1);
        assertEquals(expected, (int) formatter.deserializeInteger(bitSet, size, signed));
        assertEquals(expected, (long) formatter.deserializeLong(bitSet, size, signed));
        assertEquals(expected, formatter.deserializeBigInteger(bitSet, size, signed).intValue());
    }


    private void assertDeserialize(int byte1, int byte2, int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1, byte2);
        assertEquals(expected, (int) formatter.deserializeInteger(bitSet, size, signed));
        assertEquals(expected, (long) formatter.deserializeLong(bitSet, size, signed));
        assertEquals(expected, formatter.deserializeBigInteger(bitSet, size, signed).intValue());
    }

    private void assertDeserialize(int byte1, int byte2, int byte3, int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1, byte2, byte3);
        assertEquals(expected, (int) formatter.deserializeInteger(bitSet, size, signed));
        assertEquals(expected, (long) formatter.deserializeLong(bitSet, size, signed));
        assertEquals(expected, formatter.deserializeBigInteger(bitSet, size, signed).intValue());
    }

    private void assertDeserializeInteger(int byte1, int byte2, int byte3, int byte4,
            int size, boolean signed, int expected) {
        BitSet bitSet = getBytes(byte1, byte2, byte3, byte4);
        assertEquals(expected, (int) formatter.deserializeInteger(bitSet, size, signed));
    }

    private void assertDeserialize(int byte1, int byte2, int byte3, int byte4,
            int size, boolean signed, long expected) {
        BitSet bitSet = getBytes(byte1, byte2, byte3, byte4);
        assertEquals(expected, (long) formatter.deserializeLong(bitSet, size, signed));
        assertEquals(expected, formatter.deserializeBigInteger(bitSet, size, signed).longValue());
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
