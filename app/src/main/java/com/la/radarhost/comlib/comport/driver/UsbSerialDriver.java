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

package com.la.radarhost.comlib.comport.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.PARITY_EVEN;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.PARITY_MARK;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.PARITY_NONE;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.PARITY_ODD;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.PARITY_SPACE;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.SET_LINE_CODING;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.STOPBITS_1;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.STOPBITS_1_5;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.STOPBITS_2;
import static com.la.radarhost.comlib.comport.driver.UsbSerialConstant.USB_RT_ACM;

/**
 * USB CDC/ACM serial driver implementation.
 *
 * @author mike wakerly (opensource@hoho.com)
 * @see <a
 *      href="http://www.usb.org/developers/devclass_docs/usbcdc11.pdf">Universal
 *      Serial Bus Class Definitions for Communication Devices, v1.1</a>
 */
public class UsbSerialDriver {

    private final static String TAG = UsbSerialDriver.class.getSimpleName();

    private final UsbManager mManager;
    private final UsbDevice mDevice;


    private PortTable portTable = new PortTable();

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
    public int request() {
        UsbSerialPort port = new UsbSerialPort();

        int portNumber = portTable.add(port);
        UsbDeviceConnection connection = buildConnection(); //maybe fail

        port.setDevice(mDevice);
        port.setConnection(connection);
        port.setPortNumber(portNumber);

        return portNumber;
    }

    public void release(int portNumber) {
        portTable.remove(portNumber);
    }

    private UsbDeviceConnection buildConnection() {
        UsbDeviceConnection connection = null;
        if (mManager.hasPermission(mDevice)) {
            connection = mManager.openDevice(mDevice);
        } else {
            // to do
        }
        return connection;
    }

    public UsbDevice getDevice() {
        return mDevice;
    }

    public PortTable getPorts() {
        return portTable;
    }

    /******************************
     Internal Class
     ******************************/


}
