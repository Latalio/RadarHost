package com.la.radar.endpoint.targetdetection;

import com.la.radar.RadarData;

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
