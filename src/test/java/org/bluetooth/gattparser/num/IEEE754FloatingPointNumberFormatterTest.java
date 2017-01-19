package org.bluetooth.gattparser.num;

import java.util.BitSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IEEE754FloatingPointNumberFormatterTest {

    private IEEE754FloatingPointNumberFormatter formatter = new IEEE754FloatingPointNumberFormatter();

    @Test
    public void testDeserializeSerializeFloat() throws Exception {
        BitSet bitSet = BitSet.valueOf(new long[]{Float.floatToIntBits(1F)});
        Float deserialized = formatter.deserializeFloat(bitSet);
        assertEquals(1F, deserialized, 0.0);
        assertEquals(bitSet, formatter.serializeFloat(deserialized));

        bitSet = BitSet.valueOf(new long[]{1});
        deserialized = formatter.deserializeFloat(bitSet);
        assertEquals(Float.MIN_VALUE, deserialized, 0.0);
        assertEquals(bitSet, formatter.serializeFloat(deserialized));

        bitSet = BitSet.valueOf(new long[]{Float.floatToIntBits(Float.MAX_VALUE)});
        deserialized = formatter.deserializeFloat(bitSet);
        assertEquals(Float.MAX_VALUE, deserialized, 0.0);
        assertEquals(bitSet, formatter.serializeFloat(deserialized));
    }

    @Test
    public void testDeserializeSerializeDouble() throws Exception {
        BitSet bitSet = BitSet.valueOf(new long[]{Double.doubleToLongBits(1F)});
        Double deserialized = formatter.deserializeDouble(bitSet);
        assertEquals(1F, deserialized, 0.0);
        assertEquals(bitSet, formatter.serializeDouble(deserialized));

        bitSet = BitSet.valueOf(new long[]{1});
        deserialized = formatter.deserializeDouble(bitSet);
        assertEquals(Double.MIN_VALUE, deserialized, 0.0);
        assertEquals(bitSet, formatter.serializeDouble(deserialized));

        bitSet = BitSet.valueOf(new long[]{Double.doubleToLongBits(Double.MAX_VALUE)});
        deserialized = formatter.deserializeDouble(bitSet);
        assertEquals(Double.MAX_VALUE, deserialized, 0.0);
        assertEquals(bitSet, formatter.serializeDouble(deserialized));
    }

    @Test(expected = IllegalStateException.class)
    public void testSerializeSFloat() {
        formatter.serializeSFloat(0.0F);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeserializeSFloat() {
        formatter.deserializeSFloat(new BitSet());
    }

}
