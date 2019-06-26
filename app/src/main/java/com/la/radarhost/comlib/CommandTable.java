package com.la.radarhost.comlib;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandTable {


    private static final byte CNST_MSG_QUERY_ENDPOINT_INFO = 0x00;
    private static final byte[] QUERY_ENDPOINT = {0x5A, 0x00, 0x01, 0x00, 0x00, (byte)0xDB, (byte)0xE0};

    private static HashMap<Integer, byte[]> commands = new HashMap<Integer, byte[]>(){{
        put(0, QUERY_ENDPOINT);
    }};


    public static byte[] getCmdBytes(int cmdNum) {
        return commands.get(cmdNum);
    }
}
