package com.la.radarhost.comlib.endpoint.base;

public class FrameInfo {
    float[] sample_data;
    long frame_number;
    long num_chirps;
    short num_tx_antennas;
    long num_sample_per_chirp;
    short rx_mask;
    short adc_resolution;
    short interleaved_rx;
    RxDataFormat data_format;
}
