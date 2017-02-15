package org.sputnikdev.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("BitField")
public class BitField {

    @XStreamImplicit
    private List<Bit> bits;

    public List<Bit> getBits() {
        return bits;
    }

}
