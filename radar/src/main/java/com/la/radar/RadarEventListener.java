package com.la.radar;

import static com.la.radar.RadarManager.RADARDATA_ERROR_TIMEOUT;

public class RadarEventListener {

    private Radar mRadar;
    private RadarDataListener mDataListener;

    RadarEventListener(Radar radar, RadarDataListener listener) {
        mRadar = radar;
        mDataListener = listener;
    }

    public void onEventOccurred(RadarEvent event) {
        // status message
        if (event == RadarEvent.EVENT_TIMEOUT) {
            mDataListener.onDataChanged(RADARDATA_ERROR_TIMEOUT);
        } else if (event.obj == null) {
            if (event.status) { // todo whether to consider status code
                mRadar.updateConfig();
            } else {
                mRadar.unstageConfig();
            }
            mDataListener.onDataChanged((RadarData)event);
        } else {
            switch (event.type) {
                case RadarEvent.TYPE_GET_DSP_SETTINGS:
                    mRadar.updateConfig((RadarConfig)event.obj); break;
                case RadarEvent.TYPE_GET_TARGETS:
                    mDataListener.onDataChanged((RadarData)event.obj); break;
                case RadarEvent.TYPE_GET_RANGE_THRESHOLD: //todo how classify range threshold?
                    mDataListener.onDataChanged((RadarData)event.obj); break;
                default: break;
            }
        }
    }

}
