package com.la.radarhost.comlib.protocol;

import com.la.radarhost.comlib.endpoint.Endpoint;

public class Protocol {
    private final String TAG = Protocol.class.getSimpleName();
    COMPort mPort;
    int mPortNumber = -1;

    // Protocol Instance Struct
    int mHandle = -1;
    private Endpoint[] devEndpoints;
    private int numEndpoints;

    public Protocol(COMPort port) {
        mPort = port;
    }

    public static final byte  CNST_STARTBYTE_DATA = 0x5A;
    public static final byte  CNST_STARTBYTE_STATUS = 0x5B;
    public static final short CNST_END_OF_PAYLOAD = (short) 0xE0DB;

    public static final byte CNST_MSG_QUERY_ENDPOINT_INFO = 0x00;
    private static final byte CNST_MSG_ENDPOINT_INFO = 0X00;
    private static final byte CNST_MSG_QUERY_FW_INFO = 0x01;
    private static final byte CNST_MSG_FW_INFO = 0x01;
    private static final byte CNST_MSG_FIRMWARE_RESET = 0x02;
    private static final int  CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG = 0x01000000;


    private static final int PROTOCOL_ERROR_ENDPOINT_NOT_EXIST = -2000;

    /**
     *  These error codes are returned when the connection could not be
     * established.
     */
    private static final int PROTOCOL_ERROR_CONNECTION_NOT_EXIST = -100;
    private static final int PROTOCOL_ERROR_COULD_NOT_OPEN_INTERFACE = -101;
    private static final int PROTOCOL_ERROR_COULD_NOT_OPEN_COM_PORT = -100;
    private static final int TIMEOUT = 100;
    private static final int PROTOCOL_ERROR_RECEIVED_NO_MESSAGE = -1000;
    private static final int PROTOCOL_ERROR_RECEIVED_TIMEOUT = -1001;
    private static final int PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_END = -1003;
    private static final int PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_START = -1002;
    private static final int PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE = -102;
    private static final int PROTOCOL_CONNECTED = 1;

    private static final int PROTOCOL_ERROR_INVALID_HANDLE = -1;
    private static final int PROTOCOL_ERROR_ENDPOINT_DOES_NOT_EXIST    = -2000;
    private static final int PROTOCOL_ERROR_ENDPOINT_WRONG_TYPE        = -2001;
    private static final int PROTOCOL_ERROR_ENDPOINT_VERSION_TOO_OLD   = -2002;
    private static final int PROTOCOL_ERROR_ENDPOINT_VERSION_TOO_NEW   = -2003;

    /**
     ===============================================================================================
        6. Local Functions
     ===============================================================================================
     */
//    private void recoverFromReceiveError() {
//        /* read until buffer is empty */
//        byte[] dummy_data = new byte[1024];
//        int received_bytes = dummy_data.LENGTH;
//        while (received_bytes == dummy_data.LENGTH) {
//            received_bytes = mPort.getData(dummy_data,0);
//        }
//        /* now we have run out of data, protocol should be in sync again */
//    }
//
//    private void sendMessage(int epNum, final byte[] payload) {
//        /* setup payload HEADER and TAIL */
//        byte[] msgHeader = new byte[4];
//        byte[] msgTail = new byte[2];
//
//        msgHeader[0] = CNST_STARTBYTE_DATA;
//        msgHeader[1] = (byte) epNum;
//        msgHeader[2] = (byte) payload.LENGTH;
//        msgHeader[3] = (byte)(payload.LENGTH >> 8);
//
//        msgTail[0] = (byte) CNST_END_OF_PAYLOAD;
//        msgTail[1] = (byte)(CNST_END_OF_PAYLOAD >> 8);
//
//        /* send payload */
//        mPort.sendData(msgHeader,0); // 4 Bytes
//        mPort.sendData(payload,0);   // payload size
//        mPort.sendData(msgTail,0);   // 2 Bytes
//    }
//
//    private int getMessage(Message msg) {
//        int numReceivedBytes;
//        byte[] msgHeader = new byte[4];
//
//        numReceivedBytes = mPort.getData(msgHeader,0);
//        if (numReceivedBytes < msgHeader.LENGTH) {
//            numReceivedBytes += mPort.getData(msgHeader, numReceivedBytes);
//        }                          // get again
//        if (numReceivedBytes == 0) return PROTOCOL_ERROR_RECEIVED_NO_MESSAGE;   // no msg
//        else if (numReceivedBytes < msgHeader.LENGTH)  {
//            recoverFromReceiveError();
//            return PROTOCOL_ERROR_RECEIVED_TIMEOUT;
//        }                    // timeout
//
//        if (msgHeader[0] == CNST_STARTBYTE_DATA) {
//            int payloadSize;
//            byte[] payload;
//            byte[] msgTail = new byte[2];
//
//            payloadSize = (int)msgHeader[2] | ((int)msgHeader[3])<<8;
//            Log.d(TAG, "payload size" + payloadSize);
//            payload = new byte[payloadSize];
//
//            numReceivedBytes = mPort.getData(payload,0);
//
//            /* check if payload has been received completely */
//            if (numReceivedBytes < payloadSize) {
//                recoverFromReceiveError();
//                return PROTOCOL_ERROR_RECEIVED_TIMEOUT;
//            }
//
//            /* check payload TAIL */
//            numReceivedBytes = mPort.getData(msgTail, 0);
//
//            if ((numReceivedBytes != msgTail.LENGTH) ||
//                    (msgTail[0] != (byte) CNST_END_OF_PAYLOAD) ||
//                    (msgTail[1] != (byte)(CNST_END_OF_PAYLOAD >> 8))) {
//                recoverFromReceiveError();
//                return PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_END;
//            }
//
//            msg.ep = msgHeader[1];
//            msg.payload = payload;
//
//            return CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG;
//        }
//        else if (msgHeader[0] == CNST_STARTBYTE_STATUS) {
//            short ep = msgHeader[1];
//            int status_code = (int) msgHeader[2] | ((int)msgHeader[3]) <<8;
//
//            return ((int)ep << 16) | status_code;
//        }
//        else {
//            recoverFromReceiveError();
//            return PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_START;
//        }
//    }

