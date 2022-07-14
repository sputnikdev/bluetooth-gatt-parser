package org.openhab.bluetooth.gattparser;

import org.openhab.bluetooth.gattparser.num.FloatingPointNumberFormatter;
import org.openhab.bluetooth.gattparser.num.IEEE11073FloatingPointNumberFormatter;
import org.openhab.bluetooth.gattparser.num.IEEE754FloatingPointNumberFormatter;
import org.openhab.bluetooth.gattparser.num.RealNumberFormatter;
import org.openhab.bluetooth.gattparser.num.TwosComplementNumberFormatter;
import org.openhab.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;

/**
 * A factory class for some main objects in the library:
 * {@link BluetoothGattParser}, {@link BluetoothGattSpecificationReader}.
 *
 * @author Vlad Kolotov
 */
public final class BluetoothGattParserFactory {

    private static final RealNumberFormatter TWOS_COMPLEMENT_NUMBER_FORMATTER =
            new TwosComplementNumberFormatter();
    private static final FloatingPointNumberFormatter IEEE_754_FLOATING_POINT_NUMBER_FORMATTER =
            new IEEE754FloatingPointNumberFormatter();
    private static final FloatingPointNumberFormatter IEEE_11073_FLOATING_POINT_NUMBER_FORMATTER =
            new IEEE11073FloatingPointNumberFormatter();

    private static volatile BluetoothGattSpecificationReader reader;
    private static volatile BluetoothGattParser defaultParser;

    private BluetoothGattParserFactory() { }

    /**
     * Returns GATT specification reader.
     *
     * @return GATT specification reader
     */
    public static BluetoothGattSpecificationReader getSpecificationReader() {
        if (reader == null) {
            synchronized (BluetoothGattParserFactory.class) {
                if (reader == null) {
                    reader = new BluetoothGattSpecificationReader();
                }
            }
        }
        return reader;
    }

    /**
     * Returns Bluetooth GATT parser.
     * @return Bluetooth GATT parser
     */
    public static BluetoothGattParser getDefault() {
        if (defaultParser == null) {
            synchronized (BluetoothGattParserFactory.class) {
                if (defaultParser == null) {
                    BluetoothGattSpecificationReader reader = getSpecificationReader();
                    defaultParser = new BluetoothGattParser(reader, new GenericCharacteristicParser(reader));
                }
            }
        }
        return defaultParser;
    }

    /**
     * Returns two's complement number formatter.
     * @return two's complement number formatter
     */
    public static RealNumberFormatter getTwosComplementNumberFormatter() {
        return TWOS_COMPLEMENT_NUMBER_FORMATTER;
    }

    /**
     * Returns IEEE754 floating point number formatter.
     * @return IEEE754 floating point number formatter
     */
    public static FloatingPointNumberFormatter getIEEE754FloatingPointNumberFormatter() {
        return IEEE_754_FLOATING_POINT_NUMBER_FORMATTER;
    }

    /**
     * Returns IEEE11073 floating point number formatter.
     * @return IEEE11073 floating point number formatter
     */
    public static FloatingPointNumberFormatter getIEEE11073FloatingPointNumberFormatter() {
        return IEEE_11073_FLOATING_POINT_NUMBER_FORMATTER;
    }

}
