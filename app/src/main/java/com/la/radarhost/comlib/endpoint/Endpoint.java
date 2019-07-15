package com.la.radarhost.comlib.endpoint;

import android.os.Message;

import com.la.radarhost.comlib.RadarEvent;
import com.la.radarhost.comlib.protocol.MessageInfo;
import com.la.radarhost.comlib.protocol.Protocol;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public abstract class Endpoint {
    /**< The type of the endpoint. */
    public static int epNum;
    public static long type;
    public static int minVersion;
    public static int maxVersion;
    public static String description;

    public Map<String, byte[]> commands;

    public abstract void parsePayload(byte[] payload, RadarEvent event);

    public byte[] getCommandBytes(String cmd) {
        return commands.get(cmd);
    }

    public static byte[] wrapCommand(byte cmd) {
        byte[] cmdBytes = {cmd};
        return cmdBytes;
    }

    public static byte[] wrapCommand(byte[] payload) {
        byte[] msgHeader = new byte[4];
        byte[] msgTail = new byte[2];

        msgHeader[0] = Protocol.CNST_STARTBYTE_DATA;
        msgHeader[1] = (byte) epNum;
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


}
