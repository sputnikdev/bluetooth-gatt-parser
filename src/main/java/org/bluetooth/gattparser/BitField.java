package org.bluetooth.gattparser;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.bluetooth.gattparser.Bit;

@XStreamAlias("BitField")
public class BitField {

    @XStreamImplicit
    private List<Bit> bits;

    public List<Bit> getBits() {
        return bits;
    }

    public void setBits(List<Bit> bits) {
        this.bits = bits;
    }
}
