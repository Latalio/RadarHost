package com.la.radarhost;

import android.content.Context;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.la.radarhost.comlib.Command;
import com.la.radarhost.comlib.MessagePipeline;
import com.la.radarhost.comlib.ProtocolWorker;
import com.la.radarhost.comlib.comport.driver.UsbSerialConstant;
import com.la.radarhost.comlib.comport.driver.UsbSerialDriver;
import com.la.radarhost.comlib.comport.driver.UsbSerialPort;
import com.la.radarhost.comlib.endpoint.base.EndpointRadarBase;
import com.la.radarhost.comlib.endpoint.targetdetection.EndpointTargetDetection;
import com.la.radarhost.comlib.endpoint.zero.EndpointZero;

import java.io.IOException;
import java.util.HashMap;

public class ConsoleActivity extends AppCompatActivity {
    private final static String TAG = ConsoleActivity.class.getSimpleName();

    // view-about
    private TextView msgText;
    private Button connBtn;
    private Button startBtn;
    private Button targetBtn;
    private boolean sConnected = false;
    private boolean sStarted = false;
    private boolean sTargeted = false;

    // radar-about
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbSerialDriver mDriver;
    private UsbSerialPort mPort;

    private MessagePipeline mMsgPipe;
    private ProtocolWorker mWorker;

    private Handler mHandler;
    private EndpointZero mEpZero;
    private EndpointRadarBase mEpBase;
    private EndpointTargetDetection mEpTargetDetect;

    private ProtocolWorker mTargetDetectWorker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.console);

        msgText = findViewById(R.id.msgText);
        connBtn = findViewById(R.id.connBtn);
        startBtn = findViewById(R.id.startBtn);
        targetBtn = findViewById(R.id.targetBtn);
        Log.e(TAG, connBtn.toString());

        connBtn.setOnClickListener(new ConnBtnListener());
        startBtn.setOnClickListener(new StartBtnListener());
        targetBtn.setOnClickListener(new TargetBtnListener());

        // radar-wise init
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devList = mUsbManager.getDeviceList();
        mDevice = (UsbDevice) devList.values().toArray()[0];
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
        }
        Log.d(TAG, "Port open succeed");

        mMsgPipe = new MessagePipeline(mPort);
        mMsgPipe.start();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String str = (String)msg.obj;
                msgText.setText(str);
            }
        };
        mEpZero = new EndpointZero();
        mEpBase = new EndpointRadarBase();
        mEpTargetDetect = new EndpointTargetDetection();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    private class ConnBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (sConnected) {
                connBtn.setText("连接");
                sConnected = false;
//
            } else {
                Command cmd = new Command("QUERY_ENDPOINT_INFO");
                ProtocolWorker worker = new ProtocolWorker(mHandler, mEpZero, cmd, mMsgPipe);
                worker.start();
                connBtn.setText("已连接");
                sConnected = true;



//                int statusCode = mProtocol.connect();
//                if (statusCode < 0) {
//                    msgText.append(String.format(
//                            "Protocol connection failed. Error Code: %d\n",
//                            statusCode
//                    ));
//                    return;
//                }
//                msgText.append(String.format(
//                        "Protocol connected. Handle: %d",
//                        statusCode));
//                Endpoint[] eps = mProtocol.getDevEndpoints();
//                for (Endpoint ep:eps) {
//                    msgText.append(String.format(
//                            "%s", ep.epHostDef.description));
//                }
            }
        }
    }

    private class StartBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (sStarted) {
                Log.d(TAG, "<startBtn onClick> stop");
                Command cmd = new Command("SET_AUTOMATIC_TRIGGER");
                ProtocolWorker worker = new ProtocolWorker(mHandler, mEpBase, cmd, mMsgPipe);
                mEpBase.setAutomaticFrameTrigger(0);
                worker.start();
                startBtn.setText("启动");
                startBtn.setActivated(false);
                sStarted = false;


            } else {
                Log.d(TAG, "<startBtn onClick> start");
                Command cmd = new Command("SET_AUTOMATIC_TRIGGER");
                mEpBase.setAutomaticFrameTrigger(100000); //100000us = 100ms
                ProtocolWorker worker = new ProtocolWorker(mHandler, mEpBase, cmd, mMsgPipe);
                worker.start();
                startBtn.setActivated(true);
                startBtn.setText("已启动");

                sStarted = true;

//                EndpointTargetDetection endpoint =
//                        (EndpointTargetDetection) getSpecificEndpoint(Endpoint.TARGET_DETECTION);
//                if (endpoint == null) {
//                    Log.d(TAG, "Target Detection Endpoint not Found.");
//                    return;
//                }
//                endpoint.
//                sStarted = true;
            }
        }
    }

    private class TargetBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (sTargeted) {

            } else {
                Command cmd = new Command("GET_TARGETS", true, 500);
                ProtocolWorker worker = new ProtocolWorker(mHandler, mEpTargetDetect, cmd, mMsgPipe);
                worker.start();
                targetBtn.setActivated(true);
                sTargeted = true;

            }
        }

    }

//    private Endpoint getSpecificEndpoint(EndpointDefinition epd) {
//        Endpoint endpoint = null;
//        Endpoint[] endpoints = mProtocol.getDevEndpoints();
//        for(int i=0;i<mProtocol.getNumEndpoints();i++) {
//            if (endpoints[i].epHostDef.equals(epd)) {
//                endpoint = endpoints[i];
//                break;
//            }
//        }
//        return endpoint;
//    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDriver.releasePort(mPort.getPortNumber());


    }


    /**
     * setter and getter
     */

}
