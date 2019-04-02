package com.la.radarhost.comlib.protocol;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

import com.la.radarhost.comlib.comport.InterfaceManager;
import com.la.radarhost.comlib.endpoint.base.EndpointRadarBase;

public class Protocol {
    /**
     *
     */
    UsbDevice mDevice;
    UsbDeviceConnection mConnection;

    InterfaceManager mIntfManager;
    UsbEndpoint inEndpoint;
    UsbEndpoint outEndpoint;

    /**
     * 实例化前，一定要检查device和connection的存在性
     */
    public Protocol(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection) {
        mDevice = usbDevice;
        mConnection = usbDeviceConnection;
    }

    public static final byte CNST_STARTBYTE_DATA = 0x5A;

    /**< A status message begins with this code type.*/
    public static final byte CNST_STARTBYTE_STATUS = 0x5B;


    public static final short CNST_END_OF_PAYLOAD = (short) 0xE0DB;

    public static final byte CNST_MSG_QUERY_ENDPOINT_INFO = 0x00;
    public static final byte CNST_MSG_ENDPOINT_INFO = 0X00;
    public static final byte CNST_MSG_QUERY_FW_INFO = 0x01;
    public static final byte CNST_MSG_FW_INFO = 0x01;
    public static final byte CNST_MSG_FIRMWARE_RESET = 0x02;
    public static final int CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG = 0x01000000;

    /**
     * Error codes
     */
    // public static final int PROTOCOL_ERROR_CONNECTION_NOT_EXIST = -1;

    public static final int PROTOCOL_ERROR_ENDPOINT_NOT_EXIST = -2000;

    /**
     *  These error codes are returned when the connection could not be
     * established.
     */
    public static final int PROTOCOL_ERROR_CONNECTION_NOT_EXIST = -100;
    public static final int PROTOCOL_ERROR_COULD_NOT_OPEN_INTERFACE = -101;
    private static final int TIMEOUT = 100;
    public static final int PROTOCOL_ERROR_RECEIVED_NO_MESSAGE = -1000;
    public static final int PROTOCOL_ERROR_RECEIVED_TIMEOUT = -1001;
    public static final int PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_END = -1003;
    public static final int PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_START = -1002;
    public static final int PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE = -102;
    public static final int PROTOCOL_CONNECTED = 1;

    public static final EndpointDefinition[] knownEndpoints = {
            EndpointRadarBase.epRadarBaseDefinition
    };

    private boolean isConnected = false;

    /**
     ===============================================================================================
        6. Local Functions
     ===============================================================================================
     */

    private void recoverFromReceiveError() {
        /* read until buffer is empty */
        byte[] dummy_data = new byte[1024];
        int received_bytes = dummy_data.length;
        while (received_bytes == dummy_data.length) {
            received_bytes = mConnection.bulkTransfer(
                    outEndpoint, dummy_data, dummy_data.length, TIMEOUT);
        }
        /* now we have run out of data, protocol should be in sync again */
    }
    private void sendMessage(int endpoint_num,
                     final byte[] payload,
                     int payload_size) {
        /* setup message header and tail */
        byte[] message_header = new byte[4];
        byte[] message_tail = new byte[2];

        message_header[0] = CNST_STARTBYTE_DATA;
        message_header[1] = (byte) endpoint_num;
        message_header[2] = (byte) payload_size;
        message_header[3] = (byte)(payload_size >> 8);

        message_tail[0] = (byte) CNST_END_OF_PAYLOAD;
        message_tail[1] = (byte)(CNST_END_OF_PAYLOAD >> 8);

        /* send message */
//        connection.bulkTransfer();
        int i = mConnection.bulkTransfer(outEndpoint, message_header, message_header.length, TIMEOUT);
        Log.d("sendMessage",Integer.toString(i));
        i = mConnection.bulkTransfer(outEndpoint, payload, payload_size, TIMEOUT);
        Log.d("sendMessage",Integer.toString(i));
        i = mConnection.bulkTransfer(outEndpoint, message_tail, message_tail.length, TIMEOUT);
        Log.d("sendMessage",Integer.toString(i));
    }

