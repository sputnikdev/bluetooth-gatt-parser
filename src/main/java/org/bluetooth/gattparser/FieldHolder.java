package org.bluetooth.gattparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.bluetooth.gattparser.spec.Field;
import org.bluetooth.gattparser.spec.FlagUtils;

public class FieldHolder {

    private final Field field;
    private final int index;
    private Object value;

    public FieldHolder(Field field, Object value, int index) {
        this.field = field;
        this.value = value;
        this.index = index;
    }

    public FieldHolder(Field field, int index) {
        this.field = field;
        this.index = index;
    }

    public Field getField() {
        return field;
    }

    public int getIndex() {
        return index;
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

    strictfp public Integer getInteger(Integer def) {
        Integer result = new IntegerConverter(def).convert(Integer.class, value);
        return result != null ? (int) Math.round(result * getMultiplier()) : null;
    }

    public Long getLong(Long def) {
        Long result = new LongConverter(def).convert(Long.class, value);
        return result != null ? Math.round(result * getMultiplier()) : null;
    }

    public BigInteger getBigInteger(BigInteger def) {
        BigDecimal result = new BigDecimalConverter(def).convert(BigDecimal.class, value);
        return result != null
                ? result.multiply(BigDecimal.valueOf(getMultiplier())).setScale(0, RoundingMode.HALF_UP).toBigInteger()
                : null;
    }

    public Float getFloat(Float def) {
        Float result = new FloatConverter(def).convert(Float.class, value);
        return result != null ? (float) (result * getMultiplier()) : null;
    }

    public Double getDouble(Double def) {
        Double result = new FloatConverter(def).convert(Double.class, value);
        return result != null ? result * getMultiplier() : null;
    }

    public Boolean getBoolean(Boolean def) {
        return new BooleanConverter(def).convert(Boolean.class, value);
    }

    public String getString(String def) throws CharacteristicFormatException {
        return new StringConverter(def).convert(String.class, value);
    }

    public Object getRawValue() {
        return value;
    }

    public String getWriteFlag() {
        return FlagUtils.getWriteFlag(field, getInteger(null));
    }

    public void setBoolean(Boolean value) {
        this.value = value;
    }

    public void setInteger(Integer value) {
        this.value = value;
    }

    public void setLong(Long value) {
        this.value = value;
    }

    public void setBigInteger(BigInteger value) {
        this.value = value;
    }

    public void setFloat(Float value) {
        this.value = value;
    }

    public void setDouble(Double value) {
        this.value = value;
    }

    public void setString(String value) {
        this.value = value;
    }

    public boolean isValueSet() {
        return value != null;
    }

    private double getMultiplier() {
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

}
