package org.bluetooth.gattparser.spec;

import java.util.Collections;
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
        return Collections.unmodifiableList(enumerations);
    }

    public List<Reserved> getReserves() {
        return Collections.unmodifiableList(reserves);
    }
}
