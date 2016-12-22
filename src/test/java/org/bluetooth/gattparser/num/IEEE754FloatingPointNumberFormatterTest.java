package org.bluetooth.gattparser.num;

import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IEEE754FloatingPointNumberFormatterTest {

    private IEEE754FloatingPointNumberFormatter formatter = new IEEE754FloatingPointNumberFormatter();

    @Test
    public void testDeserializeSFloat() throws Exception {
        assertEquals(Float.intBitsToFloat(0b10000110100000000000001000001001),
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b1011011000001001})), 0.0);

        assertEquals(Float.intBitsToFloat(0b00000111100000000000000000000000),
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0011110000000000})), 0.0);

        assertEquals(Float.intBitsToFloat(0b00000000000000000000000000000001),
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0000000000000001})), 0.0);

        assertEquals(Float.intBitsToFloat(0b00000000000000000000001000000001),
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0000001000000001})), 0.0);

        assertEquals(Float.intBitsToFloat(0b10000000000000000000001000000001),
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b1000001000000001})), 0.0);
    }

    @Test
    public void testDeserializeFloat() throws Exception {
        assertEquals(1F, formatter.deserializeFloat(BitSet.valueOf(new long[]{Float.floatToIntBits(1F)})), 0.0);
        assertEquals(Float.MIN_VALUE, formatter.deserializeFloat(BitSet.valueOf(new long[]{1})), 0.0);
        assertEquals(Float.MAX_VALUE, formatter.deserializeFloat(
                BitSet.valueOf(new long[]{Float.floatToIntBits(Float.MAX_VALUE)})), 0.0);
    }

    @Test
    public void testDeserializeDouble() throws Exception {
        assertEquals(1F, formatter.deserializeDouble(BitSet.valueOf(new long[]{Double.doubleToLongBits(1F)})), 0.0);
        assertEquals(Double.MIN_VALUE, formatter.deserializeDouble(BitSet.valueOf(new long[]{1})), 0.0);
        assertEquals(Double.MAX_VALUE, formatter.deserializeDouble(
                BitSet.valueOf(new long[]{Double.doubleToLongBits(Double.MAX_VALUE)})), 0.0);
    }
}
