package org.bluetooth.gattparser;

import java.math.BigInteger;

import org.bluetooth.gattparser.spec.Field;

/**
 * binary exponent: value * Math.pow(2, exponent)
 * decimal exponent: value * Math.pow(10, exponent)
 */
public class FieldHolder {

    private final Field field;
    private final Object value;

    public FieldHolder(Field field, Object value) {
        this.field = field;
        this.value = value;
    }

    public Field getField() {
        return field;
    }

    public boolean isNumber() {
        return field.getFormat().isNumber();
    }

    public boolean isBoolean() {
        return field.getFormat().isBoolean();
    }

    public boolean isString() {
        return field.getFormat().isString();
    }

    public boolean isStruct() {
        return field.getFormat().isStruct();
    }

    public boolean hasExponent() {
        return field.getBinaryExponent() != null || field.getDecimalExponent() != null;
    }

    public boolean isInteger() {
        return value instanceof Integer && !hasExponent();
    }

    public boolean isLong() {
        return value instanceof Long && !hasExponent();
    }

    public boolean isBigInteger() {
        return value instanceof BigInteger && !hasExponent();
    }

    public boolean isDecimal() {
        return isNumber() && (value instanceof Float || value instanceof Double || hasExponent());
    }

    public boolean isFloat() {
        return isDecimal() && value instanceof Float;
    }

    public boolean isDouble() {
        return isDecimal() && value instanceof Double;
    }

    public Integer getInteger() throws CharacteristicFormatException {
        if (isInteger()) {
            return field.getMultiplier() != null ? (Integer) value * field.getMultiplier() : (Integer) value;
        }
        throw getFormatException(Integer.class);
    }

    public Long getLong() throws CharacteristicFormatException {
        if (isLong()) {
            return field.getMultiplier() != null ? (Long) value * field.getMultiplier() : (Long) value;
        }
        throw getFormatException(Long.class);
    }

    public BigInteger getBigInteger() throws CharacteristicFormatException {
        if (isBigInteger()) {
            return field.getMultiplier() != null
                    ? ((BigInteger) value).multiply(BigInteger.valueOf(field.getMultiplier()))
                    : (BigInteger) value;
        }
        throw getFormatException(BigInteger.class);
    }

    public Float getFloat() throws CharacteristicFormatException {
        if (isFloat()) {
            return (float) ((float) value * getExponentMultiplier());
        }
        throw getFormatException(Float.class);
    }

    public Double getDouble() throws CharacteristicFormatException {
        if (isDouble()) {
            return (double) value * getExponentMultiplier();
        }
        throw getFormatException(Double.class);
    }

    public Boolean getBoolean() throws CharacteristicFormatException {
        if (isBoolean()) {
            return (Boolean) value;
        }
        throw getFormatException(Boolean.class);
    }

    public String getString() throws CharacteristicFormatException {
        if (isString()) {
            return (String) value;
        }
        throw getFormatException(String.class);
    }

    public Object getRawValue() {
        return value;
    }

    private double getExponentMultiplier() {
        double multiplier = 1;
        if (field.getDecimalExponent() != null) {
            multiplier = Math.pow(10, field.getDecimalExponent());
        }
        if (field.getBinaryExponent() != null) {
            multiplier *= Math.pow(2, field.getBinaryExponent());
        }
        if (field.getMultiplier() != null) {
            multiplier *= (double) field.getMultiplier();
        }
        return multiplier;
    }

    private CharacteristicFormatException getFormatException(Class clazz) {
        return new CharacteristicFormatException("Field cannot be cast to " + clazz.getName()
                + " " + "; field type: " + field.getFormat().getType() + "; value type: "
                + value.getClass().getName());
    }

}
