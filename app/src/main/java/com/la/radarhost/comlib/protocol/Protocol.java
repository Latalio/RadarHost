package com.la.radarhost.comlib.protocol;

import com.la.radarhost.comlib.comport.COMPort;
import com.la.radarhost.comlib.endpoint.Endpoint;

import java.util.Arrays;

public class Protocol {
    COMPort mPort;
    int mPortNumber = -1;

    // Protocol Instance Struct
    int mHandle = -1;
    private Endpoint[] devEndpoints;
    private int numEndpoints;

    public Protocol(COMPort port) {
        mPort = port;
    }

    private static final byte  CNST_STARTBYTE_DATA = 0x5A;
    private static final byte  CNST_STARTBYTE_STATUS = 0x5B;
    private static final short CNST_END_OF_PAYLOAD = (short) 0xE0DB;

    private static final byte CNST_MSG_QUERY_ENDPOINT_INFO = 0x00;
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
    private void recoverFromReceiveError() {
        /* read until buffer is empty */
        byte[] dummy_data = new byte[1024];
        int received_bytes = dummy_data.length;
        while (received_bytes == dummy_data.length) {
            received_bytes = mPort.getData(dummy_data);
        }
        /* now we have run out of data, protocol should be in sync again */
    }

    private void sendMessage(int epNum, final byte[] payload) {
        /* setup message header and tail */
        byte[] msgHeader = new byte[4];
        byte[] msgTail = new byte[2];

        msgHeader[0] = CNST_STARTBYTE_DATA;
        msgHeader[1] = (byte) epNum;
        msgHeader[2] = (byte) payload.length;
        msgHeader[3] = (byte)(payload.length >> 8);

        msgTail[0] = (byte) CNST_END_OF_PAYLOAD;
        msgTail[1] = (byte)(CNST_END_OF_PAYLOAD >> 8);

        /* send message */
        mPort.sendData(msgHeader); // 4 Bytes
        mPort.sendData(payload);   // payload size
        mPort.sendData(msgTail);   // 2 Bytes
    }

    private int getMessage(MessageInfo msgInfo) {
        int numReceivedBytes;
        byte[] msgHeader = new byte[4];

        numReceivedBytes = mPort.getData(msgHeader);
        if (numReceivedBytes < msgHeader.length) {
            numReceivedBytes += mPort.getData(msgHeader, numReceivedBytes);
        }                          // get again
        if (numReceivedBytes == 0) return PROTOCOL_ERROR_RECEIVED_NO_MESSAGE;   // no msg
        else if (numReceivedBytes < msgHeader.length)  {
            recoverFromReceiveError();
            return PROTOCOL_ERROR_RECEIVED_TIMEOUT;
        }                    // timeout

        if (msgHeader[0] == CNST_STARTBYTE_DATA) {
            int payloadSize;
            byte[] payload;
            byte[] msgTail = new byte[2];

            payloadSize = (int)msgHeader[2] | ((int)msgHeader[3])<<8;
            payload = new byte[payloadSize];

            numReceivedBytes = mPort.getData(payload);

            /* check if payload has been received completely */
            if (numReceivedBytes < payloadSize) {
                recoverFromReceiveError();
                return PROTOCOL_ERROR_RECEIVED_TIMEOUT;
            }

            /* check message tail */
            numReceivedBytes = mPort.getData(msgTail);

            if ((numReceivedBytes != msgTail.length) ||
                    (msgTail[0] != (byte) CNST_END_OF_PAYLOAD) ||
                    (msgTail[1] != (byte)(CNST_END_OF_PAYLOAD >> 8))) {
                recoverFromReceiveError();
                return PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_END;
            }

            msgInfo.endpoint = msgHeader[1];
            msgInfo.payload = payload;

            return CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG;
        }
        else if (msgHeader[0] == CNST_STARTBYTE_STATUS) {
            short endpoint = msgHeader[1];
            int status_code = (int) msgHeader[2] | ((int)msgHeader[3]) <<8;

            return ((int)endpoint << 16) | status_code;
        }
        else {
            recoverFromReceiveError();
            return PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_START;
        }
    }

