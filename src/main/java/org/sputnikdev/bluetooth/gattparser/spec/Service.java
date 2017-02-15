package org.sputnikdev.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Service")
public class Service {

    @XStreamAsAttribute
    private String name;
    @XStreamAsAttribute
    private String uuid;
    @XStreamAsAttribute
    private String type;
    @XStreamAlias("InformativeText")
    private InformativeText informativeText;
    @XStreamAlias("Characteristics")
    private Characteristics characteristics;

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public InformativeText getInformativeText() {
        return informativeText;
    }

    public Characteristics getCharacteristics() {
        return characteristics;
    }

}
