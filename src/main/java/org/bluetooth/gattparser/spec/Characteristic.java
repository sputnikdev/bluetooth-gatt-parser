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

    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public InformativeText getInformativeText() {
        return informativeText;
    }

    public Value getValue() {
        return value;
    }

    public boolean isValidForRead() {
        return validForRead;
    }

    public void setValidForRead(boolean validForRead) {
        this.validForRead = validForRead;
    }
}
