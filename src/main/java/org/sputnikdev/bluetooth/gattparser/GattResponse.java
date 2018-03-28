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

    private final Map<String, FieldHolder> explicit;
    private final Map<String, PrimitiveFieldHolder> primitive = new LinkedHashMap<>();
    private final Map<String, CompositeFieldHolder<?>> composite = new LinkedHashMap<>();


    GattResponse(LinkedHashMap<String, FieldHolder> holders) {
        explicit = Collections.unmodifiableMap(holders);
        explicit.forEach((name, holder) -> flatten(holder));
    }

    /**
     * Returns primitive field holders in this response as a Map (field name -&gt; field holder).
     * @return primitive field holders
     */
    public Map<String, PrimitiveFieldHolder> getHolders() {
        return Collections.unmodifiableMap(primitive);
    }

    /**
     * Returns explicit field holders in this response as a Map (field name -&gt; field holder).
     * @return primitive field holders
     */
    public Map<String, FieldHolder> getExplicitHolders() {
        return Collections.unmodifiableMap(explicit);
    }

    /**
     * Returns composite field holders in this response as a Map (field name -&gt; field holder).
     * @return primitive field holders
     */
    public Map<String, CompositeFieldHolder<?>> getCompositeHolders() {
        return Collections.unmodifiableMap(composite);
    }

    /**
     * Returns a list of primitive field names in this response.
     * @return a list of primitive field names in this response
     */
    public Set<String> getFieldNames() {
        return primitive.keySet();
    }

    /**
     * Returns a list of explicit field names in this response.
     * @return a list of explicit field names in this response
     */
    public Set<String> getExplicitFieldNames() {
        return explicit.keySet();
    }

    /**
     * Returns a list of composite field names in this response.
     * @return a list of composite field names in this response
     */
    public Set<String> getCompositeFieldNames() {
        return composite.keySet();
    }

    /**
     * Returns a list of composite field holders in this response.
     * @return a list of composite field holders in this response
     */
    public Collection<CompositeFieldHolder<?>> getCompositeFieldHolders() {
        return composite.values();
    }

    /**
     * Returns a list of explicit field holders in this response.
     * @return a list of explicit field holders in this response
     */
    public Collection<FieldHolder> getExplicitFieldHolders() {
        return explicit.values();
    }

    /**
     * Returns a list of primitive field holders in this response.
     * @return a list of primitive field holders in this response
     */
    public Collection<PrimitiveFieldHolder> getFieldHolders() {
        return primitive.values();
    }

    /**
     * Returns a primitive field holder by its field name.
     * @param fieldName field name
     * @return a primitive field holder
     */
    public PrimitiveFieldHolder get(String fieldName) {
        return primitive.get(fieldName);
    }

    /**
     * Returns an explicit field holder by its field name.
     * @param fieldName field name
     * @return an explicit field holder
     */
    public FieldHolder getExplicitHolder(String fieldName) {
        return explicit.get(fieldName);
    }

    /**
     * Returns an explicit field holder by its field name.
     * @param fieldName field name
     * @return an explicit field holder
     */
    public <T> CompositeFieldHolder<T> getCompositeHolder(String fieldName) {
        return (CompositeFieldHolder<T>) composite.get(fieldName);
    }

    /**
     * Returns the number of explicit fields in this response.
     * @return the number of explicit fields in this response
     */
    public int getExplicitSize() {
        return explicit.size();
    }

    /**
     * Returns the number of explicit fields in this response.
     * @return the number of explicit fields in this response
     */
    public int getCompositeSize() {
        return composite.size();
    }

    /**
     * Returns the number of primitive fields in this response.
     * @return the number of primitive fields in this response
     */
    public int getSize() {
        return primitive.size();
    }

    /**
     * Checks whether a primitive field by its name exists in this response.
     * @param fieldName field name
     * @return true if a requested fields exists, false otherwise
     */
    public boolean contains(String fieldName) {
        return primitive.containsKey(fieldName);
    }

    /**
     * Checks whether an explicit field by its name exists in this response.
     * @param fieldName field name
     * @return true if a requested fields exists, false otherwise
     */
    public boolean containsExplicit(String fieldName) {
        return explicit.containsKey(fieldName);
    }

    /**
     * Checks whether a composite field by its name exists in this response.
     * @param fieldName field name
     * @return true if a requested fields exists, false otherwise
     */
    public boolean containsComposite(String fieldName) {
        return composite.containsKey(fieldName);
    }

    private void flatten(FieldHolder holder) {
        String name = holder.getField().getName();
        if (holder.isPrimitive()) {
            primitive.put(name, holder.cast());
        } else {
            CompositeFieldHolder<?> compositeHolder = holder.cast();
            composite.put(name, compositeHolder);
            compositeHolder.getHolders().forEach(this::flatten);
        }
    }

}
