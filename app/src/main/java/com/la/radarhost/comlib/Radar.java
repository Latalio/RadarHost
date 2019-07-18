package com.la.radarhost.comlib;

import com.la.radarhost.comlib.endpoint.targetdetection.DspSettings;

import java.util.LinkedList;
import java.util.Queue;

public class Radar {

    // need a staged area
    private Queue<RadarConfig> stagedConfigQueue = new LinkedList<>();

    DspSettings dspSettings = new DspSettings();

    void updateConfig() {
        RadarConfig config = stagedConfigQueue.poll();
        if (config == null) return;
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                dspSettings = (DspSettings)config; break;
            default: break;
        }
    }


    void updateConfig(RadarConfig config) {
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                dspSettings = (DspSettings)config; break;
            default: break;
        }
    }

    void updateConfig(RadarConfig[] configs) {
        for(RadarConfig config: configs) {
            updateConfig(config);
        }
    }

    public DspSettings getDspSettings() {
        return dspSettings;
    }

    public synchronized void stageConfig(RadarConfig config) {
        stagedConfigQueue.add(config);
    }

    void unstageConfig() {
        stagedConfigQueue.poll();
    }

}
