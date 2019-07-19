package com.la.radar.protocol;

public class CheckBuffer {
    private final int SIZE = 2048;

    private byte[] bytearray = new byte[SIZE];

    private int mark = 0;

    public boolean put(byte[] bytes, int offset, int length) {
        if(length > SIZE-mark) return false; // can not put in more bytes

        System.arraycopy(bytes, offset, bytearray, mark, length);
        mark += length;
        return true;
    }

    public void clear() {
        mark = 0;
    }

    public byte[] array() {
        return bytearray;
    }

    public int length() {
        return mark;
    }

    public int tail() {
        return mark-1;
    }



}
