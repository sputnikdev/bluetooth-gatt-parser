package org.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("Characteristics")
public class Characteristics {

    @XStreamImplicit(itemFieldName = "Characteristic")
    private List<CharacteristicAccess> characteristics;

    public List<CharacteristicAccess> getCharacteristics() {
        return characteristics;
    }

}
