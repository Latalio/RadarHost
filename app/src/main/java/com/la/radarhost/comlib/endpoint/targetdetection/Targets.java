package com.la.radarhost.comlib.endpoint.targetdetection;

import com.la.radarhost.comlib.RadarData;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Targets implements RadarData {

    Target[] targets;

    @Override
    public int getDataType() {
        return RadarData.TYPE_TARGETS;
    }
}
