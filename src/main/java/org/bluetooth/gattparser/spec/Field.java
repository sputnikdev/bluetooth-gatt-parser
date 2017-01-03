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
    @XStreamAlias("Reference")
    private String reference;
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

    public String getInformativeText() {
        return informativeText;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public FieldFormat getFormat() {
        return FieldFormat.valueOf(format);
    }

    public BitField getBitField() {
        return bitField;
    }

    public Integer getDecimalExponent() {
        return decimalExponent;
    }

    public Integer getBinaryExponent() {
        return binaryExponent;
    }

    public Integer getMultiplier() {
        return multiplier;
    }

    public String getUnit() {
        return unit;
    }

    public Double getMinimum() {
        return minimum;
    }

    public Double getMaximum() {
        return maximum;
    }

    public Enumerations getEnumerations() {
        return enumerations;
    }

    public String getReference() {
        return reference;
    }
}
