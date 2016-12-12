package org.bluetooth.gattparser.spec;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAliasType("Examples")
public class Examples {

    @XStreamImplicit
    private List<String> examples;

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }
}
