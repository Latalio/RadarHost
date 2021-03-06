package com.la.radar;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.la.radar.comport.driver.UsbSerialConstant;
import com.la.radar.comport.driver.UsbSerialDriver;
import com.la.radar.comport.driver.UsbSerialPort;
import com.la.radar.endpoint.base.EndpointRadarBase;
import com.la.radar.endpoint.targetdetection.DspConfig;
import com.la.radar.endpoint.targetdetection.EndpointTargetDetection;
import com.la.radar.endpoint.zero.EndpointZero;
import com.la.radar.protocol.Command;
import com.la.radar.protocol.CommandScheduler;
import com.la.radar.protocol.MessagePipeline;

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

    private Radar mRadar;
    private RadarEventListener mEventListener;

    public static final RadarData RADARDATA_ERROR_TIMEOUT = new RadarData() {
        @Override
        public int getDataType() {
            return RadarData.ERROR_TIMEOUT;
        }
    };

    public RadarManager(Context context) {
        this.context = context;

        mEpZero = new EndpointZero();
        mEpBase = new EndpointRadarBase();
        mEpTargetDetect = new EndpointTargetDetection();

    }

    public Radar getRadarInstance() {
        mRadar = new Radar();
        return mRadar;
    }

    public void registerListener(RadarDataListener listener, RadarConfig... configs)
            throws NoDeviceException, IOException {
        mEventListener = new RadarEventListener(mRadar, listener);
        connect();

        mMsgPipe = new MessagePipeline(mPort, mEventListener);
        mMsgPipe.start();
        mScheduler = new CommandScheduler();
        mScheduler.start();

        // radar configuration
        setConfigRequest(configs);
        getConfigRequest();
    }


    public void unregisterListener(RadarDataListener listener) {
        //
        mScheduler.terminate();

        untrigger();

        mMsgPipe.terminate();

        disconnect();
    }

    public void setConfigRequest(RadarConfig config) {
        if (config == null) return;
        mRadar.stageConfig(config);
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                Command cmd = new Command(mEpTargetDetect, mEpTargetDetect.setDspSettings((DspConfig)config));
                mMsgPipe .addCommand(cmd);
                break;
            default: break;
        }

    }

    public void setConfigRequest(RadarConfig[] configs) {
        for (RadarConfig config: configs) {
            setConfigRequest(config);
        }
    }

    private void getConfigRequest() {
        Command cmd = new Command(mEpTargetDetect, mEpTargetDetect.getDspSettings());
        mMsgPipe.addCommand(cmd);
    }

    private void connect() throws NoDeviceException, IOException {
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
        if (!found) throw new NoDeviceException();

        // 2. build and configure COM port
        mDriver = new UsbSerialDriver(mUsbManager, mDevice);
        int portNum = mDriver.requestPort();
        mPort = mDriver.getPort(portNum);
        mPort.open();
        mPort.setParameters(
                UsbSerialConstant.BAUDRATE_115200,
                UsbSerialConstant.DATABITS_8,
                UsbSerialConstant.STOPBITS_1,
                UsbSerialConstant.PARITY_NONE
        );
    }

    public void disconnect() {
        // todo
        mDriver.releasePort(mPort.getPortNumber());
    }

    public void trigger(long intervalMicros) {
        Command cmd = new Command(mEpBase, mEpBase.setAutomaticFrameTrigger(intervalMicros));
        mMsgPipe.addCommand(cmd);
    }

    public void trigger() {
        trigger(100000); //100000us = 100ms = 10Hz????
    }

    public void untrigger() {
        Command cmd = new Command(mEpBase, mEpBase.setAutomaticFrameTrigger(0));
        mMsgPipe.addCommand(cmd);
    }

    public void getTargets() {
        Command cmd = new Command(mEpTargetDetect, mEpTargetDetect.getTargets());
        mMsgPipe.addCommand(cmd);
    }

    public void getTargetsRepeat(int interval) {
        if (interval > 0 && !mScheduler.exists(CommandScheduler.INDEX_TARGETS)) {
            mScheduler.setInterval(interval);
            mScheduler.setCommand(CommandScheduler.INDEX_TARGETS, new Runnable() {
                @Override
                public void run() {
                    Command cmd = new Command(mEpTargetDetect, mEpTargetDetect.getTargets());
                    mMsgPipe.addCommand(cmd);
                }
            });
        } else {
            mScheduler.removeCommand(CommandScheduler.INDEX_TARGETS);
        }
    }
}
