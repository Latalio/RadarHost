package com.la.radarhost.comlib;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.la.radarhost.comlib.comport.util.HexDump;
import com.la.radarhost.comlib.endpoint.Endpoint;
import com.la.radarhost.comlib.protocol.Protocol;

import java.io.ByteArrayOutputStream;

public class ProtocolWorker extends Thread{
    private final String TAG = ProtocolWorker.class.getSimpleName();


    private Handler mHandler;
    private Endpoint mEndpoint;
    private Command mCmd;
    private MessagePipeline mMsgPipe;

    // external interface
    public boolean stop = false;
    public MessageInfo msgInfo;

    public enum PayloadState {
        not_received_yet,
        payload_valid,
        payload_invalid
    }
    public PayloadState mState = PayloadState.not_received_yet;

    public ProtocolWorker(Handler handler, Endpoint endpoint, Command command, MessagePipeline msgPipe) {
        mHandler = handler;
        mEndpoint = endpoint;
        mCmd = command;
        mMsgPipe = msgPipe;
    }

    @Override
    public void run() {
        // Send Command to MsgPipe
        byte[] cmdBytes = mEndpoint.commands.get(mCmd.cmd);
        if (cmdBytes == null) {
            Log.d(TAG, "<run>cmdBytes is null!");
            return;
        }

        if (!mCmd.repeat) {
            // Log.d(TAG, "<run>no repeat.");
            mMsgPipe.addCommand(new CommandBytes(this, wrapPayload(cmdBytes)));
            step();
        } else {
            Log.d(TAG, "<run>cycle.");
            for(;;) {
                mMsgPipe.addCommand(new CommandBytes(this, wrapPayload(cmdBytes)));
                step();
                Log.d(TAG, "<run>cycling.");
                try {
                    Thread.sleep(mCmd.interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (stop) break;
            }
        }

        Log.d(TAG, "<run>Thread end");
    }

    private void step() {
        // Receive Message from MsgPipe
        boolean loopContinue = true;
        while (loopContinue) {
            switch (mState) {
                case not_received_yet:
                    Log.d(TAG,"<step>not received yet");
                    break;
                case payload_valid:
                    Log.d(TAG,"<step>received and payload is valid");
                    // Parse Received Msg and Send Message to UI Thread
                    Message msg = Message.obtain();
                    mEndpoint.parsePayload(msgInfo.payload, msg);
                    mHandler.sendMessage(msg);

                    mState = PayloadState.not_received_yet;
                    loopContinue = false;
                    break;
                case payload_invalid:
                    Log.d(TAG,"<step>received and payload is invalid");
                    // Send Message to UI Thread
                    msg = Message.obtain();
                    // add msg
                    mHandler.sendMessage(msg);

                    mState = PayloadState.not_received_yet;
                    loopContinue = false;
                    break;
            }
        }
    }


    public int getEpNum() {
        return mEndpoint.epNum;
    }



    private byte[] wrapPayload(byte[] payload) {
        byte[] msgHeader = new byte[4];
        byte[] msgTail = new byte[2];

        msgHeader[0] = Protocol.CNST_STARTBYTE_DATA;
        msgHeader[1] = (byte) mEndpoint.epNum;
        msgHeader[2] = (byte) payload.length;
        msgHeader[3] = (byte)(payload.length >> 8);

        msgTail[0] = (byte) Protocol.CNST_END_OF_PAYLOAD;
        msgTail[1] = (byte)(Protocol.CNST_END_OF_PAYLOAD >> 8);

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        byteOutStream.write(msgHeader,0, msgHeader.length);
        byteOutStream.write(payload,0, payload.length);
        byteOutStream.write(msgTail,0, msgTail.length);

        int len = msgHeader.length + payload.length + msgTail.length;
        // Log.d(TAG, "<wrap payload as> " + len + " bytes");
        Log.d(TAG, HexDump.toHexString(byteOutStream.toByteArray()));

        return byteOutStream.toByteArray();
    }
}
