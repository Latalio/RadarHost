package com.la.radarhost.comlib;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.la.radarhost.comlib.comport.driver.UsbSerialConstant;
import com.la.radarhost.comlib.comport.driver.UsbSerialDriver;
import com.la.radarhost.comlib.comport.driver.UsbSerialPort;
import com.la.radarhost.comlib.endpoint.base.EndpointRadarBase;
import com.la.radarhost.comlib.endpoint.targetdetection.DspSettings;
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

    private Radar mRadar;
    private RadarEventListener mEventListener = new RadarEventListener();
    private RadarDataListener mDataListener;

    public static final RadarData RADARDATA_ERROR_TIMEOUT = new RadarData() {
        @Override
        public int getDataType() {
            return RadarData.ERROR_TIMEOUT;
        }
    };


    class RadarEventListener {

        void onEventOccurred(RadarEvent event) {
            // status message
            if (event == RadarEvent.EVENT_TIMEOUT) {
                mDataListener.onDataChanged(RADARDATA_ERROR_TIMEOUT);
            } else if (event.obj == null) {
                if (event.status) { // todo whether to consider status code
                    mRadar.updateConfig();
                } else {
                    mRadar.unstageConfig();
                }
                mDataListener.onDataChanged((RadarData)event);
            } else {
                switch (event.type) {
                    case RadarEvent.TYPE_GET_DSP_SETTINGS:
                        mRadar.updateConfig((RadarConfig)event.obj); break;
                    case RadarEvent.TYPE_GET_TARGETS:
                        mDataListener.onDataChanged((RadarData)event.obj); break;
                    case RadarEvent.TYPE_GET_RANGE_THRESHOLD: //todo how classify range threshold?
                        mDataListener.onDataChanged((RadarData)event.obj); break;
                    default: break;
                }
            }
        }
    }

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

    public void registerListener(RadarDataListener listener, RadarConfig[] configs) {
        mDataListener = listener;
        connect();

        mMsgPipe = new MessagePipeline(mPort, mEventListener);
        mMsgPipe.start();
        mScheduler = new CommandScheduler();
        mScheduler.start();

        // radar configuration
        sendConfig(configs);
    }

    public void registerListener(RadarDataListener listener, RadarConfig config) {
        RadarConfig[] configs = {config};
        registerListener(listener, configs);
    }

    public void unregisterListener(RadarDataListener listener) {
        //
        mScheduler.terminate();

        untrigger();

        mMsgPipe.terminate();

        disconnect();
    }

    public void sendConfig(RadarConfig config) {
        mRadar.stageConfig(config);
        switch (config.getConfigType()) {
            case RadarConfig.TYPE_DSP_SETTINGS:
                mEpTargetDetect.setDspSettings((DspSettings)config); break;
            default: break;
        }

    }

    public void sendConfig(RadarConfig[] configs) {
        for (RadarConfig config: configs) {
            sendConfig(config);
        }
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
