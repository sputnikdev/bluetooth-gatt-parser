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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sputnikdev.bluetooth.gattparser.spec.BluetoothGattSpecificationReader;
import org.sputnikdev.bluetooth.gattparser.spec.Characteristic;
import org.sputnikdev.bluetooth.gattparser.spec.Field;
import org.sputnikdev.bluetooth.gattparser.spec.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class encapsulates functionality for reading and writing Bluetooth GATT characteristics
 * in a user-friendly manner.
 * <br>It is capable of dealing with services and characteristics defined by
 * <a href="https://www.bluetooth.com/specifications/gatt">Bluetooth SIG</a> as well as user-defined services
 * and characteristics. A simple example of reading an "approved" GATT characteristic
 * (<a href="https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.battery_level.xml">Battery Level</a>) would be:
 * <pre>
 * {@code
 *
 * BluetoothGattParser parser = BluetoothGattParserFactory.getDefault();
 * String characteristicUUID = "2A19"; // battery level characteristic
 * String batteryLevelFieldName = "Level"; // name of a field in the characteristic
 * byte[] rawData = new byte[] { 51 }; // raw data received from a bluetooth device
 * parser.parse(characteristicUUID, rawData).get(batteryLevelFieldName).getInteger();
 * }
 * </pre>
 * <br>The parser can be extended with user-defined services and characteristics by adding corresponding specification
 * definitions in GATT XML files (See an example <a href="https://www.bluetooth.com/api/gatt/XmlFile?xmlFileName=org.bluetooth.characteristic.battery_level.xml">here</a>).
 * There are two options of doing so:
 * <ul>
 * <li>By adding GATT XML files into classpath directories: "ext/gatt/service" and "ext/gatt/characteristic".
 * The parser will load specification files from those directories automatically.</li>
 * <li>By loading GATT XML files via {@link BluetoothGattParser#loadExtensionsFromFolder} method</li>
 * </ul>
 * <br>The parser can be also extended with a custom characteristic parser,
 * see {@link  BluetoothGattParser#registerParser(String, CharacteristicParser)}.
 *
 * @author Vlad Kolotov
 */
public class BluetoothGattParser {

    private final Logger logger = LoggerFactory.getLogger(GenericCharacteristicParser.class);

    private BluetoothGattSpecificationReader specificationReader;
    private final Map<String, CharacteristicParser> customParsers = new HashMap<>();
    private CharacteristicParser defaultParser;

    BluetoothGattParser(BluetoothGattSpecificationReader specificationReader, CharacteristicParser defaultParser) {
        this.specificationReader = specificationReader;
        this.defaultParser = defaultParser;
    }

    /**
     * Checks whether a provided characteristic UUID is known by the parser.
     * @param characteristicUUID UUID of a GATT characteristic
     * @return true if the parser has loaded definitions for that characteristic, false otherwise
     */
    public boolean isKnownCharacteristic(String characteristicUUID) {
        return specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID)) != null;
    }

    /**
     * Checks whether a provided service UUID is known by the parser.
     * @param serviceUUID UUID of a GATT service
     * @return true if the parser has loaded definitions for that service, false otherwise
     */
    public boolean isKnownService(String serviceUUID) {
        return specificationReader.getService(getShortUUID(serviceUUID)) != null;
    }

    /**
     * Performs parsing of a GATT characteristic value (byte array) into a user-friendly format
     * (a map of parsed characteristic fields represented by {@link GattResponse}).
     *
     * @param characteristicUUID UUID of a GATT characteristic
     * @param raw byte array of data received from bluetooth device
     * @return a map of parsed characteristic fields represented by {@link GattResponse}
     * @throws CharacteristicFormatException if a characteristic cannot be parsed
     */
    public GattResponse parse(String characteristicUUID, byte[] raw) throws CharacteristicFormatException {
        return new GattResponse(parseFields(characteristicUUID, raw));
    }

    /**
     * Returns a list of fields represented by {@link GattRequest} for a write operation
     * (see {@link BluetoothGattParser#serialize(GattRequest)}) of a specified GATT characteristic.
     * Some of the returned fields can be mandatory so they have to be set before serialization,
     * check {@link GattRequest#getRequiredFieldHolders()} and {@link BluetoothGattParser#validate(GattRequest)}
     *
     * @param characteristicUUID UUID of a GATT characteristic
     * @return list of fields represented by {@link GattRequest} for a write operation
     */
    public GattRequest prepare(String characteristicUUID) {
        characteristicUUID = getShortUUID(characteristicUUID);
        return new GattRequest(characteristicUUID,
                specificationReader.getFields(specificationReader.getCharacteristicByUUID(characteristicUUID)));
    }

    /**
     * Returns a list of fields represented by {@link GattRequest} for a write operation
     * (see {@link BluetoothGattParser#serialize(GattRequest)}) of a specified GATT characteristic which is to be
     * initialized with the provided initial data.
     * Some of the returned fields can be mandatory so they have to be set before serialization,
     * check {@link GattRequest#getRequiredFieldHolders()} and {@link BluetoothGattParser#validate(GattRequest)}
     *
     * @param characteristicUUID UUID of a GATT characteristic
     * @param initial initial data
     * @return list of fields represented by {@link GattRequest} for a write operation
     */
    public GattRequest prepare(String characteristicUUID, byte[] initial) {
        characteristicUUID = getShortUUID(characteristicUUID);
        return new GattRequest(characteristicUUID, parseFields(characteristicUUID, initial));
    }

    /**
     * Performs serialization of a GATT request prepared by {@link BluetoothGattParser#prepare(String)}
     * and filled by user (see {@link GattRequest#setField}) for a further communication to a bluetooth device.
     * Some of the fields can be mandatory so they have to be set before serialization,
     * check {@link GattRequest#getRequiredFieldHolders()} and {@link BluetoothGattParser#validate(GattRequest)}.
     *
     * @param gattRequest a GATT request object
     * @return serialized fields as an array of bytes ready to send to a bluetooth device
     * @throws IllegalArgumentException if provided GATT request is not valid
     */
    public byte[] serialize(GattRequest gattRequest) {
        return serialize(gattRequest, true);
    }

    /**
     * Performs serialization of a GATT request prepared by {@link BluetoothGattParser#prepare(String)}
     * and filled by user (see {@link GattRequest#setField}) for a further communication to a bluetooth device.
     * Some of the fields can be mandatory so they have to be set before serialization,
     * check {@link GattRequest#getRequiredFieldHolders()} and {@link BluetoothGattParser#validate(GattRequest)}.
     *
     * @param gattRequest a GATT request object
     * @param strict dictates whether validation has to be performed before serialization
     *               (see {@link BluetoothGattParser#validate(GattRequest)})
     * @return serialized fields as an array of bytes ready to send to a bluetooth device
     * @throws IllegalArgumentException if provided GATT request is not valid and strict parameter is set to true
     */
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
                return customParsers.get(characteristicUUID).serialize(gattRequest.getAllFieldHolders());
            }
            return defaultParser.serialize(gattRequest.getAllFieldHolders());
        }
    }

    /**
     * Returns a GATT service specification by its UUID.
     * @param serviceUUID UUID of a GATT service
     * @return a GATT service specification by its UUID
     */
    public Service getService(String serviceUUID) {
        return specificationReader.getService(getShortUUID(serviceUUID));
    }

    /**
     * Returns a GATT characteristic specification by its UUID.
     * @param characteristicUUID UUID of a GATT characteristic
     * @return a GATT characteristic specification by its UUID
     */
    public Characteristic getCharacteristic(String characteristicUUID) {
        return specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID));
    }

    /**
     * Returns a list of field specifications for a given characteristic.
     * Note that field references are taken into account. Referencing fields are not returned,
     * referenced fields returned instead (see {@link Field#getReference()}).
     *
     * @param characteristicUUID UUID of a GATT characteristic
     * @return a list of field specifications for a given characteristic
     */
    public List<Field> getFields(String characteristicUUID) {
        return specificationReader.getFields(getCharacteristic(getShortUUID(characteristicUUID)));
    }

    /**
     * Registers a new characteristic parser (see {@link CharacteristicParser}) for a given characteristic.
     * @param characteristicUUID UUID of a GATT characteristic
     * @param parser a new instance of a characteristic parser
     */
    public void registerParser(String characteristicUUID, CharacteristicParser parser) {
        synchronized (customParsers) {
            customParsers.put(getShortUUID(characteristicUUID), parser);
        }
    }

    /**
     * Checks whether a given characteristic is valid for read operation
     * (see {@link BluetoothGattParser#parse(String, byte[])}).
     * Note that not all standard and approved characteristics are valid for automatic read operations due to
     * malformed or incorrect GATT XML specification files.
     *
     * @param characteristicUUID UUID of a GATT characteristic
     * @return true if a given characteristic is valid for read operation
     */
    public boolean isValidForRead(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID));
        return characteristic != null && characteristic.isValidForRead();
    }

    /**
     * Checks whether a given characteristic is valid for write operation
     * (see {@link BluetoothGattParser#serialize(GattRequest)}).
     * Note that not all standard and approved characteristics are valid for automatic write operations due to
     * malformed or incorrect GATT XML specification files.
     *
     * @param characteristicUUID UUID of a GATT characteristic
     * @return true if a given characteristic is valid for write operation
     */
    public boolean isValidForWrite(String characteristicUUID) {
        Characteristic characteristic = specificationReader.getCharacteristicByUUID(getShortUUID(characteristicUUID));
        return characteristic != null && characteristic.isValidForWrite();
    }

    /**
     * Checks if a GATT request object has all mandatory fields set (see {@link BluetoothGattParser#prepare(String)}).
     *
     * @param gattRequest a GATT request object
     * @return true if a given GATT request is valid for write operation
     * (see {@link BluetoothGattParser#serialize(GattRequest)})
     */
    public boolean validate(GattRequest gattRequest) {
        FieldHolder controlPointField = gattRequest.getControlPointFieldHolder();
        String requirement = controlPointField != null ? controlPointField.getEnumerationValue() : null;

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

    /**
     * This method is used to load/register custom services and characteristics
     * (defined in GATT XML specification files,
     * see an example <a href="https://www.bluetooth.com/api/gatt/XmlFile?xmlFileName=org.bluetooth.characteristic.battery_level.xml">here</a>)
     * from a folder. The folder must contain two sub-folders for services and characteristics respectively:
     * "path"/service and "path"/characteristic. It is also possible to override existing services and characteristics
     * by matching UUIDs of services and characteristics in the loaded files.
     * @param path a root path to a folder containing definitions for custom services and characteristics
     */
    public void loadExtensionsFromFolder(String path) {
        specificationReader.loadExtensionsFromFolder(path);
    }

    /**
     * Returns text representation of the provided array of bytes. Example: [01, 05, ab]
     * @param raw bytes array
     * @param radix the radix to use in the string representation
     * @return array text representation
     */
    public String parse(byte[] raw, int radix) {
        String[] hexFormatted = new String[raw.length];
        int index = 0;
        for (byte b : raw) {
            String num = Integer.toUnsignedString(Byte.toUnsignedInt(b), radix);
            hexFormatted[index++] = ("00" + num).substring(num.length());
        }
        return Arrays.toString(hexFormatted);
    }

    /**
     * Serializes a string that represents an array of bytes (comma separated, e.g: [01, 05, ab]),
     * see ({@link #parse(byte[], int)}).
     * @param raw a string representing an array of bytes
     * @param radix the radix to use in the string representation
     * @return serialized array
     */
    public byte[] serialize(String raw, int radix) {
        String data = raw.replace("[", "").replace("]", "");
        String[] tokens = data.split(",");
        byte[] bytes = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            bytes[i] = (byte) (int) Integer.valueOf(tokens[i].trim(), radix);
        }
        return bytes;
    }

    private String getShortUUID(String uuid) {
        if (uuid.length() < 8) {
            return uuid.toUpperCase();
        }
        return Long.toHexString(Long.valueOf(uuid.substring(0, 8), 16)).toUpperCase();
    }

    private LinkedHashMap<String, FieldHolder> parseFields(String characteristicUUID, byte[] raw) {
        characteristicUUID = getShortUUID(characteristicUUID);
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

}
