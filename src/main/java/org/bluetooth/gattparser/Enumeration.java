package org.bluetooth.gattparser;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Enumeration")
public class Enumeration {

    @XStreamAsAttribute
    private String key;
    @XStreamAsAttribute
    private String value;
    @XStreamAsAttribute
    private String requires;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRequires() {
        return requires;
    }

    public void setRequires(String requires) {
        this.requires = requires;
    }
}
