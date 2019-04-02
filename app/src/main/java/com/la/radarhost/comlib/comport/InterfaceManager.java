package com.la.radarhost.comlib.comport;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

public class InterfaceManager {
    /**
     * 用于打开正确的 USB Interface 和 USB Endpoint
     */
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;

    private UsbInterface mInterface;
    private UsbEndpoint inEndpoint;
    private UsbEndpoint outEndpoint;

    private static final int INTERFACE_BULK = 1;
    private static final int ENDPOINT_COUNT = 2;
    private static final int OPEN_SUCCEED = 10;
    private static final int ENDPOINT_PARSE_FAILED = -1;
    private static final int INTERFACE_CLAIM_FAILED = -1;

    public InterfaceManager(UsbDevice usbDevice,
                            UsbDeviceConnection usbDeviceConnection) {
        mDevice = usbDevice;
        mConnection = usbDeviceConnection;
    }

    public boolean open() {
        mInterface = mDevice.getInterface(INTERFACE_BULK);
        if (!mConnection.claimInterface(mInterface, false)) {
            Log.d("InterfaceManager", "Interface claim failed.");
            return false;
        }
        for (int intfIndex=0; intfIndex<ENDPOINT_COUNT; intfIndex++) {
            UsbEndpoint usbEndpoint = mInterface.getEndpoint(intfIndex);
            if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_IN) {
                inEndpoint = usbEndpoint;
                Log.d("InterfaceManager", "In endpoint got.");
            } else if (usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                outEndpoint = usbEndpoint;
                Log.d("InterfaceManager", "Out endpoint got.");
            } else {
                Log.d("InterfaceManager", "Endpoint parse failed.");
                return false;
            }
        }
        return true;
    }

    public boolean close() {
        return mConnection.releaseInterface(mInterface);
    }

    public UsbEndpoint getInEndpoint() { return inEndpoint; }
    public UsbEndpoint getOutEndpoint() { return outEndpoint; }
}
