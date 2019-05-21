package com.la.radarhost.comlib.protocol;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;

public class

ProtocolManager {
    private final static int LENGTH = 8;
    private static Protocol[] protocolTable = new Protocol[LENGTH];
    private static int numHandles = 0;

    public static int register(Protocol protocol) {
        int handle = search(null);
        if (handle < 0) {
            return -1; // means that table is full
        }

        protocolTable[handle] = protocol;
        numHandles += 1;
        return handle;
    }

    public static boolean unregister(Protocol protocol) {
        int handle = search(protocol);
        if (handle < 0) {
            return false; // means that protocol not exist in the table
        }

        protocolTable[handle] = null;
        numHandles -= 1;
        return true;
    }

    public static boolean isValid(Protocol protocol) {
        int handle = search(protocol);
        if (handle < 0) {
            return false;
        }
        return true;
    }

    // return the index where target appear first time otherwise return -1
    private static int search(Protocol protocol) {
        boolean hit = false;
        int i;
        for (i=0;i<LENGTH;i++) {
            if (protocolTable[i] == protocol) {
                hit = true;
                break;
            }
        }

        if (hit) {
            return i;
        }
        return -1;
    }
}
