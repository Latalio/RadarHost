package com.la.radarhost.comlib.endpoint;

import android.os.Message;

import com.la.radarhost.comlib.protocol.MessageInfo;

import java.util.Map;

public abstract class Endpoint {
    /**< The type of the endpoint. */
    public int epNum;
    public long type;
    public int minVersion;
    public int maxVersion;
    public String description;

    public Map<String, byte[]> commands;

    public abstract void parsePayload(byte[] payload, Message msg);

    public static byte[] warpCommand(byte cmd) {
        byte[] cmdBytes = {cmd};
        return cmdBytes;
    }

    /**
     * 首先判断类型，如果类型匹配成功，再判断版本。
     * 无匹配类型时，则定义为undefine端点；
     * 有匹配类型，但版本不匹配时，定义为version unmatch端点。
     */
//    public void checkCompatibility() {
//        boolean hitType = false;
//        boolean hitVersion = false;
//        for (EndpointDefinition epd:epHostDefGroup
//             ) {
//            if (type == epd.type) {
//                hitType = true;
//                epHostDef = epd;
//
//                if ((version>=epd.minVersion) && (version<=epd.maxVersion)) {
//                    hitVersion = true;
//                }
//                break;
//            }
//        }
//        if (!hitType) {
//            epHostDef = HOST_UNDEFINED;
//            return;
//        }
//        if (!hitVersion) {
//            epHostDef = VERSION_UNMATCH;
//        }
//    }
}
