package org.bluetooth.gattparser;

import org.bluetooth.gattparser.num.FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.IEEE11073FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.IEEE754FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.RealNumberFormatter;
import org.bluetooth.gattparser.num.TwosComplementNumberFormatter;
import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;

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
