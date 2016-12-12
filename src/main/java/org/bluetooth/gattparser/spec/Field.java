package org.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Field")
public class Field {

    @XStreamAsAttribute
    private String name;
    @XStreamAlias("InformativeText")
    private String informativeText;
    @XStreamAlias("Requirement")
    private String requirement;
    @XStreamAlias("Format")
    private String format;
    @XStreamAlias("BitField")
    private BitField bitField;
    @XStreamAlias("DecimalExponent")
    private Integer decimalExponent;
    @XStreamAlias("Unit")
    private String unit;
    @XStreamAlias("Minimum")
    private int minimum;
    @XStreamAlias("Maximum")
    private int maximum;
    @XStreamAlias("Enumerations")
    private Enumerations enumerations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformativeText() {
        return informativeText;
    }

    public void setInformativeText(String informativeText) {
        this.informativeText = informativeText;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public BitField getBitField() {
        return bitField;
    }

    public void setBitField(BitField bitField) {
        this.bitField = bitField;
    }

    public Integer getDecimalExponent() {
        return decimalExponent;
    }

    public void setDecimalExponent(Integer decimalExponent) {
        this.decimalExponent = decimalExponent;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public Enumerations getEnumerations() {
        return enumerations;
    }

    public void setEnumerations(Enumerations enumerations) {
        this.enumerations = enumerations;
    }
}
