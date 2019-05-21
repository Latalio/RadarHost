package com.la.radarhost;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.la.radarhost.comlib.protocol.Protocol;

import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    UsbManager usbManager;
    UsbDevice usbDevice = null;
    UsbDeviceConnection usbDeviceConnection;
    TextView msgView;
    Button exitBtn;
    Button connBtn;
    private boolean isConnected = false;
//    debug port
//    private static final int idVendor = 4966;
//    private static final int idProduct = 261;
    private static final int idVendor = 1419;
    private static final int idProduct = 88;

    private PendingIntent pi;
    private final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        msgView = findViewById(R.id.msgText);
        exitBtn = findViewById(R.id.exitBtn);
        connBtn = findViewById(R.id.connBtn);
        connBtn.setText("Disconnected");

        pi = PendingIntent.getBroadcast(
                this, 0, new Intent(ACTION_USB_PERMISSION), 0
        );

        registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));

        usbDeviceConnection = connect();
        connBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    isConnected = false;
                    connBtn.setText("Disconnected");
                } else {
                    usbDeviceConnection = connect();
//                    Protocol protocol = new Protocol(usbDevice, usbDeviceConnection);
//                    int connStatus = protocol.connect();
//                    Log.d(TAG,"protocol error code: " + connStatus);
//                    protocol.disconnect();
//                    isConnected = true;


                    connBtn.setText("Connected");
                }
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
            }
        });





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    UsbDeviceConnection connect() {
        UsbDeviceConnection deviceConnection;

        /* Connect to the device. */
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
//            Log.d("MainActivity.init", String.format(
////                    "Vendor Id: %d\nProduct Id: %d\n",
////                    device.getVendorId(), device.getProductId()
////            ));
            if ((device.getVendorId() == idVendor) && (device.getProductId() == idProduct)) {
                usbDevice = device;
                break;
            }
        }

        if (usbDevice == null) {
            msgView.append("[INFO]: No target device found.\n");
            Log.d(TAG, "No target device found.");
            isConnected = false;
            connBtn.setText("Disconnected");
            return null;

        } else {
            Log.d(TAG, "Target device found.");
        }

        /* Require permission of the device. */
        if (!usbManager.hasPermission(usbDevice)) {
            Log.d(TAG, "No permission to the device");
            usbManager.requestPermission(usbDevice, pi);
        }

        if (usbManager.hasPermission(usbDevice)) {
            Log.d(TAG, "Permission obtained.");
        } else {
            Log.d(TAG, "Permission acquisition failed.");
            return null;
        }

        /* Open the device. */
        deviceConnection = usbManager.openDevice(usbDevice);
        if (deviceConnection == null) {
            Log.d(TAG, "Connection establish failed.");
            isConnected = false;
            connBtn.setText("Disconnected");
            return null;

        } else {
            Log.d(TAG, "Connection establish succeed.");
        }

        isConnected = true;
        connBtn.setText("Connected");
        return deviceConnection;
    }


    private static final String ACTION_USB_PERMISSION = "com.la.radarhost.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.d(TAG, "Permission got.");
                        }
                    } else {
                        Log.d(TAG, "Permission denied for device.");
                    }
                }
            }
        }
    };
}
