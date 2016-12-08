package org.bluetooth.gattparser;

import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FieldFormatTest {

    @Test
    public void testBooleanGetValue() throws Exception {
        byte[] bytes = new byte[] {0, 1, 0};
        System.out.println(toBinary(bytes));

        BitSet bitSet = BitSet.valueOf(bytes);

        bitSet = bitSet.get(8, 9);
        System.out.println(toBinary(bitSet.toByteArray()));
        assertTrue((Boolean) FieldFormat.BOOLEAN.getValue(bitSet));
    }

    private String toBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        return sb.toString();
    }
}
