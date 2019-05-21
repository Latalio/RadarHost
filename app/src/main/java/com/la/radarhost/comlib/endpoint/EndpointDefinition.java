package com.la.radarhost.comlib.endpoint;

import com.la.radarhost.comlib.protocol.MessageInfo;

public class EndpointDefinition {
    public long type;
    public int minVersion;
    public int maxVersion;
    public String description;

    public EndpointDefinition(long type, int minVersion, int maxVersion, String description) {
        this.type = type;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EndpointDefinition)) {
            return false;
        }
        EndpointDefinition epd = (EndpointDefinition) obj;
        return (type==epd.type)&&(minVersion==epd.minVersion)&&(maxVersion==epd.maxVersion);
    }
}
