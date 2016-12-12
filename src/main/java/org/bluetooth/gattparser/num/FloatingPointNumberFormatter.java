package org.bluetooth.gattparser.num;

import java.util.BitSet;

public interface FloatingPointNumberFormatter {

    Float deserializeSFloat(BitSet bits);
    Float deserializeFloat(BitSet bits);
    Double deserializeDouble(BitSet bits);

    BitSet serializeSFloat(Float number);
    BitSet serializeFloat(Float number);
    BitSet serializeDouble(Double number);

}
