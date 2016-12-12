package org.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Enumerations")
public class Enumerations {

    @XStreamImplicit
    private List<Enumeration> enumerations;
    @XStreamAlias("Reserved")
    private Reserved reserved;

    public List<Enumeration> getEnumerations() {
        return enumerations;
    }

    public void setEnumerations(List<Enumeration> enumerations) {
        this.enumerations = enumerations;
    }

    public Reserved getReserved() {
        return reserved;
    }

    public void setReserved(Reserved reserved) {
        this.reserved = reserved;
    }
}
