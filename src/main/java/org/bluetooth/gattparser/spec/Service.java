package org.bluetooth.gattparser.spec;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public InformativeText getInformativeText() {
        return informativeText;
    }

    public void setInformativeText(InformativeText InformativeText) {
        this.informativeText = InformativeText;
    }

    public Characteristics getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(Characteristics characteristics) {
        this.characteristics = characteristics;
    }
}
