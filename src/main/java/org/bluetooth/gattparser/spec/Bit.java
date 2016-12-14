package org.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Bit")
public class Bit {

    @XStreamAsAttribute
    private int index;
    @XStreamAsAttribute
    private int size;
    @XStreamAsAttribute
    private String name;
    @XStreamAlias("Enumerations")
    private Enumerations enumerations;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Enumerations getEnumerations() {
        return enumerations;
    }

    public void setEnumerations(Enumerations enumerations) {
        this.enumerations = enumerations;
    }

    public String getRequires(byte value) {
        for (Enumeration enumeration : enumerations.getEnumerations()) {
            if (String.valueOf(value).equals(enumeration.getKey())) {
                return enumeration.getRequires();
            }
        }
        return null;
    }
}
