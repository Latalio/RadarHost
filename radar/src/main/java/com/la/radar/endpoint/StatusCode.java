package com.la.radar.endpoint;

import com.la.radar.RadarData;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StatusCode implements RadarData {
    short status_code;

    @Override
    public int getDataType() {
        return RadarData.ERROR_UNUSUAL_STATUS_CODE;
    }
}
