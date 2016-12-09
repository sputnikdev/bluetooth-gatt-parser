package org.bluetooth.gattparser;

import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FieldFormatTest {

    @Test
    public void testBooleanGetValue() throws Exception {
        BitSet bitSet = getBytes((byte) 0b01010100, (byte) 1);
        assertTrue(FieldFormat.BOOLEAN.getValue(bitSet));

        bitSet = getBytes((byte) 0b01010100, (byte) 0);
        assertFalse(FieldFormat.BOOLEAN.getValue(bitSet));
    }

    @Test
    public void test2BitGetValue() throws Exception {
        assertFieldFormat(FieldFormat.TWOBIT);
    }

    @Test
    public void testNibbleGetValue() throws Exception {
        assertFieldFormat(FieldFormat.NIBBLE);
    }

    @Test
    public void testUint8GetValue() throws Exception {
        assertFieldFormat(FieldFormat.UINT8);
    }

    @Test
    public void testUint12GetValue() throws Exception {
        assertFieldFormat(FieldFormat.UINT12);
    }

    private void assertFieldFormat(FieldFormat format) {
        for (long i = 0; i < Math.pow(2, format.getSize()); i++) {
            BitSet bitSet = BitSet.valueOf(new long[] {i});
            assertEquals(i, (Number) format.getValue(bitSet));
        }
    }

    private BitSet getBytes(byte mask, byte value) {
        byte b = (byte) (mask | value);
        return BitSet.valueOf(new byte[] {b});
    }

    private String toBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }
}
