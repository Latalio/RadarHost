package com.la.radarhost.comlib.endpoint.base;

import android.os.Message;

import com.la.radarhost.comlib.endpoint.Endpoint;
import com.la.radarhost.comlib.protocol.MessageInfo;
import com.la.radarhost.comlib.protocol.Protocol;

import java.util.HashMap;

public class EndpointRadarBase extends Endpoint {
    /**
     * Command Codes
     * Each payload payload of the supported endpoint starts with one of these command codes.
     *
     */
    public static final byte MSG_FRAME_DATA             = 0x00;
    public static final byte MSG_GET_FRAME_DATA         = 0x01;

    public static final byte MSG_SET_AUTOMATIC_TRIGGER  = 0x02;
    public static final byte MSG_ENABLE_TEST_MODE       = 0x03;
    public static final byte MSG_GET_DRIVER_VERSION     = 0x20;
    public static final byte MSG_SET_DRIVER_VERSION     = 0x21;
    public static final byte MSG_GET_DEVICE_INFO        = 0x22;
    public static final byte MSG_SET_DEVICE_INFO        = 0x23;
    public static final byte MSG_GET_TEMPRATURE         = 0x30;
    public static final byte MSG_SET_TEMPRATURE         = 0x31;
    public static final byte MSG_GET_TX_POWER           = 0x32;
    public static final byte MSG_SET_TX_POWER           = 0x33;
    public static final byte MSG_GET_CHRIP_DURATION     = 0x34;
    public static final byte MSG_SET_CHRIP_DURATION     = 0x35;
    public static final byte MSG_GET_MIN_INTERVAL       = 0x36;
    public static final byte MSG_SET_MIN_INTERVAL       = 0x37;
    public static final byte MSG_GET_FRAME_FORMAT       = 0x40;
    public static final byte MSG_SET_FRAME_FORMAT       = 0x41;

    public EndpointRadarBase() {
        epNum = 1;
        type = 0x52424153L;
        minVersion = 1;
        maxVersion = 1;
        description = "ifxRadarBase";
        commands = new HashMap<String, byte[]>();
    }

    @Override
    public void parsePayload(byte[] payload, Message msg) {
        //todo
    }

    private int parseFrameInfo() {
        return 0;
    }

    public void setAutomaticFrameTrigger(long frameIntervalUs) {
        byte[] cmd = new byte[5];

        Protocol.writePayload(cmd, MSG_SET_AUTOMATIC_TRIGGER);
        Protocol.writePayload(cmd,1,frameIntervalUs);

        commands.put("SET_AUTOMATIC_TRIGGER", cmd);
    }
}
