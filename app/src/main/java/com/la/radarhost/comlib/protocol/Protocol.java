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
