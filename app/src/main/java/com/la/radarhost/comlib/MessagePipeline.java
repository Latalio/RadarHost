package com.la.radarhost.comlib;

import android.util.Log;

import com.la.radarhost.comlib.comport.driver.UsbSerialPort;
import com.la.radarhost.comlib.protocol.Protocol;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MessagePipeline extends Thread{
    private static String TAG = MessagePipeline.class.getSimpleName();

    private boolean terminate = false;
    private boolean terminated = false;

    private Queue<Command> mCmdQueue = new LinkedList<>();
    private Command currCmd;  // current command

    private UsbSerialPort mPort;
    private int timeout = 200;


    private enum State {
        IDLE,
        OPERATING,
        TERMINATED
    }
    private State mState = State.IDLE;

    private final int BUFFER_SIZE = 2048;
    private final byte[] mReadBuffer = new byte[BUFFER_SIZE];

    private final CheckBuffer mCheckBuffer = new CheckBuffer();
    private enum CheckState {
        HEADER,
        LENGTH,
        TAIL
    }
    private CheckState mCheckState = CheckState.HEADER;
    private int checkLength;
    private final int READ_ROUNDS = 20;

    private MessageParser mParser;



    public MessagePipeline(UsbSerialPort port, RadarEventListener listener) {
        mPort = port;
        mParser = new MessageParser(listener);
        mParser.start();
    }

    @Override
    public void run() {
        for(;!terminated;) {
            switch (mState) {
                case IDLE: idle(); break;
                case OPERATING: operating(); break;
                case TERMINATED: terminated(); break;
                default: break;
            }
        }
    }

    private void idle() {
        if((currCmd = mCmdQueue.poll()) != null) {
            try {
                mPort.write(currCmd.bytes, timeout);
            } catch (IOException e) {
                Log.d(TAG, "IDLE Write failed.");
            }
            mCheckBuffer.clear();

            mState = State.OPERATING;
        }
    }

    private void operating() {
        short count = 0;
        while (count < READ_ROUNDS) {
            try {
                int recvLen = mPort.read(mReadBuffer, 100); // 10ms also OK.

                mCheckBuffer.put(mReadBuffer,0,recvLen);
                if (checkMessage(mCheckBuffer.array(), currCmd.ep.epNum)) break;
            } catch (IOException e) {
                Log.d(TAG, "OPER Read failed.");
            }
            count++;
        }

        mState = State.IDLE;
    }

    private void terminated() {
        if (terminate) {
            terminated = true;
            mParser.terminate();
        }

        mState = State.IDLE;
    }

    public synchronized boolean addCommand(Command cmd) {
        return mCmdQueue.offer(cmd);
    }

    private boolean checkMessage(byte[] msgBytes, int epNum) {
        switch (mCheckState) {
            case HEADER:
                if (msgBytes[0] == Protocol.CNST_STARTBYTE_DATA &&
                msgBytes[1] == (byte) epNum) {
                    checkLength =  (msgBytes[2] | msgBytes[3]<<8) + 4+2+4; //msg HEADER + msg TAIL + status
                    mCheckState = CheckState.LENGTH;
//                    Log.d("TAG", "HEADER pass");
//                    Log.d("TAG", "check LENGTH: " + checkLength);
                } else if (msgBytes[0] == Protocol.CNST_STARTBYTE_STATUS &&
                        msgBytes[1] == (byte) epNum && mCheckBuffer.length() == 4){
                    currCmd.msg = new Message(
                            Arrays.copyOfRange(msgBytes,mCheckBuffer.length()-2,mCheckBuffer.length())
                    );
                    mParser.addCommand(currCmd);
                    return true;
                } else {
                    break;
                }
            case LENGTH:
//                Log.d("TAG", "checkbuf LENGTH: " + mCheckBuffer.LENGTH());
                if (mCheckBuffer.length() >= checkLength) {
                    mCheckState = CheckState.TAIL;
                    Log.d("TAG", "LENGTH pass");
                } else {
                    break;
                }
            case TAIL:
                int tail = mCheckBuffer.tail();
                if (msgBytes[tail-5] == (byte) Protocol.CNST_END_OF_PAYLOAD &&
                    msgBytes[tail-4] == (byte) (Protocol.CNST_END_OF_PAYLOAD>>8) &&
                    msgBytes[tail-3] == Protocol.CNST_STARTBYTE_STATUS &&
                    msgBytes[tail-2] == (byte) epNum
                ) {
//                    Log.d("TAG", "TAIL pass");
                    currCmd.msg = new Message(
                            Arrays.copyOfRange(msgBytes,4,checkLength-6),   // msgTail(2)+status(4)=6;
                            Arrays.copyOfRange(msgBytes,checkLength-4,checkLength)
                    );
                    mParser.addCommand(currCmd);
                    mCheckState = CheckState.HEADER; // reset check process
                    return true;
                }
        }
        return false;
    }

    public void terminate() {
        terminate = true;
    }

    public void setTimeout(int timeoutMillis) {
        timeout = timeoutMillis;
    }









}
