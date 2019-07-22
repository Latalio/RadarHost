package com.la.radar.endpoint.targetdetection;

import android.util.Log;

import com.la.radar.RadarEvent;
import com.la.radar.comport.util.HexDump;
import com.la.radar.endpoint.Endpoint;
import com.la.radar.protocol.Protocol;

public class EndpointTargetDetection extends Endpoint {
    String TAG = "EPTD";
    /**
     * Command Codes
     * Each payload payload of the target detection ep starts with one of these command codes.
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
    }


    @Override
    public void parsePayload(byte[] payload, RadarEvent event) {
        byte msgTag = (byte)Protocol.readPayload(payload,0,1);
        switch (msgTag) {
            case MSG_GET_DSP_SETTINGS: parseDspSettings(payload, event); break;
            case MSG_GET_TARGETS: parseTargetInfo(payload, event); break;
            case MSG_GET_RANGE_THRESHOLD: parseRangeThreshold(payload, event); break;
            default: break;
        }
    }

    private static void parseTargetInfo(byte[] payload, RadarEvent event) {
        short numTargets = (short)Protocol.readPayload(payload,1,1);
        Target[] targets = new Target[numTargets];

        // B0 MSG_GET_TARGETS
        // B1 number of targets
        if (payload.length == numTargets * Target.SIZE + 2) {
            for(short i = 0;i<numTargets;i++) {
                int targetID = (int)Protocol.readPayload(payload,2+i*Target.SIZE,4);
                float level = (float)Protocol.readPayload(payload,6+i*Target.SIZE,4);
                float radius = Float.intBitsToFloat(
                        (int)Protocol.readPayload(payload,10+i*Target.SIZE,4));
                float azimuth = (float)Protocol.readPayload(payload,14+i*Target.SIZE,4);
                float elevation = (float)Protocol.readPayload(payload,18+i*Target.SIZE,4);
                float radial_speed = Float.intBitsToFloat(
                        (int)Protocol.readPayload(payload,22+i*Target.SIZE,4));
                float azimuth_speed = (float)Protocol.readPayload(payload,26+i*Target.SIZE,4);
                float elevation_speed = (float)Protocol.readPayload(payload,30+i*Target.SIZE,4);


                targets[i] = new Target(
                        targetID,
                        level,
                        radius,
                        azimuth,
                        elevation,
                        radial_speed,
                        azimuth_speed,
                        elevation_speed
                );
            }
        }
        event.type = RadarEvent.TYPE_GET_TARGETS;
        event.obj = new Targets(targets);
    }

    private void parseDspSettings(byte[] payload, RadarEvent event) {
        Log.d(TAG, HexDump.toHexString(payload));

        short range_mvg_avg_length;
        int min_range_cm;
        int max_range_cm;
        int min_speed_kmh;
        int max_speed_kmh;
        int min_angle_degree;
        int max_angle_degree;
        int range_threshold;
        int speed_threshold;
        int adaptive_offset;
        short enable_tracking;
        short num_of_tracks;
        short median_filter_length;
        short enable_mti_filter;
        int mti_filter_length;
        short range_thresh_type;

        //B0 MSG_GET_DSP_SETTINGS
        range_mvg_avg_length = (short)Protocol.readPayload(payload,1,1);
        min_range_cm = (int)Protocol.readPayload(payload,2,2);
        max_range_cm = (int)Protocol.readPayload(payload,4,2);
        min_speed_kmh = (int)Protocol.readPayload(payload,6,2);
        max_speed_kmh = (int)Protocol.readPayload(payload,8,2);
        min_angle_degree = (int)Protocol.readPayload(payload,10,2);
        max_angle_degree = (int)Protocol.readPayload(payload,12,2);
        range_threshold = (int)Protocol.readPayload(payload,14,2);
        speed_threshold = (int)Protocol.readPayload(payload,16,2);

        if (payload.length == 27) {
            adaptive_offset = (int)Protocol.readPayload(payload,18,2);
            enable_tracking = (short)Protocol.readPayload(payload,20,1);
            num_of_tracks = (short)Protocol.readPayload(payload,21,1);
            median_filter_length = (short)Protocol.readPayload(payload,22,1);
            enable_mti_filter = (short)Protocol.readPayload(payload,23,1);
            mti_filter_length = (int)Protocol.readPayload(payload,24,2);
            range_thresh_type = (short)Protocol.readPayload(payload,26,1);
        } else {
            adaptive_offset = 0;
            enable_tracking = 0;
            num_of_tracks = 1;
            median_filter_length = 5;
            enable_mti_filter = 0;
            mti_filter_length = 10;
            range_thresh_type = 0;
        }

        event.type = RadarEvent.TYPE_GET_DSP_SETTINGS;
        event.obj = new DspConfig(
                range_mvg_avg_length,
                range_thresh_type,
                min_range_cm,
                max_range_cm,
                min_speed_kmh,
                max_speed_kmh,
                min_angle_degree,
                max_angle_degree,
                range_threshold,
                speed_threshold,
                adaptive_offset,
                enable_tracking,
                num_of_tracks,
                median_filter_length,
                enable_mti_filter,
                mti_filter_length
        );



    }

    private void parseRangeThreshold(byte[] payload, RadarEvent event) {
        int threshold = (int)Protocol.readPayload(payload,1,2);
        event.type = RadarEvent.TYPE_GET_RANGE_THRESHOLD;
        event.obj = threshold;
    }

    /**
     * Command generation methods
     */

    public byte[] getDspSettings() {
        return wrapCommand(MSG_GET_DSP_SETTINGS);
    }

    public byte[] setDspSettings(DspConfig settings) {
        byte[] cmd = new byte[27];

        Protocol.writePayload(cmd,MSG_SET_DSP_SETTINGS);
        Protocol.writePayload(cmd,1,settings.range_mvg_avg_length);
        Protocol.writePayload(cmd,2,settings.min_range_cm);
        Protocol.writePayload(cmd,4,settings.max_range_cm);
        Protocol.writePayload(cmd,6,settings.min_speed_kmh);
        Protocol.writePayload(cmd,8,settings.max_speed_kmh);
        Protocol.writePayload(cmd,10,settings.min_angle_degree);
        Protocol.writePayload(cmd,12,settings.max_angle_degree);
        Protocol.writePayload(cmd,14,settings.range_threshold);
        Protocol.writePayload(cmd,16,settings.speed_threshold);
        Protocol.writePayload(cmd,18,settings.adaptive_offset);
        Protocol.writePayload(cmd,20,settings.enable_tracking);
        Protocol.writePayload(cmd,21,settings.num_of_tracks);
        Protocol.writePayload(cmd,22,settings.median_filter_length);
        Protocol.writePayload(cmd,23,settings.enable_mti_filter);
        Protocol.writePayload(cmd,24,settings.mti_filter_length);
        Protocol.writePayload(cmd,26,settings.range_thresh_type);

        Log.d(TAG, HexDump.toHexString(cmd));
        return wrapCommand(cmd);
    }

    public byte[] getTargets() {
        return wrapCommand(MSG_GET_TARGETS);
    }

    public byte[] getRangeThreshold() {
        return wrapCommand(MSG_GET_RANGE_THRESHOLD);
    }

}
