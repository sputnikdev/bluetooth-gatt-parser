package org.bluetooth.gattparser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.bluetooth.gattparser.spec.Characteristic;
import org.bluetooth.gattparser.spec.Field;

public class BluetoothGattParser {

    private final BluetoothGattSpecificationReader specificationReader;
    private final Map<String, CharacteristicParser> customParsers = new HashMap<>();
    private final CharacteristicParser defaultParser;

    BluetoothGattParser(BluetoothGattSpecificationReader specificationReader, CharacteristicParser defaultParser) {
        this.specificationReader = specificationReader;
        this.defaultParser = defaultParser;
    }

    public Map<String, FieldHolder> parse(String characteristicUUID, byte[] raw) throws CharacteristicFormatException {
        synchronized (customParsers) {
            if (!isValidForRead(characteristicUUID)) {
                throw new CharacteristicFormatException("Characteristic is not valid for read: " + characteristicUUID);
            }
            Characteristic characteristic = specificationReader.getCharacteristic(characteristicUUID);
            if (customParsers.containsKey(characteristicUUID)) {
                return customParsers.get(characteristicUUID).parse(characteristic, raw);
            }
            return defaultParser.parse(characteristic, raw);
        }
    }

    public List<Field> getFields(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristic(characteristicUUID);
        if (characteristic != null && characteristic.getValue() != null) {
            return specificationReader.getCharacteristic(characteristicUUID).getValue().getFields();
        }
        return Collections.emptyList();
    }

    public void registerParser(String characteristicUUID, CharacteristicParser parser) {
        synchronized (customParsers) {
            customParsers.put(characteristicUUID, parser);
        }
    }

    public boolean isValidForRead(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristic(characteristicUUID);
        return characteristic != null && characteristic.isValidForRead();
    }

}
