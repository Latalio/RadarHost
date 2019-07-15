package com.la.radarhost.comlib;

import com.la.radarhost.comlib.endpoint.Endpoint;

public class Command {

    public final Endpoint endpoint;
    public byte[] bytes;
    public final int epNum;
    public final boolean repeat;
    public final int interval;

    public ProtocolWorker worker;


    /**
     * General Command constructor
     * @param cmd human-readable command name
     * @param repeat indicate that whether this cmd be executed
     * @param interval the repetition interval. it will be ignored when {@code repeat} is false.
     */
    public Command(Endpoint endpoint, String cmd, boolean repeat, int interval) {
        this.endpoint = endpoint;
        this.bytes = endpoint.getCommandBytes(cmd);
        this.epNum = endpoint.epNum;
        this.repeat = repeat;
        this.interval = repeat ? interval : 0;
    }

    public Command(Endpoint endpoint, String cmd) {
        this(endpoint, cmd, false, 0);
    }




}
