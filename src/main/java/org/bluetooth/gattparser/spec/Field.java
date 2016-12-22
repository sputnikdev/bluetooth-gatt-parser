package org.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Field")
public class Field {

    @XStreamAsAttribute
    private String name;
    @XStreamAlias("InformativeText")
    private String informativeText;
    @XStreamImplicit(itemFieldName = "Requirement")
    private List<String> requirements;
    @XStreamAlias("Format")
    private String format;
    @XStreamAlias("BitField")
    private BitField bitField;
    @XStreamAlias("DecimalExponent")
    private Integer decimalExponent;
    @XStreamAlias("BinaryExponent")
    private Integer binaryExponent;
    @XStreamAlias("Multiplier")
    private Integer multiplier;
    @XStreamAlias("Unit")
    private String unit;
    @XStreamAlias("Minimum")
    private Double minimum;
    @XStreamAlias("Maximum")
    private Double maximum;
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

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public FieldFormat getFormat() {
        return FieldFormat.valueOf(format);
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

    public Integer getBinaryExponent() {
        return binaryExponent;
    }

    public void setBinaryExponent(Integer binaryExponent) {
        this.binaryExponent = binaryExponent;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Integer multiplier) {
        this.multiplier = multiplier;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public Double getMaximum() {
        return maximum;
    }

    public void setMaximum(Double maximum) {
        this.maximum = maximum;
    }

    public Enumerations getEnumerations() {
        return enumerations;
    }

    public void setEnumerations(Enumerations enumerations) {
        this.enumerations = enumerations;
    }
}
