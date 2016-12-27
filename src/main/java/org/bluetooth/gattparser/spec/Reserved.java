package org.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAliasType("Reserved")
public class Reserved {

    @XStreamAsAttribute
    private int start;
    @XStreamAsAttribute
    private int end;

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

}
