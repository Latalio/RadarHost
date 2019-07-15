package com.la.radarhost.comlib;

public class RadarEvent {
    public int type;
    public Object obj;


    public RadarEvent(int type) {
        this.type = type;
    }

    public RadarEvent() {

    }

    public static final int PAYLOAD_INVALID = -11;

}
