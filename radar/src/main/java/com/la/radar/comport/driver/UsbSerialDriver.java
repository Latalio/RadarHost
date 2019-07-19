/* Copyright 2011-2013 Google Inc.
 * Copyright 2013 mike wakerly <opensource@hoho.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: https://github.com/mik3y/usb-serial-for-android
 */

package com.la.radar.comport.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import java.io.IOException;


public class UsbSerialDriver {

    private final static String TAG = UsbSerialDriver.class.getSimpleName();

    private final UsbManager mManager;
    private final UsbDevice mDevice;

    private int mNumPorts = 0;


    private final short PORTS_LENGTH = 8;
    private UsbSerialPort[] ports = new UsbSerialPort[PORTS_LENGTH];

    /******************************
     Constructor
     ******************************/
    public UsbSerialDriver(UsbManager manager, UsbDevice device) {
        mDevice = device;
        mManager = manager;
    }

    /******************************
     External Functions
     ******************************/
    public int requestPort() {
        UsbSerialPort port = new UsbSerialPort(this);
        if(isFull()) return -1;

        mNumPorts += 1;
        int portNum = search(null);
        ports[portNum] = port;
        return portNum;
    }

    public void releasePort(int portNum) {
        try {
            ports[portNum].close();
        } catch (IOException e) {
            // Ignore
        } finally {
            ports[portNum]= null;
            mNumPorts -= 1;
        }
    }

    UsbDeviceConnection requestConnection() {
        UsbDeviceConnection connection = null;
        if (mManager.hasPermission(mDevice)) {
            connection = mManager.openDevice(mDevice);
        }
        return connection;
    }


    public UsbDevice getDevice() {
        return mDevice;
    }

    public UsbSerialPort getPort(int portNum) {
        return ports[portNum];
    }

    public int getPortNum(UsbSerialPort port) {
        return search(port);
    }

    // funcs to handle ports list

    private int search(UsbSerialPort port) {
        int i;
        for (i=0;i<PORTS_LENGTH;i++) {
            if(ports[i]==port) {
                break;
            }
        }
        return i;
    }

    private boolean isFull() {
        return mNumPorts == PORTS_LENGTH;
    }


}
