package org.bluetooth.gattparser;

import org.bluetooth.gattparser.num.IEEE754FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.TwosComplementNumberFormatter;
import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;

public class BluetoothGattParserFactory {

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
        ParserContext parserContext = new ParserContext(new BluetoothGattSpecificationReader(),
                new TwosComplementNumberFormatter(),
                new IEEE754FloatingPointNumberFormatter());
        return new BluetoothGattParser(parserContext, new GenericCharacteristicParser());
    }

}
