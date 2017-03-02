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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.sputnikdev.bluetooth.gattparser.spec.Field;

/**
 *
 * @author Vlad Kolotov
 */
public class GattRequest {

    private final String characteristicUUID;
    private final Map<String, FieldHolder> holders;
    private FieldHolder controlPointField;

    GattRequest(String characteristicUUID, List<Field> fields) {
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Fields cannot be empty");
        }
        this.characteristicUUID = characteristicUUID;
        this.holders = getHolders(fields);
        this.controlPointField = findControlPointField();
    }

    public FieldHolder getControlPointFieldHolder() {
        return controlPointField;
    }

    public boolean hasControlPoint() {
        return this.controlPointField != null;
    }

    public void setField(String name, Boolean value) {
        validate(name);
        holders.get(name).setBoolean(value);
    }

    public void setField(String name, Integer value) {
        validate(name);
        holders.get(name).setInteger(value);
    }

    public void setField(String name, Long value) {
        validate(name);
        holders.get(name).setLong(value);
    }

    public void setField(String name, BigInteger value) {
        validate(name);
        holders.get(name).setBigInteger(value);
    }

    public void setField(String name, Float value) {
        validate(name);
        holders.get(name).setFloat(value);
    }

    public void setField(String name, Double value) {
        validate(name);
        holders.get(name).setDouble(value);
    }

    public void setField(String name, String value) {
        validate(name);
        holders.get(name).setString(value);
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public Collection<FieldHolder> getFieldHolders() {
        return holders.values();
    }

    public FieldHolder getFieldHolder(String name) {
        validate(name);
        return holders.get(name);
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
     * Returns map of holders with preserved order of fields
     * @return map of holders with preserved order of fields
     */
    Map<String, FieldHolder> getHolders() {
        return holders;
    }

    private void validate(String name) {
        if (!this.holders.containsKey(name)) {
            throw new IllegalArgumentException("Unknown field: " + name);
        }
    }

    private FieldHolder findControlPointField() {
        FieldHolder firstField = holders.values().iterator().next();
        if (firstField.getField().getEnumerations() != null) {
            return firstField;
        }
        return null;
    }

    private Map<String, FieldHolder> getHolders(List<Field> fields) {
        Map<String, FieldHolder> result = new LinkedHashMap<>();
        int index = 0;
        for (Field field : fields) {
            result.put(field.getName(), new FieldHolder(field, index++));
        }
        return Collections.unmodifiableMap(result);
    }

}
