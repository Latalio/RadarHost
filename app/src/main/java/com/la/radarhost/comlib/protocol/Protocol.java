package com.la.radarhost.comlib.protocol;

import com.la.radarhost.comlib.endpoint.Endpoint;

public class Protocol {
    private final String TAG = Protocol.class.getSimpleName();

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






}
