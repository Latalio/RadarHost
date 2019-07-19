package com.la.radarhost;

import com.la.radar.protocol.Protocol;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class TestFloat {
    public static void main(String[] args) {
        byte[] radius = {0x00,0x00, (byte)0xA9, 0x43};
//        byte[] radius = {0x43,(byte)0xA9, 0x00, 0x00};
//        int a = 0;
//        a |= radius[0];
//        System.out.println(Integer.toHexString(a));
//        a |= radius[1]<<8;
//        System.out.println(Integer.toHexString(a));
//        a |= (radius[2]&0xff)<<16;
//        System.out.println(Integer.toHexString(a));
//        a |= radius[3]<<24;
//        System.out.println(Integer.toHexString(a));

        DataInputStream d = new DataInputStream(new ByteArrayInputStream(radius));
        float a = (float) Protocol.readPayload(radius,0,4);
        System.out.println(a);

    }
}
