package com.la.radarhost.comlib.protocol;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ProtocolManager {
    public static Protocol[] handles;

    public static int register(Protocol protocol) {
        int i;
        for (i=0; i<handles.length; i++) {
            if (handles[i] == null) break;
        }
        if (i == handles.length) {
            handles = Arrays.copyOf(handles, handles.length + 1);
            handles[i] = protocol;
        } else {
            handles[i] = protocol;
        }

        return i;
    }

    public static void unregister(Protocol protocol) {
        handles[Arrays.binarySearch(handles, protocol)] = null;
    }
}
