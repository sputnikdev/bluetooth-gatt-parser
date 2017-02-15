package org.sputnikdev.bluetooth.gattparser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sputnikdev.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.sputnikdev.bluetooth.gattparser.spec.Characteristic;
import org.sputnikdev.bluetooth.gattparser.spec.Field;
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

    public boolean isKnownCharacteristic(String characteristicUUID) {
        return specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID)) != null;
    }

    public GattResponse parse(String characteristicUUID, byte[] raw)
            throws CharacteristicFormatException {
        characteristicUUID = getShortUUID(characteristicUUID);
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
        characteristicUUID = getShortUUID(characteristicUUID);
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
            String characteristicUUID = getShortUUID(gattRequest.getCharacteristicUUID());
            if (strict && !isValidForWrite(characteristicUUID)) {
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
        return specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID));
    }

    public List<Field> getFields(String characteristicUUID) {
        return specificationReader.getFields(getCharacteristic(getShortUUID(characteristicUUID)));
    }

    public void registerParser(String characteristicUUID, CharacteristicParser parser) {
        synchronized (customParsers) {
            customParsers.put(getShortUUID(characteristicUUID), parser);
        }
    }

    public boolean isValidForRead(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID));
        return characteristic != null && characteristic.isValidForRead();
    }

    public boolean isValidForWrite(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID));
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

    public void loadExtensionsFromFolder(String path) {
        specificationReader.loadExtensionsFromFolder(path);
    }

    private String getShortUUID(String uuid) {
        if (uuid.length() < 8) {
            return uuid.toUpperCase();
        }
        return Long.toHexString(Long.valueOf(uuid.substring(0, 8), 16)).toUpperCase();
    }

}
