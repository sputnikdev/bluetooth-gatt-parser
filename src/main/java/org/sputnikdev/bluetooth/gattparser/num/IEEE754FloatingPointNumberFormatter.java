package org.sputnikdev.bluetooth.gattparser.num;

/*-
 * #%L
 * org.sputnikdev:bluetooth-gatt-parser
 * %%
 * Copyright (C) 2017 Sputnik Dev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.BitSet;

/**
 * IEEE754 floating point number formatter.
 * Stateless and threadsafe.
 *
 * @author Vlad Kolotov
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
