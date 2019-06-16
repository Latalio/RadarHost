package com.la.radarhost.comlib.comport;

import android.util.Log;

import com.la.radarhost.comlib.comport.driver.UsbSerialConstant;
import com.la.radarhost.comlib.comport.driver.UsbSerialDriver;
import com.la.radarhost.comlib.comport.driver.UsbSerialPort;
import com.la.radarhost.comlib.comport.util.HexDump;

import java.io.IOException;

public class COMPort {
    private final String TAG = COMPort.class.getSimpleName();

    private UsbSerialPort mPort;

    private int timeout = 200;

    public COMPort(UsbSerialDriver driver) {
        int portNum = driver.requestPort();
        mPort = driver.getPort(portNum);
    }

    public int open() {
        try {
            mPort.open();
            mPort.setParameters(
                    UsbSerialConstant.BAUDRATE_115200,
                    UsbSerialConstant.DATABITS_8,
                    UsbSerialConstant.STOPBITS_1,
                    UsbSerialConstant.PARITY_NONE
            );

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "COM port open failed.");
            try {
                mPort.close();
            } catch (IOException e2) {
               e2.printStackTrace();
            }
        }

        Log.d(TAG, "COM port open succeed.");
        return mPort.getPortNumber();
    }

    public void close() {
        try {
            mPort.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "COM port close failed.");
        }

        Log.d(TAG, "COM port close succeed.");
    }

    public int sendData(byte[] data, int offset) {
        byte[] buffer = new byte[data.length-offset];
        System.arraycopy(data, offset, buffer, 0, buffer.length);

        int numSendBytes = 0;
        try {
            numSendBytes = mPort.write(buffer, timeout);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Send data failed.");
            return 0;
        }

        Log.d(TAG, String.format("Send data succeed. [%d bytes]", numSendBytes));
        return numSendBytes;
    }

    public int getData(byte[] data, int offset) {
        byte[] buffer = new byte[data.length-offset];
        int numGetBytes = 0;
        try {
            numGetBytes = mPort.read(buffer, timeout);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Get data failed.");
            return 0;
        }
        System.arraycopy(buffer, 0, data, offset, buffer.length);

        Log.d(TAG, String.format("Get data succeed. [%d bytes]", numGetBytes));
        Log.d(TAG, HexDump.toHexString(data));
        return numGetBytes;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
