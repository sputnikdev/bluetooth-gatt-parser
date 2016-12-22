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

    private BluetoothGattParserFactory() { }

    /**
     * Returns a default gatt parser based on:
     * <ul>
     * <li>Two's complement & little-endian real number formatter
     * {@link org.bluetooth.gattparser.num.TwosComplementNumberFormatter}
     * <li>IEEE754 floating point number formatter
     * {@link org.bluetooth.gattparser.num.IEEE754FloatingPointNumberFormatter}
     * <li>Classpath GATT specification reader
     * </ul>
     * @return default Bluetooth GATT parser
     */
    public static BluetoothGattParser getDefault() {
        return new BluetoothGattParser(new BluetoothGattSpecificationReader(), new GenericCharacteristicParser());
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
