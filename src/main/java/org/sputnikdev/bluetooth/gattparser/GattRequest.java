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

import org.sputnikdev.bluetooth.gattparser.spec.BitField;
import org.sputnikdev.bluetooth.gattparser.spec.Enumeration;
import org.sputnikdev.bluetooth.gattparser.spec.Field;
import org.sputnikdev.bluetooth.gattparser.spec.FlagUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Defines an object for capturing field values of a Bluetooth GATT characteristic. A GattRequest provides some methods
 * for identifying mandatory and optional fields as well as some convenient methods for capturing field values.
 *
 * @author Vlad Kolotov
 */
public class GattRequest {

    private final String characteristicUUID;
    private final Map<String, FieldHolder> holders;
    private final FieldHolder opCodesHolder;

    /**
     * Creates a GATT request for a given GATT characteristic and its fields.
     * @param characteristicUUID an UUID of a characteristic
     * @param fields a list of characteristic fields
     */
    GattRequest(String characteristicUUID, List<Field> fields) {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty");
        }
        this.characteristicUUID = characteristicUUID;
        holders = getHolders(fields);
        opCodesHolder = findOpCodesField();
    }

    /**
     * Creates a GATT request for a given GATT characteristic and its field holders.
     * @param characteristicUUID an UUID of a characteristic
     * @param holders a list of characteristic field hodlers
     */
    GattRequest(String characteristicUUID, Map<String, FieldHolder> holders) {
        if (holders.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty");
        }
        this.characteristicUUID = characteristicUUID;
        this.holders = new HashMap<>(holders);
        opCodesHolder = findOpCodesField();
    }

    /**
     * Returns operational codes field holder (if exists) for a given GATT characteristic.
     * Normally this field is used to identify a list of mandatory fields based on its value and
     * control field GATT specification, see {@link Field#getEnumerations()}
     * and {@link FieldHolder#getEnumerationRequires()} and {@link GattRequest#getRequiredFieldHolders()}.
     * @return an operational codes field
     */
    public FieldHolder getOpCodesFieldHolder() {
        return opCodesHolder;
    }

    /**
     * Checks whether an OpCodes field exists.
     * @return true if an OpCodes field exists
     */
    public boolean hasOpCodesField() {
        return opCodesHolder != null;
    }

    /**
     * Sets a Boolean value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, Boolean value) {
        validate(name);
        holders.get(name).setBoolean(value);
    }

    /**
     * Sets an Integer value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, Integer value) {
        setField(name, FieldHolder::setInteger, value);
    }


    /**
     * Sets a Long value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, Long value) {
        setField(name, FieldHolder::setLong, value);
    }

    /**
     * Sets a BigInteger value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, BigInteger value) {
        setField(name, FieldHolder::setBigInteger, value);
    }

    /**
     * Sets a Float value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, Float value) {
        setField(name, FieldHolder::setFloat, value);
    }

    /**
     * Sets a Double value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, Double value) {
        setField(name, FieldHolder::setDouble, value);
    }

    /**
     * Sets the field value from the given enumeration (enumeration key).
     * @param name field name
     * @param value field value
     */
    public void setField(String name, Enumeration value) {
        setField(name, FieldHolder::setEnumeration, value);
    }

    /**
     * Sets a new struct value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, byte[] value) {
        setField(name, FieldHolder::setStruct, value);
    }

    /**
     * Sets a String value for a field by its name.
     * @param name field name
     * @param value field value
     */
    public void setField(String name, String value) {
        setField(name, FieldHolder::setString, value);
    }

    /**
     * Returns associated to this request GATT characteristic UUID.
     * @return GATT characteristic UUID
     */
    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    /**
     * Returns a list of all fields.
     * @return a list of all fields
     */
    public List<FieldHolder> getAllFieldHolders() {
        return new ArrayList<>(holders.values());
    }

    /**
     * Returns a list of mandatory fields only.
     * @return a list of mandatory fields only
     */
    public List<FieldHolder> getRequiredFieldHolders() {
        FieldHolder controlPointField = getOpCodesFieldHolder();
        String requirement = controlPointField != null ? controlPointField.getEnumerationRequires() : null;

        List<FieldHolder> required = new ArrayList<>();
        required.addAll(getRequiredHolders("Mandatory"));

        if (requirement != null) {
            required.addAll(getRequiredHolders(requirement));
        }

        return Collections.unmodifiableList(required);
    }

    /**
     * Returns a field holder by its field name.
     * @param name requested field name
     * @return a field holder
     */
    public FieldHolder getFieldHolder(String name) {
        validate(name);
        return holders.get(name);
    }

    /**
     * Checks whether a field is present in the request.
     * @param name field name
     * @return true if present, false otherwise
     */
    public boolean hasFieldHolder(String name) {
        return holders.containsKey(name);
    }

    List<FieldHolder> getRequiredHolders(String requirement) {
        List<FieldHolder> result = new ArrayList<>();
        for (FieldHolder holder : holders.values()) {
            if (holder.getField().getRequirements().contains(requirement)) {
                result.add(holder);
            }
        }
        return result;
    }

    /**
     * Returns map of holders with preserved order of fields.
     * @return map of holders with preserved order of fields
     */
    Map<String, FieldHolder> getHolders() {
        return holders;
    }

    private void validate(String name) {
        if (!holders.containsKey(name)) {
            throw new IllegalArgumentException("Unknown field: " + name);
        }
    }

    private FieldHolder findOpCodesField() {
        return holders.values().stream().filter(field -> FlagUtils.isOpCodesField(field.getField()))
                .findFirst().orElse(null);
    }

    private Map<String, FieldHolder> getHolders(List<Field> fields) {
        Map<String, FieldHolder> result = new LinkedHashMap<>();
        for (Field field : fields) {
            result.put(field.getName(), new FieldHolder(field));
        }
        return Collections.unmodifiableMap(result);
    }

    private void setOpCode(List<String> requirements) {
        if (requirements != null && !requirements.isEmpty()) {
            if (opCodesHolder != null) {
                BitField bitField = opCodesHolder.getField().getBitField();
                if (bitField == null) {
                    requirements.stream().filter(req -> !"Mandatory".equalsIgnoreCase(req))
                            .findFirst().ifPresent(requirement -> {
                                opCodesHolder.getField().getEnumerations().getEnumerations().stream()
                                        .filter(enm -> requirement.equalsIgnoreCase(enm.getRequires()))
                                        .findFirst().ifPresent(enm -> {
                                            opCodesHolder.setBigInteger(enm.getKey());
                                        });
                            });
                } else {
                    //TODO handle bitmap
                }
            }
        }
    }

    private <T> void setField(String name, BiConsumer<FieldHolder, T> setter, T value) {
        validate(name);
        FieldHolder fieldHolder = holders.get(name);
        setter.accept(fieldHolder, value);
        setOpCode(fieldHolder.getField().getRequirements());
    }

}
