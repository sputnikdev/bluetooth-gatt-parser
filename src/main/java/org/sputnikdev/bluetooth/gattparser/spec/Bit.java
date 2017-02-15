package org.sputnikdev.bluetooth.gattparser.spec;

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

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public Enumerations getEnumerations() {
        return enumerations;
    }

    public String getFlag(byte value) {
        if (enumerations == null) {
            return null;
        }
        for (Enumeration enumeration : enumerations.getEnumerations()) {
            if (value == enumeration.getKey()) {
                return enumeration.getRequires();
            }
        }
        return null;
    }
}
