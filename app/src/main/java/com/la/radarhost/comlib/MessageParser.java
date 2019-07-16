package com.la.radarhost.comlib;

import java.util.LinkedList;
import java.util.Queue;

public class MessageParser extends Thread {

    private Queue<Command> mCmdQueue = new LinkedList<>();

    private RadarEventListener mListener;
    private boolean finish = false;

    public MessageParser(RadarEventListener listener) {
        mListener = listener;
    }

    @Override
    public void run() {
        Command cmd;
        for(;finish;) {
            if ((cmd = mCmdQueue.poll()) != null) {
                // 1. parse
                // 2. callback
                if (cmd.msg == null) {
                    mListener.onRadarChanged(RadarEvent.EVENT_TIMEOUT);
                } else {
                    RadarEvent event = new RadarEvent();
                    cmd.ep.parsePayload(cmd.msg.payload, event);
                    mListener.onRadarChanged(event);
                }
            }
        }
    }

    public synchronized boolean addCommand(Command cmd) {
        return mCmdQueue.offer(cmd);
    }

    public void finish() {
        finish = true;
    }
}
