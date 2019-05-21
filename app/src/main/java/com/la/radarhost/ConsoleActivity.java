package com.la.radarhost;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.la.radarhost.comlib.comport.COMPort;
import com.la.radarhost.comlib.comport.driver.UsbSerialDriver;
import com.la.radarhost.comlib.endpoint.Endpoint;
import com.la.radarhost.comlib.endpoint.EndpointDefinition;
import com.la.radarhost.comlib.endpoint.targetdetection.EndpointTargetDetection;
import com.la.radarhost.comlib.protocol.Protocol;

import java.util.HashMap;

public class ConsoleActivity extends AppCompatActivity {
    private final static String TAG = ConsoleActivity.class.getSimpleName();

    // view-about
    private TextView msgText;
    private boolean sConnected = false;
    private boolean sStarted = false;

    // radar-about
    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbSerialDriver mDriver;
    private COMPort mCOMPort;
    private Protocol mProtocol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        msgText = findViewById(R.id.msgText);
        Button connBtn = findViewById(R.id.connBtn);
        Button startBtn = findViewById(R.id.startBtn);

        connBtn.setOnClickListener(new ConnBtnListener());
        startBtn.setOnClickListener(new StartBtnListener());

        // radar-wise init
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devList = mUsbManager.getDeviceList();
        mDevice = (UsbDevice) devList.values().toArray()[0];
        mDriver = new UsbSerialDriver(mUsbManager, mDevice);
        mCOMPort = new COMPort();
        mCOMPort.initialize(mDriver);
        mProtocol = new Protocol(mCOMPort);
    }

    private class ConnBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (sConnected) {
                sConnected = false;
                mProtocol.disconnect();
            } else {
                int statusCode = mProtocol.connect();
                if (statusCode < 0) {
                    msgText.append(String.format(
                            "Protocol connection failed. Error Code: %d",
                            statusCode
                    ));
                    return;
                }
                msgText.append(String.format(
                        "Protocol connected. Handle: %d",
                        statusCode));
                Endpoint[] eps = mProtocol.getDevEndpoints();
                for (Endpoint ep:eps) {
                    msgText.append(String.format(
                            "%s", ep.epHostDef.description));
                }
            }
        }
    }

    private class StartBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (sStarted) {
                sStarted = false;

            } else {
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

    private Endpoint getSpecificEndpoint(EndpointDefinition epd) {
        Endpoint endpoint = null;
        Endpoint[] endpoints = mProtocol.getDevEndpoints();
        for(int i=0;i<mProtocol.getNumEndpoints();i++) {
            if (endpoints[i].epHostDef.equals(epd)) {
                endpoint = endpoints[i];
                break;
            }
        }
        return endpoint;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    /**
     * setter and getter
     */

}
