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
        BitSet bitSet = BitSet.valueOf(new byte[] {0b00000000});
        assertEquals(0, (int) FieldFormat.TWOBIT.getValue(bitSet));

        bitSet = BitSet.valueOf(new byte[] {0b00000001});
        assertEquals(1, (int) FieldFormat.TWOBIT.getValue(bitSet));

        bitSet = BitSet.valueOf(new byte[] {0b00000011});
        assertEquals(3, (int) FieldFormat.TWOBIT.getValue(bitSet));

        // all bits greater than 2th should be skipped
        bitSet = BitSet.valueOf(new byte[] {0b00000100});
        assertEquals(0, (int) FieldFormat.TWOBIT.getValue(bitSet));
        bitSet = BitSet.valueOf(new long[] {0b11111100});
        assertEquals(0, (int) FieldFormat.TWOBIT.getValue(bitSet));
    }

    @Test
    public void testNibbleGetValue() throws Exception {
        BitSet bitSet = BitSet.valueOf(new byte[] {0b00000000});
        assertEquals(0, (int) FieldFormat.NIBBLE.getValue(bitSet));

        bitSet = BitSet.valueOf(new byte[] {0b00000001});
        assertEquals(1, (int) FieldFormat.NIBBLE.getValue(bitSet));

        bitSet = BitSet.valueOf(new byte[] {0b00001111});
        assertEquals(0b00001111, (int) FieldFormat.NIBBLE.getValue(bitSet));

        // all bits greater than 4th should be skipped
        bitSet = BitSet.valueOf(new byte[] {0b00010000});
        assertEquals(0, (int) FieldFormat.NIBBLE.getValue(bitSet));
        bitSet = BitSet.valueOf(new long[] {0b11110000});
        assertEquals(0, (int) FieldFormat.NIBBLE.getValue(bitSet));
    }

    @Test
    public void testUint8GetValue() throws Exception {
        BitSet bitSet = BitSet.valueOf(new byte[] {0b00000000});
        assertEquals(0, (int) FieldFormat.UINT8.getValue(bitSet));

        bitSet = BitSet.valueOf(new byte[] {0b00000001});
        assertEquals(1, (int) FieldFormat.UINT8.getValue(bitSet));

        bitSet = BitSet.valueOf(new long[] {0b10000000});
        assertEquals(0b10000000, (int) FieldFormat.UINT8.getValue(bitSet));
        bitSet = BitSet.valueOf(new long[] {0b11111111});
        assertEquals(0b11111111, (int) FieldFormat.UINT8.getValue(bitSet));
    }

    @Test
    public void testSint8GetValue() throws Exception {
        BitSet bitSet = BitSet.valueOf(new byte[] {0b00000000});
        assertEquals(0, (int) FieldFormat.SINT8.getValue(bitSet));

//        bitSet = BitSet.valueOf(new byte[] {0b00000001});
//        assertEquals(1, (int) FieldFormat.SINT8.getValue(bitSet));

//        bitSet = BitSet.valueOf(new long[] {0b10000000});
//        assertEquals(0b10000000, (int) FieldFormat.SINT8.getValue(bitSet));
        bitSet = BitSet.valueOf(new long[] {0b11111111});
        assertEquals(-1, (int) FieldFormat.SINT8.getValue(bitSet));
    }

    @Test
    public void testUint12GetValue() throws Exception {
        assertFieldFormatInteger(FieldFormat.UINT12);
    }

    @Test
    public void testUint16GetValue() throws Exception {
        assertFieldFormatInteger(FieldFormat.UINT16);
    }

    @Test
    public void testUint24GetValue() throws Exception {
        assertFieldFormatInteger(FieldFormat.UINT24);
    }

    @Test
    public void testUint32GetValue() throws Exception {
        BitSet bitSet = BitSet.valueOf(new long[] {0});
        assertEquals(0L, (long) FieldFormat.UINT32.getValue(bitSet));

        bitSet = BitSet.valueOf(new long[] {-1});
        assertEquals(0L, (long) FieldFormat.UINT32.getValue(bitSet));
    }

    private void assertFieldFormatInteger(FieldFormat format) {
        for (Integer i = 0; i < Math.pow(2, format.getSize()); i++) {
            BitSet bitSet = BitSet.valueOf(new long[] {i});
            assertEquals(i, format.getValue(bitSet));
        }
    }

    private void assertFieldFormatLong(FieldFormat format) {
        for (Long i = 0L; i < Math.pow(2, format.getSize()); i++) {
            BitSet bitSet = BitSet.valueOf(new long[] {i});
            assertEquals(i, format.getValue(bitSet));
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
