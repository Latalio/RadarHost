package com.la.radarhost.comlib.endpoint.targetdetection;

import com.la.radarhost.comlib.endpoint.Endpoint;
import com.la.radarhost.comlib.protocol.MessageInfo;
import com.la.radarhost.comlib.protocol.Protocol;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;

public class EndpointTargetDetection extends Endpoint {
    /**
     * Command Codes
     * Each payload payload of the target detection endpoint starts with one of these command codes.
     */
    private final static byte MSG_GET_DSP_SETTINGS      = 0x00; /**< A payload to retrieve dsp settings */
    private final static byte MSG_SET_DSP_SETTINGS      = 0x01; /**< A payload to set dsp settings */
    private final static byte MSG_GET_TARGETS           = 0x02; /**< A payload to get info about targets detected */
    private final static byte MSG_GET_RANGE_THRESHOLD   = 0x03; /**< A payload to get target detection range threshold */
    //0520 verified

    public EndpointTargetDetection() {
        epNum       = 7;
        type        = 0x52544443L;
        minVersion  = 1;
        maxVersion  = 1;
        description = "ifxRadar Target Detection";
        commands = new HashMap<String, byte[]>() {{
            put("GET_DSP_SETTINGS", Endpoint.warpCommand(MSG_GET_DSP_SETTINGS));
            put("GET_TARGETS", Endpoint.warpCommand(MSG_GET_TARGETS));
            put("GET_RANGE_THRESHOLD", Endpoint.warpCommand(MSG_GET_RANGE_THRESHOLD));
        }};
    }


    @Override
    public Object parsePayload(byte[] payload) {
        Object obj;
        byte msgTag = (byte)Protocol.readPayload(payload,0,1);
        switch (msgTag) {
            case MSG_GET_TARGETS:
                obj = parseTargetInfo(payload);
                break;
            default:
                obj = null;
        }

        return obj;

    }

    private static Object parseTargetInfo(byte[] payload) {
        short numTargets = (short)Protocol.readPayload(payload,1,1);
        TargetInfo[] targets = new TargetInfo[numTargets];

        // B0 MSG_GET_TARGETS
        // B1 number of targets
        if (payload.length == numTargets * TargetInfo.SIZE + 2) {
            for(short i = 0;i<numTargets;i++) {
                int targetID = (int)Protocol.readPayload(payload,2+i*TargetInfo.SIZE,4);
                float level = (float)Protocol.readPayload(payload,6+i*TargetInfo.SIZE,4);
                float radius = Float.intBitsToFloat(
                        (int)Protocol.readPayload(payload,10+i*TargetInfo.SIZE,4));
                float azimuth = (float)Protocol.readPayload(payload,14+i*TargetInfo.SIZE,4);
                float elevation = (float)Protocol.readPayload(payload,18+i*TargetInfo.SIZE,4);
                float radial_speed = Float.intBitsToFloat(
                        (int)Protocol.readPayload(payload,22+i*TargetInfo.SIZE,4));
                float azimuth_speed = (float)Protocol.readPayload(payload,26+i*TargetInfo.SIZE,4);
                float elevation_speed = (float)Protocol.readPayload(payload,30+i*TargetInfo.SIZE,4);

                TargetInfo targetInfo = new TargetInfo(
                        targetID,
                        level,
                        radius,
                        azimuth,
                        elevation,
                        radial_speed,
                        azimuth_speed,
                        elevation_speed
                );
                targets[i] = targetInfo;
            }
        }
        StringBuilder str = new StringBuilder();
        for (TargetInfo target : targets) {
            str.append("ID: ");
            str.append(target.targetID);
            str.append("\n");
            str.append("distance: ");
            str.append(target.radius);
            str.append("\n");
            str.append("speed: ");
            str.append(target.radial_speed);
            str.append("\n");
        }
        return str.toString();
    }

    private boolean parseDSPSettings(byte[] payload) {

        return false;
    }

    private boolean parseRangeThreshold(byte[] payload) {
        return false;
    }

}
