package org.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("InformativeText")
public class InformativeText {
    @XStreamAlias("Abstract")
    private String _abstract;
    @XStreamAlias("Summary")
    private String summary;
    @XStreamAlias("Examples")
    private Examples examples;
    @XStreamAlias("Note")
    private String note;

    public String getAbstract() {
        return _abstract;
    }

    public String getSummary() {
        return summary;
    }

    public Examples getExamples() {
        return examples;
    }

    public String getNote() {
        return note;
    }

}