    /**
     ===============================================================================================
        7. Exported Functions
     ===============================================================================================
     */
    /**
     * 打开特定端口，并获取端点信息。
     */
    public int connect() {
        MessageInfo msgInfo = new MessageInfo();
        int statusCode;

        mPortNumber = mPort.open();
        if (mPortNumber < 0) return PROTOCOL_ERROR_COULD_NOT_OPEN_COM_PORT;

        /* query endpoint information */
        byte[] uQueryMessage = { CNST_MSG_QUERY_ENDPOINT_INFO };
        sendMessage(0, uQueryMessage);

        /* get msg and check validation */
        statusCode = getMessage(msgInfo);
        if ((statusCode != CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG) ||
                (msgInfo.endpoint != 0) ||
                (msgInfo.payload.length <2 ) ||
                (msgInfo.payload[0] != CNST_MSG_ENDPOINT_INFO)) {
            mPort.close();
            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
        }

        numEndpoints = msgInfo.payload[1];
        if ((msgInfo.payload.length != 6*numEndpoints + 2) ||
                (numEndpoints == 0)) {
            mPort.close();
            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
        }

        /* parse endpoint msg */
        devEndpoints = new Endpoint[numEndpoints];
        for(int i=0; i<numEndpoints; ++i) {
            Endpoint endpoint = devEndpoints[i];
            endpoint.type = readPayload(msgInfo.payload, 2 + i*6, 4);
            endpoint.version = (int)readPayload(msgInfo.payload, 6 + i*6, 2);
            endpoint.checkCompatibility();
        }

        /* check the status code */
        statusCode = getMessage(msgInfo);
        if (statusCode != 0) {   // (/*endpoint*/0 << 16) | /*status code*/0x0000)
            devEndpoints = null;
            mPort.close();
            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
        }

        /* register the mHandle in ProtocolManager */
        mHandle = ProtocolManager.register(this);
        return mHandle;
    }

    public void disconnect() {
        if (ProtocolManager.isValid(this)) {
            /* close COM Port */
            mPort.close();

            /* reset field */
            devEndpoints = null;
            ProtocolManager.unregister(this);
        }
    }

    // be called by ep functions
    public int sendAndReceive(Endpoint endpoint, byte[] payload) {
        int statusCode;
        MessageInfo messageInfo = new MessageInfo();
        /* check mHandle and endpoint compatibility */
        /* --------------------------------------- */
        /* check mHandle */
        if (ProtocolManager.isValid(this)) {
            return PROTOCOL_ERROR_INVALID_HANDLE;
        }

        /* check if endpoint exists */
        if (!existInDevEndpoints(endpoint)) {
            return nonexistReason(endpoint);
        }

        /* send message */
        sendMessage(endpoint.epNumber, payload);

        /* receive message from the board */
        while ((statusCode = getMessage(messageInfo)) == CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG) {
            endpoint.parsePayload(messageInfo);
            messageInfo = null;
        }

        return statusCode;
    }

    // return 0 means valid
    private int nonexistReason(Endpoint endpoint) {
        if (endpoint.type != endpoint.epHostDef.type)
            return PROTOCOL_ERROR_ENDPOINT_WRONG_TYPE;
        if (endpoint.version < endpoint.epHostDef.minVersion)
            return PROTOCOL_ERROR_ENDPOINT_VERSION_TOO_OLD;
        if (endpoint.version > endpoint.epHostDef.maxVersion)
            return PROTOCOL_ERROR_ENDPOINT_VERSION_TOO_NEW;
        return 0;
    }

    private boolean existInDevEndpoints(Endpoint endpoint) {
        boolean hit = false;
        for (Endpoint ep : devEndpoints) {
            if (ep.epHostDef.equals(endpoint.epHostDef)) {
                hit = true;
                break;
            }
        }
        return hit;
    }


    /**
     ===============================================================================================
     7. Static Functions
     ===============================================================================================
     */
    public static long readPayload(final byte[] payload, int offset, int length) {
        // Little Endian high-vs-high, low-vs-low
        long value = 0L;
        for (int i=0; i<length; i++) {
            value = value | payload[offset+i] << (8*i);
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
    public int getNumEndpoints() {
        return numEndpoints;
    }
    public Endpoint[] getDevEndpoints() {
        return Arrays.copyOf(devEndpoints, devEndpoints.length);
    }



}
