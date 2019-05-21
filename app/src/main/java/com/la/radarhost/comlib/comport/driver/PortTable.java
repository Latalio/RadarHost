package com.la.radarhost.comlib.comport.driver;

public class PortTable {
    private final int LENGTH = 8;
    private UsbSerialPort[] ports = new UsbSerialPort[LENGTH];
    private int length = 0;

    int add(UsbSerialPort port) {
        if(isFull()) {
            return -1;
        }

        length += 1;
        return search();
    }

    int search() {
        int i;
        for (i=0;i<LENGTH;i++) {
            if(ports[i]==null) {
                break;
            }
        }
        return i;
    }

    boolean isFull() {
        return length == LENGTH;
    }

    void remove(int portNumber) {
        ports[portNumber] = null;
        length -= 1;
    }

    public UsbSerialPort get(int portNumber) {
        return ports[portNumber];
    }
}
