package com.la.radarhost.comlib.endpoint.zero;

import android.os.Message;

import com.la.radarhost.comlib.RadarEvent;
import com.la.radarhost.comlib.endpoint.Endpoint;
import com.la.radarhost.comlib.protocol.MessageInfo;
import com.la.radarhost.comlib.protocol.Protocol;

import java.util.HashMap;

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
