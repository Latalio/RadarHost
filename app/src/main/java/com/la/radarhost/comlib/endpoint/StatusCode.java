package com.la.radarhost.comlib.endpoint;

import com.la.radarhost.comlib.RadarData;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StatusCode implements RadarData {
    short status_code;

    @Override
    public int getDataType() {
        return RadarData.ERROR_UNUSUAL_STATUS_CODE;
    }
}
