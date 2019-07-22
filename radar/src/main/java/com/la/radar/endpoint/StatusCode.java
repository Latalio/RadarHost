package com.la.radar.endpoint;

import com.la.radar.RadarData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
public class StatusCode implements RadarData {

    @Getter
    short status_code;

    @Override
    public int getDataType() {
        return RadarData.TYPE_STATUS_CODE;
    }

    public final static short SUCCESS = 0x0000;
}
