package com.la.radar;

public interface RadarData {

    int getDataType();

    int ERROR_TIMEOUT = -401;
    int ERROR_STATUS_CODE = -402;
    int TYPE_TARGETS = 401;
    int TYPE_STATUS_CODE = 402;
}
