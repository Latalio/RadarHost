package com.la.radarhost.comlib.endpoint;

import android.os.Message;

import com.la.radarhost.comlib.RadarEvent;
import com.la.radarhost.comlib.protocol.MessageInfo;
import com.la.radarhost.comlib.protocol.Protocol;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public abstract class Endpoint {
    /**< The type of the ep. */
    public int epNum;
    public long type;
    public int minVersion;
    public int maxVersion;
    public String description;


    public abstract void parsePayload(byte[] payload, RadarEvent event);

    public byte[] wrapCommand(byte[] cmd) {
        byte[] msgHeader = new byte[4];
        byte[] msgTail = new byte[2];

        msgHeader[0] = Protocol.CNST_STARTBYTE_DATA;
        msgHeader[1] = (byte) epNum;
        msgHeader[2] = (byte) cmd.length;
        msgHeader[3] = (byte)(cmd.length >> 8);

        msgTail[0] = (byte) Protocol.CNST_END_OF_PAYLOAD;
        msgTail[1] = (byte)(Protocol.CNST_END_OF_PAYLOAD >> 8);

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        byteOutStream.write(msgHeader,0, msgHeader.length);
        byteOutStream.write(cmd,0, cmd.length);
        byteOutStream.write(msgTail,0, msgTail.length);

        return byteOutStream.toByteArray();
    }

    public byte[] wrapCommand(byte cmd) {
        byte[] cmdArray = {cmd};
        return wrapCommand(cmdArray);
    }


}
