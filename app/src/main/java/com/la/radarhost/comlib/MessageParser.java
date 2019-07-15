package com.la.radarhost.comlib;

import java.util.LinkedList;
import java.util.Queue;

public class MessageParser extends Thread {

    private Queue<byte[]> MessageQueue = new LinkedList<>();
    private byte[] currMsg;


    @Override
    public void run() {

    }
}
