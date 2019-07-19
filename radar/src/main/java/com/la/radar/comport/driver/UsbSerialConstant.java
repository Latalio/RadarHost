package com.la.radar.comport.driver;

import android.hardware.usb.UsbConstants;

public class UsbSerialConstant {
    /** baud rate. */
    public static final int BAUDRATE_115200 = 115200;

    /** data bits. */
    public static final int DATABITS_5 = 5;
    public static final int DATABITS_6 = 6;
    public static final int DATABITS_7 = 7;
    public static final int DATABITS_8 = 8;

    /** flow control. */
    public static final int FLOWCONTROL_NONE = 0;
    public static final int FLOWCONTROL_RTSCTS_IN = 1;
    public static final int FLOWCONTROL_RTSCTS_OUT = 2;
    public static final int FLOWCONTROL_XONXOFF_IN = 4;
    public static final int FLOWCONTROL_XONXOFF_OUT = 8;

    /** parity. */
    public static final int PARITY_NONE = 0;
    public static final int PARITY_ODD = 1;
    public static final int PARITY_EVEN = 2;
    public static final int PARITY_MARK = 3;
    public static final int PARITY_SPACE = 4;

    /** start bits. */
    public static final int STOPBITS_1 = 1;
    public static final int STOPBITS_1_5 = 3;
    public static final int STOPBITS_2 = 2;


    /** what's this?? */
    public static final int USB_RECIP_INTERFACE = 0x01;
    public static final int USB_RT_ACM = UsbConstants.USB_TYPE_CLASS | USB_RECIP_INTERFACE;

    public static final int SET_LINE_CODING = 0x20;  // USB CDC 1.1 section 6.2
    public static final int GET_LINE_CODING = 0x21;
    public static final int SET_CONTROL_LINE_STATE = 0x22;
    public static final int SEND_BREAK = 0x23;
}
