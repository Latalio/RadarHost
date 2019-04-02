package com.la.radarhost.comlib.protocol;

public class Instance {
    /**< The number of endpoints present in the connected device. */
    short numEndpoints;
    /**< An array containing information about each endpoint present in the connected device. */
    Endpoint[] endpoints;

    Instance() {
        numEndpoints = 0;
    }
}
