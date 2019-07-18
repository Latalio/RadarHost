package com.la.radarhost;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.util.Log;

import com.la.radarhost.comlib.Command;
import com.la.radarhost.comlib.CommandScheduler;
import com.la.radarhost.comlib.MessageParser;
import com.la.radarhost.comlib.MessagePipeline;
import com.la.radarhost.comlib.RadarConfiguration;
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


public class RadarManager {
    private final static String TAG = RadarManager.class.getSimpleName();

    private final Context context;

    private EndpointZero mEpZero;
    private EndpointRadarBase mEpBase;
    private EndpointTargetDetection mEpTargetDetect;

    private MessagePipeline mMsgPipe;
    private CommandScheduler mScheduler;

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbSerialDriver mDriver;
    private UsbSerialPort mPort;

    // todo checkout vendor and product id
    private final int D2G_VENDOR_ID = 1419;
    private final int D2G_PRODUCT_ID = 88;


    public RadarManager(Context context) {
        this.context = context;

        mEpZero = new EndpointZero();
        mEpBase = new EndpointRadarBase();
        mEpTargetDetect = new EndpointTargetDetection();
    }

    //
    public void registerListener(RadarEventListener listener, Radar radar, RadarConfiguration[] configs) {
        connect();

        mMsgPipe = new MessagePipeline(mPort, listener);
        mMsgPipe.start();
        mScheduler = new CommandScheduler();
        mScheduler.start();

        // radar configuration
    }



    public void unregisterListener(RadarEventListener listener) {
        //
        mScheduler.terminate();

        untrigger();

        mMsgPipe.terminate();

        disconnect();
    }

    public void updateConfiguration(RadarConfiguration config) {

    }

    //TODO try to throw some errors
    private boolean connect() {
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        // 1. search target device
        boolean found = false;
        HashMap<String, UsbDevice> devList = mUsbManager.getDeviceList();
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
            Log.e(TAG, "no device found.");
            return false;
        }

        // 2. build and configure COM port
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
            Log.e(TAG, "COM port open failed.");
            return false;
        }

        return true;
    }

    public void disconnect() {
        // todo
        mDriver.releasePort(mPort.getPortNumber());
    }

    public void trigger() {
        Command cmd = new Command(mEpBase, mEpBase.setAutomaticFrameTrigger(100000));
        mMsgPipe.addCommand(cmd);
    }

    private void untrigger() {
        Command cmd = new Command(mEpBase, mEpBase.setAutomaticFrameTrigger(0));
        mMsgPipe.addCommand(cmd);
    }

    private void configure() {

    }

    public void getTargets() {
        Command cmd = new Command(mEpTargetDetect, mEpTargetDetect.getTargets());
        mMsgPipe.addCommand(cmd);
    }

    public void getTargetsRepeat(int interval) {
        mScheduler.setInterval(interval);
        mScheduler.addCommand(new Runnable() {
            @Override
            public void run() {
                Command cmd = new Command(mEpTargetDetect, mEpTargetDetect.getTargets());
                mMsgPipe.addCommand(cmd);
            }
        });
        Log.e(TAG, "getTargetsRepeat() entered.");


    }
}
