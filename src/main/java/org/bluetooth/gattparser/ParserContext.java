package org.bluetooth.gattparser;

import org.bluetooth.gattparser.num.FloatingPointNumberFormatter;
import org.bluetooth.gattparser.num.RealNumberFormatter;
import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;

public class ParserContext {

    private final BluetoothGattSpecificationReader specificationReader;
    private final RealNumberFormatter realNumberFormatter;
    private final FloatingPointNumberFormatter floatingPointNumberFormatter;

    public ParserContext(BluetoothGattSpecificationReader specificationReader, RealNumberFormatter realNumberFormatter,
            FloatingPointNumberFormatter floatingPointNumberFormatter) {
        this.specificationReader = specificationReader;
        this.realNumberFormatter = realNumberFormatter;
        this.floatingPointNumberFormatter = floatingPointNumberFormatter;
    }

    public BluetoothGattSpecificationReader getSpecificationReader() {
        return specificationReader;
    }

    public RealNumberFormatter getRealNumberFormatter() {
        return realNumberFormatter;
    }

    public FloatingPointNumberFormatter getFloatingPointNumberFormatter() {
        return floatingPointNumberFormatter;
    }
}
