package com.la.radar;

import android.util.Log;

import com.la.radar.endpoint.targetdetection.DspConfig;

import java.util.LinkedList;
import java.util.Queue;

public class Radar {
    private final String TAG = Radar.class.getSimpleName();

    // need a staged area
    private Queue<RadarConfig> stagedConfigQueue = new LinkedList<>();

    DspConfig dspConfig = new DspConfig();

    void updateConfig() {
        RadarConfig config = stagedConfigQueue.poll();
        if (config == null) return;
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                dspConfig = (DspConfig)config; break;
            default: break;
        }
    }

    void updateConfig(RadarConfig config) {
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                Log.e(TAG, "TYPE_DSP_SETTINGS");
                dspConfig = (DspConfig)config;
                Log.e(TAG, dspConfig.toString()); break;
            default: break;
        }
    }

    void updateConfig(RadarConfig[] configs) {
        for(RadarConfig config: configs) {
            updateConfig(config);
        }
    }

    public DspConfig getDspConfig() {
        return dspConfig;
    }

    public synchronized void stageConfig(RadarConfig config) {
        stagedConfigQueue.add(config);
    }

    void unstageConfig() {
        stagedConfigQueue.poll();
    }

}
