package com.la.radar.protocol;

public class Message {
    public byte[] payload;
    public byte[] status;

    public Message(byte[] payload, byte[] status) {
        this.payload = payload;
        this.status = status;
    }

    public Message(byte[] status) {
        this(null, status);
    }
}
