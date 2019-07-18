package com.la.radarhost.comlib;

import java.util.LinkedList;
import java.util.Queue;

public class MessageParser extends Thread {
    private final String TAG = MessageParser.class.getSimpleName();

    private Queue<Command> mCmdQueue = new LinkedList<>();

    private RadarManager.RadarEventListener mListener;
    private boolean terminate = false;

    public MessageParser(RadarManager.RadarEventListener listener) {
        mListener = listener;
    }

    @Override
    public void run() {
        Command cmd;
        for(;!terminate;) {
            if ((cmd = mCmdQueue.poll()) != null) {
                // 1. parse
                // 2. callback
                if (cmd.msg == null) {
                    mListener.onEventOccurred(RadarEvent.EVENT_TIMEOUT);
                } else {
                    RadarEvent event = new RadarEvent();
                    if (cmd.msg.payload != null) {
                        cmd.ep.parsePayload(cmd.msg.payload, event);
                    }
                    cmd.ep.parseStatus(cmd.msg.status, event);
                    mListener.onEventOccurred(event);
                }
            }
        }
    }

    public synchronized boolean addCommand(Command cmd) {
        return mCmdQueue.offer(cmd);
    }

    public void terminate() {
        terminate = true;
    }
}
