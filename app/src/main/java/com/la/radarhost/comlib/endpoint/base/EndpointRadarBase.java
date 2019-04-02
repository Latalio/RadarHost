package com.la.radarhost.comlib.endpoint.base;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;

import com.la.radarhost.comlib.protocol.EndpointDefinition;

public class EndpointRadarBase {

    /**
     * \internal
     * \defgroup EndpointRadarBaseCommandCodes
     *
     * \brief There are the command codes to identify the payload type.
     * 确定负载信息类型的命令码。
     *
     * Each payload message of the supported endpoint starts with one of these commend codes.
     *
     */
    public static final byte MSG_FRAME_DATA = 0x00;
    public static final byte MSG_GET_FRAME_DATA = 0x01;
    /**
     * A message to configure the automatic frame trigger.
     * @see EndpointRadarBase#setAutomaticFrameTrigger(UsbDeviceConnection, UsbEndpoint, long)
     */
    public static final byte MSG_SET_AUTOMATIC_TRIGGER = 0x02;
    public static final byte MSG_ENABLE_TEST_MODE = 0x03;
    public static final byte MSG_GET_DRIVER_VERSION = 0x20;
    public static final byte MSG_SET_DRIVER_VERSION = 0x21;
    public static final byte MSG_GET_DEVICE_INFO = 0x22;
    public static final byte MSG_SET_DEVICE_INFO = 0x23;
    public static final byte MSG_GET_TEMPRATURE = 0x30;
    public static final byte MSG_SET_TEMPRATURE = 0x31;
    public static final byte MSG_GET_TX_POWER = 0x32;
    public static final byte MSG_SET_TX_POWER = 0x33;
    public static final byte MSG_GET_CHRIP_DURATION = 0x34;
    public static final byte MSG_SET_CHRIP_DURATION = 0x35;
    public static final byte MSG_GET_MIN_INTERVAL = 0x36;
    public static final byte MSG_SET_MIN_INTERVAL = 0x37;
    public static final byte MSG_GET_FRAME_FORMAT = 0x40;
    public static final byte MSG_SET_FRAME_FORMAT = 0x41;

    public static final EndpointDefinition epRadarBaseDefinition = new EndpointDefinition(
            0x52424153L, 1, 1, "ifxRadarBase"
    );


    private int parseFrameInfo(UsbDeviceConnection connection,
                               UsbEndpoint endpoint,
                               final byte[] payload,
                               int payload_size) {
        final int header_size = 18;

        return 1;

    }


//    int setAutomaticFrameTrigger(UsbDeviceConnection connection,
//                                 UsbEndpoint endpoint,
//                                 long frame_interval_us) {
//
//
//        }
}
