package com.la.radarhost.comlib;

public class CommandBytes {
    public ProtocolWorker worker;
    public byte[] cmdBytes;


    public CommandBytes(ProtocolWorker worker, byte[] cmdBytes) {
        this.worker = worker;
        this.cmdBytes = cmdBytes;
    }
}
