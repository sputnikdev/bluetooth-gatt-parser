package org.bluetooth.gattparser;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.bluetooth.gattparser.spec.Characteristic;

public class BluetoothGattParser {

    private final BluetoothGattSpecificationReader specificationReader;
    private final Map<String, CharacteristicParser> customParsers = new HashMap<>();
    private final CharacteristicParser defaultParser;

    BluetoothGattParser(BluetoothGattSpecificationReader specificationReader, CharacteristicParser defaultParser) {
        this.specificationReader = specificationReader;
        this.defaultParser = defaultParser;
    }

    public LinkedHashMap<String, FieldHolder> parse(String characteristicUUID, byte[] raw)
            throws CharacteristicFormatException {
        synchronized (customParsers) {
            if (!isValidForRead(characteristicUUID)) {
                throw new CharacteristicFormatException("Characteristic is not valid for read: " + characteristicUUID);
            }
            Characteristic characteristic = specificationReader.getCharacteristicByUUID(characteristicUUID);
            if (customParsers.containsKey(characteristicUUID)) {
                return customParsers.get(characteristicUUID).parse(characteristic, raw);
            }
            return defaultParser.parse(characteristic, raw);
        }
    }

    public Characteristic getCharacteristic(String characteristicUUID) {
        return specificationReader.getCharacteristicByUUID(characteristicUUID);
    }

    public void registerParser(String characteristicUUID, CharacteristicParser parser) {
        synchronized (customParsers) {
            customParsers.put(characteristicUUID, parser);
        }
    }

    public boolean isValidForRead(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristicByUUID(characteristicUUID);
        return characteristic != null && characteristic.isValidForRead();
    }

}
