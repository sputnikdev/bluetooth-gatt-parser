package org.sputnikdev.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class CharacteristicAccess {

    @XStreamAsAttribute
    private String name;
    @XStreamAsAttribute
    private String type;
    @XStreamAlias("InformativeText")
    private String informativeText;
    @XStreamAlias("Requirement")
    private String requirement;
    @XStreamImplicit
    private List<Properties> properties;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getInformativeText() {
        return informativeText;
    }

    public String getRequirement() {
        return requirement;
    }

    public List<Properties> getProperties() {
        return properties;
    }

}
