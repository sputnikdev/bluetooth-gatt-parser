package org.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Enumerations")
public class Enumerations {

    @XStreamImplicit
    private List<Enumeration> enumerations;
    @XStreamImplicit
    private List<Reserved> reserves;

    public List<Enumeration> getEnumerations() {
        return enumerations;
    }

    public void setEnumerations(List<Enumeration> enumerations) {
        this.enumerations = enumerations;
    }

    public List<Reserved> getReserves() {
        return reserves;
    }

    public void setReserves(List<Reserved> reserves) {
        this.reserves = reserves;
    }
}
