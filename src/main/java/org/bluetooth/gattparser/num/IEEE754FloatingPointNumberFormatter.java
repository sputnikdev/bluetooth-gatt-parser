package org.bluetooth.gattparser.num;

import java.util.BitSet;

/**
 * IEEE754 floating point number formatter.
 * Stateless and threadsafe.
 */
public class IEEE754FloatingPointNumberFormatter implements FloatingPointNumberFormatter {

    @Override
    public Float deserializeSFloat(BitSet bits) {
        int mask = bits.get(15) ? Integer.MIN_VALUE : 0;
        BitSet exponent = bits.get(11, 15);
        mask |= exponent.length() > 0 ? exponent.toLongArray()[0] << 23 : 0;
        BitSet mantissa = bits.get(0, 11);
        mask |= mantissa.length() > 0 ? mantissa.toLongArray()[0] : 0;
        return Float.intBitsToFloat(mask);
    }

    @Override
    public Float deserializeFloat(BitSet bits) {
        int mask = bits.get(31) ? Integer.MIN_VALUE : 0;
        BitSet exponent = bits.get(23, 31);
        mask |= exponent.length() > 0 ? exponent.toLongArray()[0] << 23 : 0;
        BitSet mantissa = bits.get(0, 23);
        mask |= mantissa.length() > 0 ? mantissa.toLongArray()[0] : 0;
        return Float.intBitsToFloat(mask);
    }

    @Override
    public Double deserializeDouble(BitSet bits) {
        long mask = bits.get(63) ? Long.MIN_VALUE : 0L;
        BitSet exponent = bits.get(52, 63);
        mask |= exponent.length() > 0 ? exponent.toLongArray()[0] << 52 : 0;
        BitSet mantissa = bits.get(0, 52);
        mask |= mantissa.length() > 0 ? mantissa.toLongArray()[0] : 0;
        return Double.longBitsToDouble(mask);
    }

    @Override
    public BitSet serializeSFloat(Float number) {
        return null;
    }

    @Override
    public BitSet serializeFloat(Float number) {
        return null;
    }

    @Override
    public BitSet serializeDouble(Double number) {
        return null;
    }
}
