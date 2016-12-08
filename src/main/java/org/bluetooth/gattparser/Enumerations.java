package org.bluetooth.gattparser;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.bluetooth.gattparser.Enumeration;

@XStreamAlias("Enumerations")
public class Enumerations {

    @XStreamImplicit
    List<Enumeration> enumerations;

    public List<Enumeration> getEnumerations() {
        return enumerations;
    }

    public void setEnumerations(List<Enumeration> enumerations) {
        this.enumerations = enumerations;
    }
}
