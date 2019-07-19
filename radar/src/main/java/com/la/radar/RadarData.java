package com.la.radar;

public interface RadarData {

    int getDataType();

    int ERROR_TIMEOUT = -401;
    int ERROR_UNUSUAL_STATUS_CODE = -402;
    int TYPE_TARGETS = 401;
}
