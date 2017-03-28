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

import java.math.BigInteger;
import java.util.BitSet;

/**
 * Real number formatter interface. Defines methods for serialization and deserialization of real numbers.
 *
 * @author Vlad Kolotov
 */
public interface RealNumberFormatter {

    /**
     * Performs deserialization of a sequence of bits representing a real number into an Integer object
     * @param bits a sequence of bits representing a real number
     * @param size number of bits of a given sequence
     * @param signed indicates whether a given sequence represents a signed number
     * @return decoded number
     */
    Integer deserializeInteger(BitSet bits, int size, boolean signed);

    /**
     * Performs deserialization of a sequence of bits representing a real number into a Long object
     * @param bits a sequence of bits representing a real number
     * @param size number of bits of a given sequence
     * @param signed indicates whether a given sequence represents a signed number
     * @return decoded number
     */
    Long deserializeLong(BitSet bits, int size, boolean signed);

    /**
     * Performs deserialization of a sequence of bits representing a real number into a BigInteger object
     * @param bits a sequence of bits representing a real number
     * @param size number of bits of a given sequence
     * @param signed indicates whether a given sequence represents a signed number
     * @return decoded number
     */
    BigInteger deserializeBigInteger(BitSet bits, int size, boolean signed);

    /**
     * Performs serialization of a given real number encoded as an Integer object into a sequence of bits
     * @param number an Integer object
     * @param size number of bits of a given real number encoded as an Integer object
     * @param signed indicates whether a given real number is a signed number
     * @return serialized sequence of bits
     */
    BitSet serialize(Integer number, int size, boolean signed);

    /**
     * Performs serialization of a given real number encoded as a Long object into a sequence of bits
     * @param number a Long object
     * @param size number of bits of a given real number encoded as a Long object
     * @param signed indicates whether a given real number is a signed number
     * @return serialized sequence of bits
     */
    BitSet serialize(Long number, int size, boolean signed);

    /**
     * Performs serialization of a given real number encoded as a BigInteger object into a sequence of bits
     * @param number a BigInteger object
     * @param size number of bits of a given real number encoded as a BigInteger object
     * @param signed indicates whether a given real number is a signed number
     * @return serialized sequence of bits
     */
    BitSet serialize(BigInteger number, int size, boolean signed);

}
