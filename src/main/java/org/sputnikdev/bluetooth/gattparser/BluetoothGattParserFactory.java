package org.sputnikdev.bluetooth.gattparser;

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

import org.sputnikdev.bluetooth.gattparser.num.FloatingPointNumberFormatter;
import org.sputnikdev.bluetooth.gattparser.num.IEEE11073FloatingPointNumberFormatter;
import org.sputnikdev.bluetooth.gattparser.num.IEEE754FloatingPointNumberFormatter;
import org.sputnikdev.bluetooth.gattparser.num.RealNumberFormatter;
import org.sputnikdev.bluetooth.gattparser.num.TwosComplementNumberFormatter;
import org.sputnikdev.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;

/**
 *
 * @author Vlad Kolotov
 */
public class BluetoothGattParserFactory {

    private static final RealNumberFormatter TWOS_COMPLEMENT_NUMBER_FORMATTER =
            new TwosComplementNumberFormatter();
    private static final FloatingPointNumberFormatter IEEE_754_FLOATING_POINT_NUMBER_FORMATTER =
            new IEEE754FloatingPointNumberFormatter();
    private static final FloatingPointNumberFormatter IEEE_11073_FLOATING_POINT_NUMBER_FORMATTER =
            new IEEE11073FloatingPointNumberFormatter();

    private static BluetoothGattSpecificationReader reader;
    private static BluetoothGattParser defaultParser;

    private BluetoothGattParserFactory() { }

    synchronized public static BluetoothGattSpecificationReader getSpecificationReader() {
        if (reader == null) {
            reader = new BluetoothGattSpecificationReader();
        }
        return reader;
    }

    synchronized public static BluetoothGattParser getDefault() {
        if (defaultParser == null) {
            BluetoothGattSpecificationReader reader = getSpecificationReader();
            defaultParser = new BluetoothGattParser(reader, new GenericCharacteristicParser(reader));
        }
        return defaultParser;
    }

    public static RealNumberFormatter getTwosComplementNumberFormatter() {
        return TWOS_COMPLEMENT_NUMBER_FORMATTER;
    }

    public static FloatingPointNumberFormatter getIEEE754FloatingPointNumberFormatter() {
        return IEEE_754_FLOATING_POINT_NUMBER_FORMATTER;
    }

    public static FloatingPointNumberFormatter getIEEE11073FloatingPointNumberFormatter() {
        return IEEE_11073_FLOATING_POINT_NUMBER_FORMATTER;
    }

}
