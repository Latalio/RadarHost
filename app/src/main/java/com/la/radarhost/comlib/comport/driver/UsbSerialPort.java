package com.la.radarhost.comlib.comport.driver;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import java.io.IOException;

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

public class UsbSerialPort {
    private final String TAG = UsbSerialPort.class.getSimpleName();

    private UsbDevice mDevice = null;
    private UsbDeviceConnection mConnection = null;


    private static final int DEFAULT_READ_BUFFER_SIZE = 16 * 1024;
    private static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;

    private int mPortNumber = -1;
    // non-null when open()

    /** Internal read buffer.  Guarded by {@link #mReadBufferLock}. */
    private byte[] mReadBuffer;
    private byte[] mWriteBuffer;

    private final Object mReadBufferLock = new Object();
    private final Object mWriteBufferLock = new Object();


    private UsbInterface mControlInterface;
    private UsbInterface mDataInterface;

    private UsbEndpoint mControlEndpoint;
    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;

    UsbSerialPort() {
        mReadBuffer = new byte[DEFAULT_READ_BUFFER_SIZE];
        mWriteBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];
    }

    /*****************************************************
     * Internal Functions
     *****************************************************/
    private void openInterface() throws IOException {
        Log.d(TAG, "claiming interfaces, count=" + mDevice.getInterfaceCount());

        mControlInterface = mDevice.getInterface(0);
        Log.d(TAG, "Control iface=" + mControlInterface);
        // class should be USB_CLASS_COMM

        if (!mConnection.claimInterface(mControlInterface, true)) {
            throw new IOException("Could not claim control interface.");
        }

        mControlEndpoint = mControlInterface.getEndpoint(0);
        Log.d(TAG, "Control endpoint direction: " + mControlEndpoint.getDirection());

        Log.d(TAG, "Claiming data interface.");
        mDataInterface = mDevice.getInterface(1);
        Log.d(TAG, "data iface=" + mDataInterface);
        // class should be USB_CLASS_CDC_DATA

        if (!mConnection.claimInterface(mDataInterface, true)) {
            throw new IOException("Could not claim data interface.");
        }
        mReadEndpoint = mDataInterface.getEndpoint(1);
        Log.d(TAG, "Read endpoint direction: " + mReadEndpoint.getDirection());
        mWriteEndpoint = mDataInterface.getEndpoint(0);
        Log.d(TAG, "Write endpoint direction: " + mWriteEndpoint.getDirection());
    }

    private int sendAcmControlMessage(int request, int value, byte[] buf) {
        return mConnection.controlTransfer(
                USB_RT_ACM, request, value, 0, buf, buf != null ? buf.length : 0, 5000);
    }

    /*****************************************************
     * External Functions
     *****************************************************/
    public void open() throws IOException {
        if (mConnection != null) {
            throw new IOException("Already open");
        }

        boolean opened = false;
        try {
            openInterface();
            opened = true;
        } finally {
            if (!opened) {
                mConnection = null;
                // just to be on the save side
                mControlEndpoint = null;
                mReadEndpoint = null;
                mWriteEndpoint = null;
            }
        }
    }

    public void close() throws IOException {
        if (mConnection == null) {
            throw new IOException("Already closed");
        }
        mConnection.close();
        mConnection = null;
    }

    public int read(byte[] dest, int timeoutMillis) throws IOException {
        final int numBytesRead;
        synchronized (mReadBufferLock) {
            int readAmt = Math.min(dest.length, mReadBuffer.length);
            numBytesRead = mConnection.bulkTransfer(mReadEndpoint, mReadBuffer, readAmt,
                    timeoutMillis);
            if (numBytesRead < 0) {
                // This sucks: we get -1 on timeout, not 0 as preferred.
                // We *should* use UsbRequest, except it has a bug/api oversight
                // where there is no way to determine the number of bytes read
                // in response :\ -- http://b.android.com/28023
                if (timeoutMillis == Integer.MAX_VALUE) {
                    // Hack: Special case "~infinite timeout" as an error.
                    return -1;
                }
                return 0;
            }
            System.arraycopy(mReadBuffer, 0, dest, 0, numBytesRead);
        }
        return numBytesRead;
    }

    public int write(byte[] src, int timeoutMillis) throws IOException {
        // TODO(mikey): Nearly identical to FtdiSerial write. Refactor.
        int offset = 0;

        while (offset < src.length) {
            final int writeLength;
            final int amtWritten;

            synchronized (mWriteBufferLock) {
                final byte[] writeBuffer;

                writeLength = Math.min(src.length - offset, mWriteBuffer.length);
                if (offset == 0) {
                    writeBuffer = src;
                } else {
                    // bulkTransfer does not support offsets, make a copy.
                    System.arraycopy(src, offset, mWriteBuffer, 0, writeLength);
                    writeBuffer = mWriteBuffer;
                }

                amtWritten = mConnection.bulkTransfer(mWriteEndpoint, writeBuffer, writeLength,
                        timeoutMillis);
            }
            if (amtWritten <= 0) {
                throw new IOException("Error writing " + writeLength
                        + " bytes at offset " + offset + " length=" + src.length);
            }

            Log.d(TAG, "Wrote amt=" + amtWritten + " attempted=" + writeLength);
            offset += amtWritten;
        }
        return offset;
    }

    public void setParameters(int baudRate, int dataBits, int stopBits, int parity) {
        byte stopBitsByte;
        switch (stopBits) {
            case STOPBITS_1: stopBitsByte = 0; break;
            case STOPBITS_1_5: stopBitsByte = 1; break;
            case STOPBITS_2: stopBitsByte = 2; break;
            default: throw new IllegalArgumentException("Bad value for stopBits: " + stopBits);
        }

        byte parityBitesByte;
        switch (parity) {
            case PARITY_NONE: parityBitesByte = 0; break;
            case PARITY_ODD: parityBitesByte = 1; break;
            case PARITY_EVEN: parityBitesByte = 2; break;
            case PARITY_MARK: parityBitesByte = 3; break;
            case PARITY_SPACE: parityBitesByte = 4; break;
            default: throw new IllegalArgumentException("Bad value for parity: " + parity);
        }

        byte[] msg = {
                (byte) ( baudRate & 0xff),
                (byte) ((baudRate >> 8 ) & 0xff),
                (byte) ((baudRate >> 16) & 0xff),
                (byte) ((baudRate >> 24) & 0xff),
                stopBitsByte,
                parityBitesByte,
                (byte) dataBits};
        sendAcmControlMessage(SET_LINE_CODING, 0, msg);
    }

    /*****************************************************
     * Getter and setter
     *****************************************************/
    public void setDevice(UsbDevice device) {
        mDevice = device;
    }
    public void setConnection(UsbDeviceConnection connection) {
        mConnection = connection;
    }
    public void setPortNumber(int portNumber) {
        mPortNumber = portNumber;
    }
    public int getPortNumber() {
        return mPortNumber;
    }
}