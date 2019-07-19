package com.la.radar.endpoint.zero;

import com.la.radar.RadarEvent;
import com.la.radar.endpoint.Endpoint;

public class EndpointZero extends Endpoint {

    private final static byte[] MSG_QUERY_ENDPOINT_INFO = {0x00};

    public EndpointZero() {
        epNum       = 0;
        type        = 0L;
        minVersion  = 0;
        maxVersion  = 1;
        description = "ifxRadar EPZero";
    }

    public void parsePayload(byte[] payload, RadarEvent event) {

    }

}
