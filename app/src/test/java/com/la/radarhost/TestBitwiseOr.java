package com.la.radarhost;

public class TestBitwiseOr {

    public static void main(String[] args) {
        byte B1 = (byte)0x82;
        byte B2 = 0x00;

        int length = (B1&0xFF) | (B2&0xFF)<<8 ;
        System.out.println(length);

        byte B3 = (byte)256;
        System.out.println(B3);
    }
}
