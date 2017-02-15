package org.sputnikdev.bluetooth.gattparser.spec;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Properties")
public class Properties {

    @XStreamAlias("Read")
    private String read;
    @XStreamAlias("Write")
    private String write;
    @XStreamAlias("WriteWithoutResponse")
    private String writeWithoutResponse;
    @XStreamAlias("SignedWrite")
    private String signedWrite;
    @XStreamAlias("ReliableWrite")
    private String reliableWrite;
    @XStreamAlias("Notify")
    private String notify;
    @XStreamAlias("Indicate")
    private String indicate;
    @XStreamAlias("WritableAuxiliaries")
    private String writableAuxiliaries;
    @XStreamAlias("Broadcast")
    private String broadcast;

    public String getRead() {
        return read;
    }

    public String getWrite() {
        return write;
    }

    public String getWriteWithoutResponse() {
        return writeWithoutResponse;
    }

    public String getSignedWrite() {
        return signedWrite;
    }

    public String getReliableWrite() {
        return reliableWrite;
    }

    public String getNotify() {
        return notify;
    }

    public String getIndicate() {
        return indicate;
    }

    public String getWritableAuxiliaries() {
        return writableAuxiliaries;
    }

    public String getBroadcast() {
        return broadcast;
    }

}
