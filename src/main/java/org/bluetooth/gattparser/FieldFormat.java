package org.bluetooth.gattparser;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FieldFormat {

    BOOLEAN(0x01, "boolean", 1, false, false) {
        @Override
        public Boolean getValue(BitSet bitSet) {
            return getBoolean(bitSet);
        }
    },

    TWOBIT(0x02, "2bit", 2, false, false),
    NIBBLE(0x03, "nubble", 4, false, false),
    UINT8(0x04, "uint8", 8, false, true),
    UINT12(0x05, "uint12", 12, false, true),
    UINT16(0x06, "uint16", 16, false, true),
    UINT24(0x07, "uint24", 24, false, true),
    UINT32(0x08, "uint32", 32, false, true),
    UINT48(0x09, "uint48", 48, false, true),
    UINT64(0x0A, "uint64", 64, false, true),
    UINT128(0x0B, "uint128", 128, false, true),
    UINTX(0xFF, "xbit", -1, false, true) {
        @Override
        public BigInteger getValue(BitSet bitSet) {
            return getBigInteger(bitSet);
        }
    },

    SINT8(0x0C, "sint8", 8, true, true),
    SINT12(0x0D, "sint12", 12, true, true),
    SINT16(0x0E, "sint16", 16, true, true),
    SINT24(0x0F, "sint24", 24, true, true),
    SINT32(0x10, "sint32", 32, true, true),
    SINT48(0x11, "sint48", 48, true, true),
    SINT64(0x12, "sint64", 64, true, true),
    SINT128(0x13, "sint128", 128, true, true),

    FLOAT32(0x14, "float32", 32, true, false) {
        @Override
        public Float getValue(BitSet bitSet) {
            return getFloat(bitSet, 32);
        }
    },
    FLOAT64(0x15, "float64", 64, true, false) {
        @Override
        public Double getValue(BitSet bitSet) {
            return getDouble(bitSet, 64);
        }
    },
    SFLOAT(0x16, "SFLOAT", 16, true, false) {
        @Override
        public Float getValue(BitSet bitSet) {
            return getFloat(bitSet, 16);
        }
    },
    FLOAT(0x17, "FLOAT", 32, true, false) {
        @Override
        public Float getValue(BitSet bitSet) {
            return getFloat(bitSet, 32);
        }
    },
    DUINT16(0x18, "duint16", 16, true, false) {
        @Override
        public Float getValue(BitSet bitSet) {
            return getFloat(bitSet, 16);
        }
    },

    UTF8S(0x19, "utf8s", 0, false, false) {
        @Override
        public String getValue(BitSet bitSet) {
            return getString(bitSet, "UTF-8");
        }
    },
    UTF16S(0x1A, "utf16s", 0, false, false) {
        @Override
        public String getValue(BitSet bitSet) {
            return getString(bitSet, "UTF-16");
        }
    },

    STRUCT(0x1B, "struct", 0, false, false) {
        @Override
        public Object getValue(BitSet bitSet) {
            throw new IllegalStateException("Not supported");
        }
    };

    private static final Map<String, FieldFormat> BY_NAME = Collections.unmodifiableMap(
        new HashMap<String, FieldFormat>() {{
        for (FieldFormat fieldFormat : FieldFormat.values()) {
            put(fieldFormat.getName(), fieldFormat);
        }
    }});

    private int format;
    private String name;
    private int size;
    private boolean signed;
    private boolean exponentValue;

    FieldFormat(int format, String name, int size, boolean signed, boolean exponentValue) {
        this.format = format;
        this.name = name;
        this.size = size;
        this.signed = signed;
        this.exponentValue = exponentValue;
    }

    public int getFormat() {
        return format;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isSigned() {
        return signed;
    }

    public boolean isExponentValue() {
        return exponentValue;
    }

    public static FieldFormat getFieldFormat(String name) {
        if (name == null) {
            throw new IllegalStateException();
        }
        if (BY_NAME.containsKey(name)) {
            return BY_NAME.get(name);
        } else if (name.toLowerCase().endsWith("bit")) {
            return UINTX;
        }
        throw new IllegalStateException("Unknown format: " + name);
    }

    public static int getSize(String formatName) {
        FieldFormat format = getFieldFormat(formatName);
        if (UINTX == format) {
            int size = Integer.parseInt(formatName.substring(0, formatName.length() - 3));
            if (size == 0) {
                throw new IllegalStateException("Could not parse field type: " + formatName);
            }
            return size;
        } else {
            return format.getSize();
        }
    }

    public <T> T getValue(BitSet bitSet) {
        bitSet = truncate(bitSet, size);
        if (signed) {
            if (size <= 32) {
                return (T) getInteger(bitSet, 32);
            } else if (size <= 64) {
                return (T) getLong(bitSet);
            } else {
                return (T) getBigInteger(bitSet);
            }
        } else {
            if (size < 32) {
                return (T) getInteger(bitSet, 32);
            } else if (size < 64) {
                return (T) getLong(bitSet);
            } else {
                return (T) getBigInteger(bitSet);
            }
        }
    }

    private static BitSet truncate(BitSet bitSet, int size) {
        if (size > 0) {
            bitSet = bitSet.get(0, size);
        }
        return bitSet;
    }

    private static Boolean getBoolean(BitSet bitSet) {
        return truncate(bitSet, 1).get(0);
    }

    private static Integer getInteger(BitSet bits, int size) {
        int value = 0;
        for (int i = 0; i < bits.length() && i < size; i++) {
            value += bits.get(i) ? (1 << i) : 0L;
        }
        return value;
    }

    private static Long getLong(BitSet bitSet) {
        return ByteBuffer.wrap(bitSet.toByteArray()).getLong();
    }

    private static BigInteger getBigInteger(BitSet bitSet) {
        return new BigInteger(bitSet.toByteArray());
    }

    private static Float getFloat(BitSet bitSet, int size) {
        bitSet = truncate(bitSet, size);
        return ByteBuffer.wrap(bitSet.toByteArray()).getFloat();
    }

    private static Double getDouble(BitSet bitSet, int size) {
        bitSet = truncate(bitSet, size);
        return ByteBuffer.wrap(bitSet.toByteArray()).getDouble();
    }

    private static String getString(BitSet bitSet, String encoding) {
        try {
            return new String(bitSet.toByteArray(), encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }




}
