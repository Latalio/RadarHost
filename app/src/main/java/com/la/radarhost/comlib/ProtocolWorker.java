package com.la.radarhost.comlib;

import com.la.radarhost.comlib.protocol.Protocol;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class ProtocolWorker implements Runnable{
    private final String TAG = ProtocolWorker.class.getSimpleName();

    private final RadarEventListener mListener;
    private final MessagePipeline mMsgPipe;
    private final Command mCmd;

    private Message mMsg;
    private boolean finish = false;

    public ProtocolWorker(RadarEventListener listener, MessagePipeline msgPipe, Command cmd) {
        mListener = listener;
        mMsgPipe = msgPipe;

        cmd.bytes = wrapCommand(cmd.bytes);
        cmd.worker = this;
        mCmd = cmd;
    }

    public enum ReceivingState {
        NOT_RECEIVED_YET,
        VALID,
        INVALID
    }
    private ReceivingState mState = ReceivingState.NOT_RECEIVED_YET;


    @Override
    public void run() {
        if (mCmd.repeat) {
            for(;finish;) {
                step();

                try {
                    Thread.sleep(mCmd.interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } else {
            step();
        }
    }

    /**
     *
     */
    private void step() {
        // send command to MessagePipe's command queue
        mMsgPipe.addCommand(mCmd);
        boolean loop = true;

        // wait for message response
        RadarEvent event = new RadarEvent();
        for (;loop;) {
            switch (mState) {
                case NOT_RECEIVED_YET: break;
                case VALID:
                    mCmd.endpoint.parsePayload(mMsg.payload, event);
                    mListener.onRadarChanged(event);

                    mState = ReceivingState.NOT_RECEIVED_YET;
                    loop = false;
                    break;
                case INVALID:
                    event.type = RadarEvent.PAYLOAD_INVALID;

                    mState = ReceivingState.NOT_RECEIVED_YET;
                    loop = false;
                    break;
            }
        }
    }


    private byte[] wrapCommand(byte[] payload) {
        byte[] msgHeader = new byte[4];
        byte[] msgTail = new byte[2];

        msgHeader[0] = Protocol.CNST_STARTBYTE_DATA;
        msgHeader[1] = (byte) mCmd.epNum;
        msgHeader[2] = (byte) payload.length;
        msgHeader[3] = (byte)(payload.length >> 8);

        msgTail[0] = (byte) Protocol.CNST_END_OF_PAYLOAD;
        msgTail[1] = (byte)(Protocol.CNST_END_OF_PAYLOAD >> 8);

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        byteOutStream.write(msgHeader,0, msgHeader.length);
        byteOutStream.write(payload,0, payload.length);
        byteOutStream.write(msgTail,0, msgTail.length);

        return byteOutStream.toByteArray();
    }

    public void finish() {
        finish = true;
    }

    void setStateValid() {
        mState = ReceivingState.VALID;
    }

    void setStateInvalid() {
        mState = ReceivingState.INVALID;
    }

    void setMessage(Message msg) {
        mMsg = msg;
    }
}
