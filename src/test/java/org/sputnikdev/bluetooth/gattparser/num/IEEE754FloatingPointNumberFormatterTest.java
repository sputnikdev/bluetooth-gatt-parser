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
