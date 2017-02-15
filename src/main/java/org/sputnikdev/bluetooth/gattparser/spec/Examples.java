package org.sputnikdev.bluetooth.gattparser.spec;

import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAliasType("Examples")
public class Examples {

    @XStreamImplicit
    private List<String> examples;

    public List<String> getExamples() {
        return Collections.unmodifiableList(examples);
    }

}
