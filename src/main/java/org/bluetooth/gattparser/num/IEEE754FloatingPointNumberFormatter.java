package org.bluetooth.gattparser.num;

import java.util.BitSet;

/**
 * IEEE754 floating point number formatter.
 * Stateless and threadsafe.
 */
public class IEEE754FloatingPointNumberFormatter implements FloatingPointNumberFormatter {

    @Override
    public Float deserializeSFloat(BitSet bits) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override
    public Float deserializeFloat(BitSet bits) {
        return Float.intBitsToFloat((int) bits.toLongArray()[0]);
    }

    @Override
    public Double deserializeDouble(BitSet bits) {
        return Double.longBitsToDouble(bits.toLongArray()[0]);
    }

    @Override
    public BitSet serializeSFloat(Float number) {
        throw new IllegalStateException("Operation not supported");
    }

    @Override
    public BitSet serializeFloat(Float number) {
        return BitSet.valueOf(new long[] { Float.floatToRawIntBits(number) });
    }

    @Override
    public BitSet serializeDouble(Double number) {
        return BitSet.valueOf(new long[] { Double.doubleToRawLongBits(number) });
    }
}