    /**
     ===============================================================================================
        7. Exported Functions
     ===============================================================================================
     */
    /**
     * 打开特定端口，并获取端点信息。
     */
//    public int connect() {
//        Message msg = new Message();
//        int statusCode;
//
//        mPortNumber = mPort.open();
//        if (mPortNumber < 0) {
//            Log.d(TAG, "port number less than 0");
//            return PROTOCOL_ERROR_COULD_NOT_OPEN_COM_PORT;
//        }
//
//        /* query ep information */
//        byte[] uQueryMessage = { CNST_MSG_QUERY_ENDPOINT_INFO };
//        sendMessage(0, uQueryMessage);
//
//        /* get msg and check validation */
//        statusCode = getMessage(msg);
//        if ((statusCode != CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG) ||
//                (msg.ep != 0) ||
//                (msg.payload.LENGTH <2 ) ||
//                (msg.payload[0] != CNST_MSG_ENDPOINT_INFO)) {
//            mPort.close();
//            Log.d(TAG, "status code error");
//            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
//        }
//
//        numEndpoints = msg.payload[1];
//        if ((msg.payload.LENGTH != 6*numEndpoints + 2) ||
//                (numEndpoints == 0)) {
//            mPort.close();
//            Log.d(TAG, "payload LENGTH error");
//            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
//        }
//
//        /* parse ep msg */
//        devEndpoints = new Endpoint[numEndpoints];
//        for(int i=0; i<numEndpoints; ++i) {
//            Endpoint ep = devEndpoints[i];
//            ep.type = readPayload(msg.payload, 2 + i*6, 4);
//            ep.version = (int)readPayload(msg.payload, 6 + i*6, 2);
//            ep.checkCompatibility();
//        }
//
//        /* check the status code */
//        statusCode = getMessage(msg);
//        if (statusCode != 0) {   // (/*ep*/0 << 16) | /*status code*/0x0000)
//            devEndpoints = null;
//            mPort.close();
//            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
//        }
//
//        /* register the mHandle in ProtocolManager */
//        mHandle = ProtocolManager.register(this);
//        return mHandle;
//    }
//
//    public void disconnect() {
//        if (ProtocolManager.isValid(this)) {
//            /* close COM Port */
//            mPort.close();
//
//            /* reset field */
//            devEndpoints = null;
//            ProtocolManager.unregister(this);
//        }
//    }

//    // be called by ep functions
//    public int sendAndReceive(Endpoint ep, byte[] payload) {
//        int statusCode;
//        Message messageInfo = new Message();
//        /* check mHandle and ep compatibility */
//        /* --------------------------------------- */
//        /* check mHandle */
//        if (ProtocolManager.isValid(this)) {
//            return PROTOCOL_ERROR_INVALID_HANDLE;
//        }
//
//        /* check if ep exists */
//        if (!existInDevEndpoints(ep)) {
//            return nonexistReason(ep);
//        }
//
//        /* send payload */
//        sendMessage(ep.epNum, payload);
//
//        /* receive payload from the board */
//        while ((statusCode = getMessage(messageInfo)) == CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG) {
//            ep.parsePayload(messageInfo);
//            messageInfo = null;
//        }
//
//        return statusCode;
//    }

    // return 0 means valid
//    private int nonexistReason(Endpoint ep) {
//        if (ep.type != ep.epHostDef.type)
//            return PROTOCOL_ERROR_ENDPOINT_WRONG_TYPE;
//        if (ep.version < ep.epHostDef.minVersion)
//            return PROTOCOL_ERROR_ENDPOINT_VERSION_TOO_OLD;
//        if (ep.version > ep.epHostDef.maxVersion)
//            return PROTOCOL_ERROR_ENDPOINT_VERSION_TOO_NEW;
//        return 0;
//    }
//
//    private boolean existInDevEndpoints(Endpoint ep) {
//        boolean hit = false;
//        for (Endpoint ep : devEndpoints) {
//            if (ep.epHostDef.equals(ep.epHostDef)) {
//                hit = true;
//                break;
//            }
//        }
//        return hit;
//    }


    /**
     ===============================================================================================
     7. Static Functions
     ===============================================================================================
     */
    public static long readPayload(final byte[] payload, int offset, int length) {
        // Little Endian high-vs-high, low-vs-low
        long value = 0L;
        for (int i=0; i<length; i++) {
            value = value | (payload[offset+i] & 0xff) << (8*i);
        }
        return value;
    }

    public static void writePayload(byte[] payload, byte cmd) {
        payload[0] = cmd;
    }
    public static void writePayload(byte[] payload, int offset, long cmd) {
        payload[offset]   = (byte) cmd;
        payload[offset+1] = (byte) (cmd>>8);
        payload[offset+2] = (byte) (cmd>>16);
        payload[offset+3] = (byte) (cmd>>24);
    }



    /**
     * getter and setter
     */
//    public int getNumEndpoints() {
//        return numEndpoints;
//    }
//    public Endpoint[] getDevEndpoints() {
//        return Arrays.copyOf(devEndpoints, devEndpoints.LENGTH);
//    }



}
