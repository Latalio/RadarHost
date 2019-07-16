package com.la.radarhost.comlib;

public class RadarEvent {
    public int type;
    public Object obj;


    public RadarEvent(int type) {
        this.type = type;
    }

    public RadarEvent() {

    }

    public static final int TYPE_ERROR_TIMEOUT = -11;
    public static final int TYPE_TARGETS = 0x0702;

    public static final RadarEvent EVENT_TIMEOUT = new RadarEvent(TYPE_ERROR_TIMEOUT);

}
