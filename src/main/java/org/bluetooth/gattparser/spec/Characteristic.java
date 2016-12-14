package org.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Characteristic")
public class Characteristic {
    @XStreamAsAttribute
    private String name;
    @XStreamAsAttribute
    private String uuid;
    @XStreamAsAttribute
    private String type;
    @XStreamAlias("InformativeText")
    private InformativeText informativeText;
    @XStreamAlias("Value")
    private Value value;

    private boolean validForRead;

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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public boolean isValidForRead() {
        return validForRead;
    }

    void setValidForRead(boolean validForRead) {
        this.validForRead = validForRead;
    }
}
