package org.sputnikdev.bluetooth.gattparser.num;

import java.math.BigInteger;
import java.util.BitSet;

public interface RealNumberFormatter {

    Integer deserializeInteger(BitSet bits, int size, boolean signed);
    Long deserializeLong(BitSet bits, int size, boolean signed);
    BigInteger deserializeBigInteger(BitSet bits, int size, boolean signed);

    BitSet serialize(Integer number, int size, boolean signed);
    BitSet serialize(Long number, int size, boolean signed);
    BitSet serialize(BigInteger number, int size, boolean signed);

}