    private int getMessage(MessageInfo messageInfo) {
        byte[] message_header = new byte[4];
        int num_received_bytes;

        /* read message header */
        /* ------------------- */
        num_received_bytes =
                mConnection.bulkTransfer(inEndpoint,
                        message_header,
                        message_header.length,
                        TIMEOUT);
        Log.d("e",new String(new StringBuilder().append(num_received_bytes)));
        /*

         */
        if (num_received_bytes < message_header.length) {
            num_received_bytes +=
                    mConnection.bulkTransfer(
                            inEndpoint,
                            message_header,
                            num_received_bytes,
                            message_header.length-num_received_bytes,
                            TIMEOUT);
        }

        if (num_received_bytes == 0) return PROTOCOL_ERROR_RECEIVED_NO_MESSAGE;
        else if (num_received_bytes < message_header.length)  {
            recoverFromReceiveError();
            return PROTOCOL_ERROR_RECEIVED_TIMEOUT;
        }

        /* read rest of message */
        /* -------------------- */
        if (message_header[0] == CNST_STARTBYTE_DATA) {
            byte[] payload;
            int payload_size;
            byte[] message_tail = new byte[2];

            payload_size = (int)message_header[2] | ((int)message_header[3])<<8;
            payload = new byte[payload_size];

            num_received_bytes = mConnection.bulkTransfer(
                    inEndpoint, payload, payload_size, TIMEOUT);

            /* check if payload has been received completely */
            if (num_received_bytes < payload_size) {
                recoverFromReceiveError();
                return PROTOCOL_ERROR_RECEIVED_TIMEOUT;
            }

            /* check message tail */
            num_received_bytes = mConnection.bulkTransfer(
                    inEndpoint, message_tail, message_tail.length, TIMEOUT);

            if ((num_received_bytes != message_tail.length) ||
                    (message_tail[0] != (byte) CNST_END_OF_PAYLOAD) ||
                    (message_tail[1] != (byte)(CNST_END_OF_PAYLOAD >> 8))) {
                recoverFromReceiveError();
                return PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_END;
            }

            messageInfo.endpoint = message_header[1];
            messageInfo.payload = payload;
            messageInfo.payloadSize = payload_size;

            return CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG;
        } else if (message_header[0] == CNST_STARTBYTE_STATUS) {
            short endpoint = message_header[1];
            int status_code = (int) message_header[2] | ((int)message_header[3]) <<8;

            return ((int)endpoint << 16) | status_code;
        } else {
            recoverFromReceiveError();
            return PROTOCOL_ERROR_RECEIVED_BAD_MESSAGE_START;
        }
    }

    private long readPayload(final byte[] payload, int offset, int length) {
        long value = 0L;
        for (int i=0; i<length; i++) {
            value = value | payload[offset+i];
            value = value << 8;
        }
        return value;
    }


    /**
     ===============================================================================================
        7. Exported Functions
     ===============================================================================================
     */
    /**
     * 打开特定端口，并获取端点信息。
     */
    public int connect() {
        Instance protocolInstance = new Instance();
        MessageInfo messageInfo = new MessageInfo();
        int receiveStatus;

        /* initialize the interface and endpoints */
        /* -------------------------------------- */
        mIntfManager = new InterfaceManager(mDevice, mConnection);
        if (!mIntfManager.open()) return  PROTOCOL_ERROR_COULD_NOT_OPEN_INTERFACE;
        inEndpoint = mIntfManager.getInEndpoint();
        outEndpoint = mIntfManager.getOutEndpoint();

        /* get Endpoint information from device */
        /* ------------------------------------ */
        /* send a message woth command code to query endpoint info to endpoint 0 */
        byte[] uQueryMessage = { CNST_MSG_QUERY_ENDPOINT_INFO };
        sendMessage(0, uQueryMessage, uQueryMessage.length);

        /* read and parse replay message from connected device */
        /* --------------------------------------------------- */
        receiveStatus = getMessage(messageInfo);
        if (((receiveStatus != CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG)) ||
                (messageInfo.endpoint != 0) ||
                (messageInfo.payloadSize <2 ) ||
                (messageInfo.payload[0] != CNST_MSG_ENDPOINT_INFO)) {
            /* This not the expected payload, clean up and quit. */
            mIntfManager.close();
            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
        }

        /* read number of endpoints and check message size */
        protocolInstance.numEndpoints = messageInfo.payload[1];

        if ((messageInfo.payloadSize != 6*protocolInstance.numEndpoints + 2) ||
                (protocolInstance.numEndpoints == 0)) {
            mIntfManager.close();
            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
        }

        /* allocate array to hold endoint information */
        protocolInstance.endpoints = new Endpoint[protocolInstance.numEndpoints];
        /* iterate over all endpoint info records in the message */
        for(int i=0; i<protocolInstance.numEndpoints; ++i) {
            int j;
            Endpoint endpoint = protocolInstance.endpoints[i];

            /* read endpoint type and version from payload */
            endpoint.type = readPayload(messageInfo.payload, 2 + i*6, 4);
            endpoint.version = (int)readPayload(messageInfo.payload, 6 + i*6, 2);

            /* find endpoint implementation matching type and version */
            for (j=0; j<knownEndpoints.length; j++) {
                if ((knownEndpoints[j].type == endpoint.type) &&
                        (knownEndpoints[j].minVersion <= endpoint.version) &&
                        (knownEndpoints[j].maxVersion >= endpoint.version)) {
                    endpoint.endpointDefinition = knownEndpoints[j];
                    break;
                }
            }
        }

        /* free payload memory */
        messageInfo.payload = null;

        /* consume the expected status message */
        receiveStatus = getMessage(messageInfo);
        if (receiveStatus != ((/*endpoint*/0 << 16) | /*status code*/0x0000)) {
            if (receiveStatus == CNST_PROTOCOL_RECEIVED_PAYLOAD_MSG) {
                messageInfo.payload = null;
            }
            protocolInstance.endpoints = null;
            mIntfManager.close();
            return PROTOCOL_ERROR_DEVICE_NOT_COMPATIBLE;
        }

        /* register the handle in ProtocolManager */

        int newHandle = ProtocolManager.register(this);

        isConnected = true;
        return newHandle;
    }

    public void disconnect() {
        if (isConnected) {
            mIntfManager.close();
            ProtocolManager.unregister(this);
            isConnected = false;
        }
    }

}
