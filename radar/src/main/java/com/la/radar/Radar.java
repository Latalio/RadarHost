package com.la.radar;

import com.la.radar.endpoint.targetdetection.DspSetting;

import java.util.LinkedList;
import java.util.Queue;

public class Radar {

    // need a staged area
    private Queue<RadarConfig> stagedConfigQueue = new LinkedList<>();

    DspSetting dspSetting = new DspSetting();

    void updateConfig() {
        RadarConfig config = stagedConfigQueue.poll();
        if (config == null) return;
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                dspSetting = (DspSetting)config; break;
            default: break;
        }
    }


    void updateConfig(RadarConfig config) {
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                dspSetting = (DspSetting)config; break;
            default: break;
        }
    }

    void updateConfig(RadarConfig[] configs) {
        for(RadarConfig config: configs) {
            updateConfig(config);
        }
    }

    public DspSetting getDspSetting() {
        return dspSetting;
    }

    public synchronized void stageConfig(RadarConfig config) {
        stagedConfigQueue.add(config);
    }

    void unstageConfig() {
        stagedConfigQueue.poll();
    }

}
