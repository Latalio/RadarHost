package com.la.radar;

public interface RadarConfig {

    int getConfigType();

    void setValue(String field, String value);

    String getValue(String field);


    int TYPE_DSP_SETTINGS = 101;
}
