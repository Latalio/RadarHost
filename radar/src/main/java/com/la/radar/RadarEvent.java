package com.la.radar;

import com.la.radar.endpoint.StatusCode;

public class RadarEvent {
    public int type;
    public Object obj;
    public boolean status;
    public StatusCode statusCode;


    public RadarEvent(int type) {
        this.type = type;
    }

    public RadarEvent() {

    }

    public static final int TYPE_ERROR_TIMEOUT = -11;
    public static final int TYPE_GET_DSP_SETTINGS = 0x0700;
    public static final int TYPE_SET_DSP_SETTINGS = 0x0701;
    public static final int TYPE_GET_TARGETS = 0x0702;
    public static final int TYPE_GET_RANGE_THRESHOLD = 0x0703;

    public static final RadarEvent EVENT_TIMEOUT = new RadarEvent(TYPE_ERROR_TIMEOUT);

}
