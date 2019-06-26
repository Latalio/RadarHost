package com.la.radarhost.comlib;

import android.util.Log;

import com.la.radarhost.comlib.comport.driver.UsbSerialPort;
import com.la.radarhost.comlib.comport.util.HexDump;
import com.la.radarhost.comlib.protocol.Protocol;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class MessagePipeline extends Thread{
    private static String TAG = MessagePipeline.class.getSimpleName();

    // External interfaces
    public boolean stop = false;

    private Queue<CommandBytes> mCmdQueue = new LinkedList<>();
    private CommandBytes cCmd;  // current command


    private UsbSerialPort mPort;
    private int mTimeout = 200;

    private final int BUFFER_SIZE = 2048;
    private final byte[] mReadBuffer = new byte[BUFFER_SIZE];

    private final CheckBuffer mCheckBuffer = new CheckBuffer();


    private State mState = State.IDLE;

    private CheckProcess mCheckProcess = CheckProcess.header;
    private int checkLength;
    private final int READ_ROUNDS = 20;


    private enum State {
        STOPPED,
        IDLE,
        OPERATING
    }

    public MessagePipeline(UsbSerialPort port) {
        mPort = port;
    }

    @Override
    public void run() {
        for(;;) {
            step();
            if (stop) break;
        }
        Log.d(TAG, "<run>Thread end.");

    }

    private void step() {
        short count = 0;
        switch (mState) {
            case IDLE:
                if (mCmdQueue.isEmpty()) return;
                else {
                    cCmd = mCmdQueue.poll();
                    try {
                        mPort.write(cCmd.cmdBytes, mTimeout);
                    } catch (IOException e) {
                        Log.d(TAG, "IDLE Write failed.");
                    }
                    mCheckBuffer.clear();
                    mState = State.OPERATING;
                }
                break;
            case OPERATING:
                while (count < READ_ROUNDS) {
                    int recvLength;
                    try {
                        //
                        long st = System.currentTimeMillis();
                        recvLength = mPort.read(mReadBuffer, 100); // 10ms also OK.
                        long et = System.currentTimeMillis();
                        Log.d(TAG, "OPER Read " + recvLength + " bytes.");
                        Log.d(TAG, HexDump.toHexString(mReadBuffer,0,recvLength));
                        Log.d(TAG, "executed time: " + (et - st));

                        mCheckBuffer.put(mReadBuffer,0,recvLength);

                        if (checkMessage(mCheckBuffer.array(), cCmd.worker.getEpNum())) break;

                    } catch (IOException e) {
                        Log.d(TAG, "OPER Read failed.");
                    }

                    count++;
                }
                if (count==READ_ROUNDS) cCmd.worker.mState = ProtocolWorker.PayloadState.payload_invalid;
                mState = State.IDLE;
                break;
            case STOPPED:
                break;
        }

    }

    public synchronized boolean addCommand(CommandBytes commandBytes) {
        return mCmdQueue.offer(commandBytes);
    }

    private enum CheckProcess {
        header,
        length,
        tail
    }
    private boolean checkMessage(byte[] msgBytes, int epNum) {
        switch (mCheckProcess) {
            case header:
                if (msgBytes[0] == Protocol.CNST_STARTBYTE_DATA &&
                msgBytes[1] == (byte) epNum) {
                    checkLength =  (msgBytes[2] | msgBytes[3]<<8) + 4+2+4; //msg header + msg tail + status
                    mCheckProcess = CheckProcess.length;
                    Log.d("TAG", "header pass");
                    Log.d("TAG", "check length: " + checkLength);
                } else if (msgBytes[0] == Protocol.CNST_STARTBYTE_STATUS &&
                        msgBytes[1] == (byte) epNum && mCheckBuffer.length() == 4){
                    cCmd.worker.msgInfo = new MessageInfo(
                            Arrays.copyOfRange(msgBytes,mCheckBuffer.length()-2,mCheckBuffer.length())
                    );
                    cCmd.worker.mState = ProtocolWorker.PayloadState.payload_valid;
                    return true;
                } else {
                    break;
                }
            case length:
                Log.d("TAG", "checkbuf length: " + mCheckBuffer.length());
                if (mCheckBuffer.length() >= checkLength) {
                    mCheckProcess = CheckProcess.tail;
                    Log.d("TAG", "length pass");
                } else {
                    break;
                }
            case tail:
                int tail = mCheckBuffer.tail();
                if (msgBytes[tail-5] == (byte) Protocol.CNST_END_OF_PAYLOAD &&
                    msgBytes[tail-4] == (byte) (Protocol.CNST_END_OF_PAYLOAD>>8) &&
                    msgBytes[tail-3] == Protocol.CNST_STARTBYTE_STATUS &&
                    msgBytes[tail-2] == (byte) epNum
                ) {
                    Log.d("TAG", "tail pass");
                    cCmd.worker.msgInfo = new MessageInfo(
                            Arrays.copyOfRange(msgBytes,4,checkLength-6),   // msgTail(2)+status(4)=6;
                            Arrays.copyOfRange(msgBytes,checkLength-4,checkLength)
                    );
                    cCmd.worker.mState = ProtocolWorker.PayloadState.payload_valid;
                    mCheckProcess = CheckProcess.header; // reset check process
                    return true;
                }
        }
        return false;
    }







}
