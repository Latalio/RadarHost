package com.la.radarhost.comlib.protocol;

public class EndpointDefinition {
    long type;
    int minVersion;
    int maxVersion;
    String description;

    public EndpointDefinition(long type, int minVersion, int maxVersion, String description) {
        this.type = type;
        this.minVersion = minVersion;
        this.maxVersion = maxVersion;
        this.description = description;
    }

}
