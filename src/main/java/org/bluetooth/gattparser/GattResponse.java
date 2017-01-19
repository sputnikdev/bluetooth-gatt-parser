package org.bluetooth.gattparser;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GattResponse {

    private final LinkedHashMap<String, FieldHolder> holders;

    public GattResponse(LinkedHashMap<String, FieldHolder> holders) {
        this.holders = holders;
    }

    public Map<String, FieldHolder> getHolders() {
        return Collections.unmodifiableMap(holders);
    }

    public Set<String> getFieldNames() {
        return holders.keySet();
    }

    public Collection<FieldHolder> getFieldHolders() {
        return holders.values();
    }

    public FieldHolder get(String fieldName) {
        return holders.get(fieldName);
    }

    public int getSize() {
        return holders.size();
    }

    public boolean contains(String fieldName) {
        return holders.containsKey(fieldName);
    }

}
