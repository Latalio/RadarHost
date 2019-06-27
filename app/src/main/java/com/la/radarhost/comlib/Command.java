package com.la.radarhost.comlib;

public class Command {
    public final String cmd;
    public final boolean repeat;
    public final int interval;

    /**
     * General Command constructor
     * @param cmd human-readable command name
     * @param repeat indicate that whether this cmd be executed
     * @param interval the repetition interval. it will be ignored when {@code repeat} is false.
     */
    public Command(String cmd, boolean repeat, int interval) {
        this.cmd = cmd;
        this.repeat = repeat;
        this.interval = repeat ? interval : 0;
    }

    public Command(String cmd) {
        this(cmd, false, 0);
    }


}
