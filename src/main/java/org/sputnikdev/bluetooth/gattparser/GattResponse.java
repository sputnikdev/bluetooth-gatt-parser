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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents result of Bluetooth GATT characteristic deserialization. Defines some useful methods for accessing
 * deserialized field values in a user-friendly manner.
 *
 * @author Vlad Kolotov
 */
public class GattResponse {

    private final LinkedHashMap<String, FieldHolder> holders;

    GattResponse(LinkedHashMap<String, FieldHolder> holders) {
        this.holders = holders;
    }

    /**
     * Returns field holders in this response as a Map (field name -&gt; field holder).
     * @return field holders
     */
    public Map<String, FieldHolder> getHolders() {
        return Collections.unmodifiableMap(holders);
    }

    /**
     * Returns a list of field names in this response.
     * @return a list of field names in this response
     */
    public Set<String> getFieldNames() {
        return holders.keySet();
    }

    /**
     * Returns a list of field holders in this response
     * @return a list of field holders in this response
     */
    public Collection<FieldHolder> getFieldHolders() {
        return holders.values();
    }

    /**
     * Returns a field holder by its field name
     * @param fieldName field name
     * @return a field holder
     */
    public FieldHolder get(String fieldName) {
        return holders.get(fieldName);
    }

    /**
     * Returns the number of fields in this response
     * @return the number of fields in this response
     */
    public int getSize() {
        return holders.size();
    }

    /**
     * Checks whether a field by its name exists in this response
     * @param fieldName field name
     * @return true if a requested fields exists, false otherwise
     */
    public boolean contains(String fieldName) {
        return holders.containsKey(fieldName);
    }

}
