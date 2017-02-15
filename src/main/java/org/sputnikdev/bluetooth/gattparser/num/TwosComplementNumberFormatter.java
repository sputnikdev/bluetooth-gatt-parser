package org.sputnikdev.bluetooth.gattparser.num;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Two's complement &amp; little-endian number formatter.
 * Stateless and threadsafe.
 */
public class TwosComplementNumberFormatter implements RealNumberFormatter {

    private final static int BIG_INTEGER_MAX_SIZE = 20 * 8;

    @Override
    public Integer deserializeInteger(BitSet bits, int size, boolean signed) {
        if (size > 32) {
            throw new IllegalArgumentException("size must be less or equal 32");
        }

        if (size == 1) {
            signed = false;
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

        if (size == 1) {
            signed = false;
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
        if (size == 1) {
            signed = false;
        }

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
        if (size == 1) {
            signed = false;
        }
        int length = Math.min(size, Integer.SIZE);
        BitSet bitSet = BitSet.valueOf(new long[] { number }).get(0, length);
        if (signed && number < 0) {
            bitSet.set(length - 1);
        }
        return bitSet;
    }

    @Override
    public BitSet serialize(Long number, int size, boolean signed) {
        if (size == 1) {
            signed = false;
        }
        int length = Math.min(size, Long.SIZE);
        BitSet bitSet = BitSet.valueOf(new long[] { number }).get(0, length);
        if (signed && number < 0) {
            bitSet.set(length - 1);
        }
        return bitSet;
    }

    @Override
    public BitSet serialize(BigInteger number, int size, boolean signed) {
        if (size == 1) {
            signed = false;
        }
        BitSet bitSet = new BitSet(size);

        int length = Math.min(size, BIG_INTEGER_MAX_SIZE);
        for (int i = 0; i < length - (signed ? 1 : 0); i++) {
            if (number.testBit(i)) {
                bitSet.set(i);
            }
        }
        if (signed && number.signum() == -1) {
            bitSet.set(length - 1);
        }
        return bitSet;
    }
}
