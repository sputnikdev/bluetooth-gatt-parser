package org.sputnikdev.bluetooth.gattparser.spec;

/*-
 * #%L
 * org.sputnikdev:bluetooth-gatt-parser
 * %%
 * Copyright (C) 2017 Sputnik Dev
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 *
 * @author Vlad Kolotov
 */
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
