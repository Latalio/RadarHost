package com.la.radarhost;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.la.radarhost.comlib.Command;
import com.la.radarhost.comlib.MessagePipeline;
import com.la.radarhost.comlib.ProtocolWorker;
import com.la.radarhost.comlib.RadarEventListener;
import com.la.radarhost.comlib.comport.driver.UsbSerialConstant;
import com.la.radarhost.comlib.comport.driver.UsbSerialDriver;
import com.la.radarhost.comlib.comport.driver.UsbSerialPort;
import com.la.radarhost.comlib.endpoint.base.EndpointRadarBase;
import com.la.radarhost.comlib.endpoint.targetdetection.EndpointTargetDetection;
import com.la.radarhost.comlib.endpoint.zero.EndpointZero;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class D2GRadar {
    private final static String TAG = D2GRadar.class.getSimpleName();

    private EndpointZero mEpZero;
    private EndpointRadarBase mEpBase;
    private EndpointTargetDetection mEpTargetDetect;

    private MessagePipeline mMsgPipe;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbSerialDriver mDriver;
    private UsbSerialPort mPort;

    // state field
    private boolean connected = false;

    private RadarEventListener mListener;

    // todo checkout vendor and product id
    private final int D2G_VENDOR_ID = 1419;
    private final int D2G_PRODUCT_ID = 88;

    // msg type
    public static final int MT_GET_TARGETS = 110;

    public D2GRadar() {
        mEpZero = new EndpointZero();
        mEpBase = new EndpointRadarBase();
        mEpTargetDetect = new EndpointTargetDetection();
    }

    public boolean connect(Context context) {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devList = mUsbManager.getDeviceList();
        boolean found = false;

        for (Map.Entry<String, UsbDevice> entry : devList.entrySet()) {
            UsbDevice device = entry.getValue();
            if (device.getVendorId() == D2G_VENDOR_ID &&
                    device.getProductId() == D2G_PRODUCT_ID) {
                mDevice = device;
                found = true;
                break;
            }
        }

        if (!found) {
            Log.e(TAG, "<devExist> device not found.");
            return false;
        }

        mDriver = new UsbSerialDriver(mUsbManager, mDevice);
        int portNum = mDriver.requestPort();
        mPort = mDriver.getPort(portNum);
        try {
            mPort.open();
            mPort.setParameters(
                    UsbSerialConstant.BAUDRATE_115200,
                    UsbSerialConstant.DATABITS_8,
                    UsbSerialConstant.STOPBITS_1,
                    UsbSerialConstant.PARITY_NONE
            );
        } catch (IOException e) {
            Log.d(TAG, "Port open failed.");
            return false;
        }
        Log.d(TAG, "Port open succeed.");

        connected = true;
        mMsgPipe = new MessagePipeline(mPort);
        mMsgPipe.start();
        return true;
    }

    public void disconnect() {
        // todo
        mDriver.releasePort(mPort.getPortNumber());
    }

    public void run() {
        Command cmd = new Command(mEpBase,"SET_AUTOMATIC_TRIGGER");
        EndpointRadarBase.setAutomaticFrameTrigger(100000); //100000us = 100ms
        ProtocolWorker worker = new ProtocolWorker(mListener, mMsgPipe, cmd);
        AsyncTask.execute(worker);
    }

    public void stop() {
        Command cmd = new Command(mEpBase,"SET_AUTOMATIC_TRIGGER");
        mEpBase.setAutomaticFrameTrigger(0);
        ProtocolWorker worker = new ProtocolWorker(mListener, mMsgPipe, cmd);
        AsyncTask.execute(worker);
    }

    public void getTargets() {
        Command cmd = new Command(mEpTargetDetect, "GET_TARGETS");
        ProtocolWorker worker = new ProtocolWorker(mListener, mMsgPipe, cmd);
        AsyncTask.execute(worker);
    }

    public void getTargetsRepeat(int interval) {
        Command cmd = new Command(mEpTargetDetect, "GET_TARGETS", true, interval);
        ProtocolWorker worker = new ProtocolWorker(mListener, mMsgPipe, cmd);
        AsyncTask.execute(worker);
    }

    public boolean isConnected() {
        return connected;
    }
}
