package com.la.radarhost.comlib.endpoint;

import com.la.radarhost.comlib.protocol.MessageInfo;

public abstract class Endpoint {
    /**< The type of the endpoint. */
    public int epNumber;
    public long type;
    public int version;
    public EndpointDefinition epHostDef;

    public abstract void parsePayload(MessageInfo messageInfo);

    public final static EndpointDefinition HOST_UNDEFINED = new EndpointDefinition(
            0x0L,
            0,
            0,
            "undefined"
    );

    public final static EndpointDefinition VERSION_UNMATCH = new EndpointDefinition(
            0x0L,
            0,
            0,
            "version unmatch"
    );

    public final static EndpointDefinition BASE = new EndpointDefinition(
            0x52424153L,
            1,
            1,
            "ifxRadarBase"
    );

    public final static EndpointDefinition TARGET_DETECTION = new EndpointDefinition(
            0x52544443L,
            1,
            1,
            "ifxRadar Target Detection"
    );

    private final static EndpointDefinition[] epHostDefGroup = {BASE, TARGET_DETECTION};

    /**
     * 首先判断类型，如果类型匹配成功，再判断版本。
     * 无匹配类型时，则定义为undefine端点；
     * 有匹配类型，但版本不匹配时，定义为version unmatch端点。
     */
    public void checkCompatibility() {
        boolean hitType = false;
        boolean hitVersion = false;
        for (EndpointDefinition epd:epHostDefGroup
             ) {
            if (type == epd.type) {
                hitType = true;
                epHostDef = epd;

                if ((version>=epd.minVersion) && (version<=epd.maxVersion)) {
                    hitVersion = true;
                }
                break;
            }
        }
        if (!hitType) {
            epHostDef = HOST_UNDEFINED;
            return;
        }
        if (!hitVersion) {
            epHostDef = VERSION_UNMATCH;
        }
    }
}
