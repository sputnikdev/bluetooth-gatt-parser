package org.bluetooth.gattparser.num;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Two's complement & little-endian number formatter. The most used formats in modern systems.
 */
public class TwosComplementNumberFormatter implements RealNumberFormatter {

    @Override
    public Integer deserializeInteger(BitSet bits, int size, boolean signed) {
        if (size > 32) {
            throw new IllegalArgumentException("size must be less or equal than 32");
        }

        boolean isNegative = signed && size > 1 && bits.get(size - 1);
        int value = isNegative ? -1 : 0;
        for (int i = 0; i < bits.length() && i < size; i++) {
            if (isNegative && !bits.get(i)) {
                value ^= 1 << i;
            } else if (!isNegative && bits.get(i)) {
                value |= 1 << i;
            }
        }
        return value;
    }

    @Override
    public Long deserializeLong(BitSet bits, int size, boolean signed) {
        if (size > 64) {
            throw new IllegalArgumentException("size must be less or equal than 64");
        }
        boolean isNegative = signed && size > 1 && bits.get(size - 1);
        long value = isNegative ? -1L : 0L;
        for (int i = 0; i < bits.length() && i < size; i++) {
            if (isNegative && !bits.get(i)) {
                value ^= 1L << i;
            } else if (!isNegative && bits.get(i)) {
                value |= 1L << i;
            }
        }
        return value;
    }

    @Override
    public BigInteger deserializeBigInteger(BitSet bits, int size, boolean signed) {
        boolean isNegative = signed && size > 1 && bits.get(size - 1);
        BigInteger value = isNegative ? BigInteger.ONE.negate() : BigInteger.ZERO;
        for (int i = 0; i < bits.length() && i < size; i++) {
            if (isNegative && !bits.get(i)) {
                value = value.clearBit(i);
            } else if (!isNegative && bits.get(i)) {
                value = value.setBit(i);
            }
        }
        return value;
    }

    @Override
    public BitSet serialize(Integer number, int size, boolean signed) {
        return null;
    }

    @Override
    public BitSet serialize(Long number, int size, boolean signed) {
        return null;
    }

    @Override
    public BitSet serialize(BigInteger number, int size, boolean signed) {
        return null;
    }
}
