package com.la.radarhost.comlib;

public class Command {
    public final String cmd;
    public final boolean repetition;
    public final int interval;

    public Command(String cmd, boolean repetition, int interval) {
        this.cmd = cmd;
        this.repetition = repetition;
        if (!repetition) this.interval = 0;
        else this.interval = interval;
    }

    public Command(String cmd) {
        this(cmd, false, 0);
    }


}
