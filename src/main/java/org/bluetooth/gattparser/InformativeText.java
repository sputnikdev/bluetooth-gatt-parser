package org.bluetooth.gattparser;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("InformativeText")
public class InformativeText {
    @XStreamAlias("Abstract")
    private String _abstract;
    @XStreamAlias("Summary")
    private String summary;

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
}
