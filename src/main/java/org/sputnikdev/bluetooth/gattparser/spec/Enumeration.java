package org.sputnikdev.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("Enumeration")
public class Enumeration {

    @XStreamAsAttribute
    private Integer key;
    @XStreamAsAttribute
    private String value;
    @XStreamAsAttribute
    private String requires;

    public Integer getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getRequires() {
        return requires;
    }

}
