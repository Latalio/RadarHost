package com.la.radarhost.comlib;

public class MessageInfo {
    public byte[] payload;
    public byte[] status;

    public MessageInfo(byte[] payload, byte[] status) {
        this.payload = payload;
        this.status = status;
    }

    public MessageInfo(byte[] status) {
        this(null, status);
    }
}
