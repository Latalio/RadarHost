package com.la.radar.protocol;

import java.util.ArrayList;
import java.util.List;

public class CommandScheduler extends Thread {

    private final int LENGTH_CMD_LIST = 4;
    private Runnable[] cmdList = new Runnable[LENGTH_CMD_LIST];

    private int interval = 1000; // default 1Hz
    private boolean terminate = false;

    public static final int INDEX_TARGETS = 1;

    @Override
    public void run() {
        for(;!terminate;) {
            for(Runnable r : cmdList) {
                if (r != null) r.run();
            }

            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public synchronized void setCommand(int index, Runnable r) {
        cmdList[index] = r;
    }

    public boolean exists(int index) {
        return (index < LENGTH_CMD_LIST) && (cmdList[index] != null);
    }

    public void removeCommand(int index) {
        cmdList[index] = null;
    }

    public void terminate() {
        terminate = true;
    }
}
