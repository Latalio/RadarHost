package com.la.radarhost.comlib.endpoint.zero;

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
        commands = new HashMap<String, byte[]>() {{
            put("QUERY_ENDPOINT_INFO", MSG_QUERY_ENDPOINT_INFO);
        }};
    }

    public Object parsePayload(byte[] payload) {
        return null;
    }

}
