package org.bluetooth.gattparser.spec;

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

    public void setRead(String read) {
        this.read = read;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public String getWriteWithoutResponse() {
        return writeWithoutResponse;
    }

    public void setWriteWithoutResponse(String writeWithoutResponse) {
        this.writeWithoutResponse = writeWithoutResponse;
    }

    public String getSignedWrite() {
        return signedWrite;
    }

    public void setSignedWrite(String signedWrite) {
        this.signedWrite = signedWrite;
    }

    public String getReliableWrite() {
        return reliableWrite;
    }

    public void setReliableWrite(String reliableWrite) {
        this.reliableWrite = reliableWrite;
    }

    public String getNotify() {
        return notify;
    }

    public void setNotify(String notify) {
        this.notify = notify;
    }

    public String getIndicate() {
        return indicate;
    }

    public void setIndicate(String indicate) {
        this.indicate = indicate;
    }

    public String getWritableAuxiliaries() {
        return writableAuxiliaries;
    }

    public void setWritableAuxiliaries(String writableAuxiliaries) {
        this.writableAuxiliaries = writableAuxiliaries;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String broadcast) {
        this.broadcast = broadcast;
    }
}
