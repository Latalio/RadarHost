package com.la.radarhost.comlib;

import com.la.radarhost.comlib.endpoint.Endpoint;

public class Command {

    public final Endpoint ep;
    public final byte[] bytes;

    public Message msg;

    public Command(Endpoint endpoint, byte[] cmdBytes) {
        this.ep = endpoint;
        this.bytes = cmdBytes;
    }
}
