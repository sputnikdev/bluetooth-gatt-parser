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

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Examples getExamples() {
        return examples;
    }

    public void setExamples(Examples examples) {
        this.examples = examples;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
