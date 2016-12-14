package org.bluetooth.gattparser.spec;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FieldFormat {

    public static final int FULL_SIZE = -1;

    private static final Map<String, FieldFormat> PREDEFINED = Collections.unmodifiableMap(
            new HashMap<String, FieldFormat>() {{
                put("boolean", new FieldFormat("boolean", FieldType.BOOLEAN, 1));
                put("nibble", new FieldFormat("nibble", FieldType.UINT, 4));
                put("float32", new FieldFormat("float32", FieldType.FLOAT, 32));
                put("float64", new FieldFormat("float64", FieldType.FLOAT, 64));
                put("sfloat", new FieldFormat("SFLOAT", FieldType.FLOAT, 16));
                put("float", new FieldFormat("FLOAT", FieldType.FLOAT, 32));
                put("duint16", new FieldFormat("duint16", FieldType.FLOAT, 16));
                put("utf8s", new FieldFormat("utf8s", FieldType.UTF8S, FULL_SIZE));
                put("utf16s", new FieldFormat("utf16s", FieldType.UTF16S, FULL_SIZE));
    }});

    private final String name;
    private final FieldType type;
    private final int size;

    FieldFormat(String name, FieldType type, int size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public static FieldFormat valueOf(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        String fieldName = name.toLowerCase();
        if (PREDEFINED.containsKey(fieldName)) {
            return PREDEFINED.get(fieldName);
        } else if (fieldName.startsWith("uint") || fieldName.endsWith("bit")) {
            return new FieldFormat(fieldName, FieldType.UINT, parseSize(fieldName));
        } else if (fieldName.startsWith("sint")) {
            return new FieldFormat(fieldName, FieldType.SINT, parseSize(fieldName));
        } else {
            throw new IllegalStateException("Unknown format type: " + name);
        }
    }

    public String getName() {
        return name;
    }

    public FieldType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    private static int parseSize(String name) {
        try {
            return Integer.parseInt(name.replace("uint", "").replace("sint", "").replace("bit", ""));
        } catch (NumberFormatException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
