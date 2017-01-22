package org.bluetooth.gattparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.bluetooth.gattparser.spec.Characteristic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BluetoothGattParser {

    private final Logger logger = LoggerFactory.getLogger(GenericCharacteristicParser.class);

    private BluetoothGattSpecificationReader specificationReader;
    private final Map<String, CharacteristicParser> customParsers = new HashMap<>();
    private CharacteristicParser defaultParser;

    BluetoothGattParser(BluetoothGattSpecificationReader specificationReader, CharacteristicParser defaultParser) {
        this.specificationReader = specificationReader;
        this.defaultParser = defaultParser;
    }

    public GattResponse parse(String characteristicUUID, byte[] raw)
            throws CharacteristicFormatException {
        synchronized (customParsers) {
            if (!isValidForRead(characteristicUUID)) {
                throw new CharacteristicFormatException("Characteristic is not valid for read: " + characteristicUUID);
            }
            Characteristic characteristic = specificationReader.getCharacteristicByUUID(characteristicUUID);
            if (customParsers.containsKey(characteristicUUID)) {
                return new GattResponse(customParsers.get(characteristicUUID).parse(characteristic, raw));
            }
            return new GattResponse(defaultParser.parse(characteristic, raw));
        }
    }

    public GattRequest prepare(String characteristicUUID) {
        return new GattRequest(characteristicUUID,
                specificationReader.getFields(specificationReader.getCharacteristicByUUID(characteristicUUID)));
    }

    public byte[] serialize(GattRequest gattRequest) {
        return serialize(gattRequest, true);
    }

    public byte[] serialize(GattRequest gattRequest, boolean strict) {
        if (strict && !validate(gattRequest)) {
            throw new IllegalArgumentException("GATT request is not valid");
        }
        synchronized (customParsers) {
            String characteristicUUID = gattRequest.getCharacteristicUUID();
            if (strict && !isValidForWrite(gattRequest.getCharacteristicUUID())) {
                throw new CharacteristicFormatException(
                        "Characteristic is not valid for write: " + characteristicUUID);
            }
            if (customParsers.containsKey(characteristicUUID)) {
                return customParsers.get(characteristicUUID).serialize(gattRequest.getFieldHolders());
            }
            return defaultParser.serialize(gattRequest.getFieldHolders());
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

    public boolean isValidForWrite(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristicByUUID(characteristicUUID);
        return characteristic != null && characteristic.isValidForWrite();
    }

    public boolean validate(GattRequest gattRequest) {
        FieldHolder controlPointField = gattRequest.getControlPointFieldHolder();
        String requirement = controlPointField != null ? controlPointField.getWriteFlag() : null;

        if (requirement != null) {
            List<FieldHolder> required = gattRequest.getRequiredHolders(requirement);
            if (required.isEmpty()) {
                logger.info("GATT request is invalid; could not find any field by requirement: {}", requirement);
                return false;
            }
            for (FieldHolder holder : required) {
                if (!holder.isValueSet()) {
                    logger.info("GATT request is invalid; field is not set: {}", holder.getField().getName());
                    return false;
                }
            }
        }

        for (FieldHolder holder : gattRequest.getRequiredHolders("Mandatory")) {
            if (!holder.isValueSet()) {
                logger.info("GATT request is invalid; field is not set: {}", holder.getField().getName());
                return false;
            }
        }
        return true;
    }

}
