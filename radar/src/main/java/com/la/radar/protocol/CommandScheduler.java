package com.la.radar.protocol;

import java.util.ArrayList;
import java.util.List;

public class CommandScheduler extends Thread {

    private List<Runnable> cmdList = new ArrayList<>();
    private int interval = 1000; // default 1Hz
    private boolean terminate = false;

    @Override
    public void run() {
        for(;!terminate;) {
            for(Runnable r : cmdList) {
                r.run();
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

    public synchronized void addCommand(Runnable r) {
        cmdList.add(r);
    }

    // todo how to remove a Runnable command

    public void terminate() {
        terminate = true;
    }
}
