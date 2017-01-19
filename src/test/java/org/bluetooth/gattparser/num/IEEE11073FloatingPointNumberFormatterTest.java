package org.bluetooth.gattparser.num;

import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IEEE11073FloatingPointNumberFormatterTest {

    private IEEE11073FloatingPointNumberFormatter formatter = new IEEE11073FloatingPointNumberFormatter();

    @Test
    public void testDeserializeSFloat() throws Exception {
        assertEquals(36.4,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b1111000101101100})), 0.00001);
        assertEquals(3.64,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b1110000101101100})), 0.00001);

        assertEquals(364,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0000000101101100})), 0.00001);
        assertEquals(3640,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0001000101101100})), 0.00001);

        assertEquals(-36.4,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b1111111010010100})), 0.00001);
        assertEquals(-364,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0000111010010100})), 0.00001);
        assertEquals(-3640,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{0b0001111010010100})), 0.00001);

        assertEquals(Float.NaN,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{IEEE11073FloatingPointNumberFormatter.SFLOAT_NaN})), 0.0);
        assertEquals(Float.NEGATIVE_INFINITY,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{IEEE11073FloatingPointNumberFormatter.SFLOAT_NEGATIVE_INFINITY})), 0.0);
        assertEquals(Float.POSITIVE_INFINITY,
                formatter.deserializeSFloat(BitSet.valueOf(new long[]{IEEE11073FloatingPointNumberFormatter.SFLOAT_POSITIVE_INFINITY})), 0.0);
    }

    @Test
    public void testDeserializeFloat() throws Exception {
        assertEquals(36.4,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b11111111000000000000000101101100})), 0.00001);
        assertEquals(3.64,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b11111110000000000000000101101100})), 0.00001);

        assertEquals(364,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b00000000000000000000000101101100})), 0.00001);
        assertEquals(3640,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b00000001000000000000000101101100})), 0.00001);

        assertEquals(-36.4,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b11111111111111111111111010010100})), 0.00001);
        assertEquals(-364,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b00000000111111111111111010010100})), 0.00001);
        assertEquals(-3640,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{0b00000001111111111111111010010100})), 0.00001);


        assertEquals(Float.NaN,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{IEEE11073FloatingPointNumberFormatter.FLOAT_NaN})), 0.0);
        assertEquals(Float.NEGATIVE_INFINITY,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{IEEE11073FloatingPointNumberFormatter.FLOAT_NEGATIVE_INFINITY})), 0.0);
        assertEquals(Float.POSITIVE_INFINITY,
                formatter.deserializeFloat(BitSet.valueOf(new long[]{IEEE11073FloatingPointNumberFormatter.FLOAT_POSITIVE_INFINITY})), 0.0);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeserializeDouble() throws Exception {
        formatter.deserializeDouble(BitSet.valueOf(new long[]{0b10L}));
    }

    @Test(expected = IllegalStateException.class)
    public void testSerializeSFloat() {
        formatter.serializeSFloat(0.0F);
    }

    @Test(expected = IllegalStateException.class)
    public void testSerializeFloat() {
        formatter.serializeFloat(0.0F);
    }

    @Test(expected = IllegalStateException.class)
    public void testSerializeDouble() {
        formatter.serializeDouble(0.0);
    }


}
