package com.la.radarhost.comlib.endpoint.targetdetection;

import com.la.radarhost.comlib.endpoint.Endpoint;
import com.la.radarhost.comlib.endpoint.EndpointDefinition;
import com.la.radarhost.comlib.protocol.MessageInfo;
import com.la.radarhost.comlib.protocol.Protocol;

import java.util.Arrays;

public class EndpointTargetDetection extends Endpoint {
    /**
     * Command Codes
     * Each payload message of the target detection endpoint starts with one of these command codes.
     */
    private final static byte MSG_GET_DSP_SETTINGS      = 0x00; /**< A message to retrieve dsp settings */
    private final static byte MSG_SET_DSP_SETTINGS      = 0x01; /**< A message to set dsp settings */
    private final static byte MSG_GET_TARGETS           = 0x02; /**< A message to get info about targets detected */
    private final static byte MSG_GET_RANGE_THRESHOLD   = 0x03; /**< A message to get target detection range threshold */
    //0520 verified

    private OnTargetInfoChangeListener mTargetInfoChangeListener;
    private OnDSPSettingsChangeListener mDSPSettingsChangeListener;

    @Override
    public void parsePayload(MessageInfo messageInfo) {

    }

    public interface OnTargetInfoChangeListener {
        void onChange(TargetInfo[] targets);
    }
    public interface OnDSPSettingsChangeListener {
        void onChange(TargetInfo[] targets);
    }

    private int parseTargetInfo(MessageInfo messageInfo) {
        byte[] payload = Arrays.copyOf(messageInfo.payload, messageInfo.payload.length);

        if ((byte)(Protocol.readPayload(payload,0,1)) == MSG_GET_TARGETS &&
                (payload.length>=2)) {

            if (mTargetInfoChangeListener != null) {
                short numTargets = (short)Protocol.readPayload(payload,1,1);
                TargetInfo[] targets = new TargetInfo[numTargets];

                // B0 MSG_GET_TARGETS
                // B1 number of targets
                if (payload.length == numTargets * TargetInfo.SIZE + 2) {
                    for(short i = 0;i<numTargets;i++) {
                        int targetID = (int)Protocol.readPayload(payload,2+i*TargetInfo.SIZE,4);
                        float level = (float)Protocol.readPayload(payload,6+i*TargetInfo.SIZE,4);
                        float radius = (float)Protocol.readPayload(payload,10+i*TargetInfo.SIZE,4);
                        float azimuth = (float)Protocol.readPayload(payload,14+i*TargetInfo.SIZE,4);
                        float elevation = (float)Protocol.readPayload(payload,18+i*TargetInfo.SIZE,4);
                        float radial_speed = (float)Protocol.readPayload(payload,22+i*TargetInfo.SIZE,4);
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
                    mTargetInfoChangeListener.onChange(targets);
                }
            }
            return 1;
        }
        return 0;
    }

    private int parseDSPSettings(MessageInfo messageInfo) {
        byte[] payload = Arrays.copyOf(messageInfo.payload, messageInfo.payload.length);

        if ((byte)(Protocol.readPayload(payload,0,1)) == MSG_GET_DSP_SETTINGS) {
            if (mDSPSettingsChangeListener!=null) {

            }
        }
        return 0;
    }

    private int parseRangeThreshold(MessageInfo messageInfo) {
        return 0;
    }


    int getTargets(Protocol protocol) {
        byte[] cmdMessage = new byte[1];

        Protocol.writePayload(cmdMessage, MSG_GET_TARGETS);

        return protocol.sendAndReceive(this, cmdMessage);
    }
}
