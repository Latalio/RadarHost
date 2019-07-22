package com.la.radarhost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.la.radar.NoDeviceException;
import com.la.radar.Radar;
import com.la.radar.RadarData;
import com.la.radar.RadarDataListener;
import com.la.radar.RadarManager;
import com.la.radar.endpoint.StatusCode;
import com.la.radar.endpoint.targetdetection.Target;
import com.la.radar.endpoint.targetdetection.Targets;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;


public class ConsoleActivity extends AppCompatActivity implements RadarDataListener {
    private final static String TAG = ConsoleActivity.class.getSimpleName();

    //todo extend the general Text to a specific one
    // view-about
    TextView mTxtStatus;
    private TextView mTxtTargets;
    private Button mBtnTrigger;
    private Button mBtnTargets;
    private RadiusVariationView mViewRadius;
    private LayoutInflater mInflater;
    private ViewGroup mContentview;

    private Button mBtnDsp;
    private Button mBtnObtain;

    private static MessageHandler mHandler;


    // radar-about
    private RadarManager mRadarManager;
    private Radar mRadar;

    private final String[] FIELDS_DSP_CONFIG = {
            "range_mvg_avg_length",
            "range_thresh_type",
            "min_range_cm",
            "max_range_cm",
            "min_speed_kmh",
            "max_speed_kmh",
            "min_angle_degree",
            "max_angle_degree",
            "range_threshold",
            "speed_threshold",
            "adaptive_offset",
            "enable_tracking",
            "num_of_tracks",
            "median_filter_length",
            "enable_mti_filter",
            "mti_filter_length"
    };

    private int colorBtnActivated;
    private int colorBtnInactivated;

    static final int MSG_TOAST_SUCCESS_STATUS = 11;
    static final int MSG_TOAST_ERROR_STATUS = 12;
    static final int MSG_STATUS_TEXT = 13;

    @Getter private Target[] targets;

    public static final String PATH = "/sdcard/RadarHost";
    public static final String PATH_DATA = PATH + "/data";
    private static final SimpleDateFormat statusDateFormat = new SimpleDateFormat("[hh:mm:ss.SSS] ");

    private TaskObtainRadarData mTaskRadarData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.main_dparams);

        mInflater = getLayoutInflater();
        mContentview = findViewById(R.id.rootlayout);

        mBtnTrigger = findViewById(R.id.btn_run);
        mBtnTargets = findViewById(R.id.btn_targets);
        mTxtStatus = findViewById(R.id.txt_status);
        mTxtTargets = findViewById(R.id.txt_targets);
        mViewRadius = findViewById(R.id.view_radius);

        mBtnDsp = findViewById(R.id.btn_dsp);
        mBtnObtain = findViewById(R.id.btn_obtain);

        mBtnDsp.setOnClickListener(new BtnDspListener());
        mBtnObtain.setOnClickListener(new BtnObtainListener());

        mBtnTrigger.setOnClickListener(new BtnTriggerListener());
        mBtnTargets.setOnClickListener(new BtnTargetsListener());

        mRadarManager = new RadarManager(this);
        mRadar = mRadarManager.getRadarInstance();

        colorBtnActivated = getResources().getColor(R.color.btnActivated);
        colorBtnInactivated = getResources().getColor(R.color.btnInactivated);

        mHandler = new MessageHandler(this);
        checkDirs();

        mTaskRadarData = new TaskObtainRadarData(this);
        mTaskRadarData.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mRadarManager.registerListener(this);
        } catch (NoDeviceException e1) {
            //TODO do something when the device don't plug in
            error("No device found!");
        } catch (IOException e2) {
            //TODO do something when the COM port got some troubles
            error("COM port got some errors");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRadarManager.unregisterListener(this);
    }

    @Override
    public void onDataChanged(RadarData data) {
        switch (data.getDataType()) {
            case RadarData.TYPE_TARGETS:
                targets = ((Targets)data).getTargets();
                if (targets.length != 0) {
                    float radius = targets[0].getRadius();
                    mViewRadius.update(radius/50);
                }
                break;
            case RadarData.TYPE_STATUS_CODE:
                StatusCode statusCode = (StatusCode)data;
                if (statusCode.getStatus_code() == StatusCode.SUCCESS) {
                    Message msg = Message.obtain();
                    msg.what = MSG_TOAST_SUCCESS_STATUS;
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_TOAST_ERROR_STATUS;
                    msg.arg1 = statusCode.getStatus_code();
                    mHandler.sendMessage(msg);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void setRequestedOrientation(int requestedOrientation){
    }

    private class BtnTriggerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mBtnTrigger.isActivated()) {
                mRadarManager.untrigger();
                mBtnTrigger.setActivated(false);
                mBtnTrigger.setBackgroundColor(colorBtnInactivated);
            } else {
                mRadarManager.trigger();
                mBtnTrigger.setActivated(true);
                mBtnTrigger.setBackgroundColor(colorBtnActivated);
            }
        }
    }

    private class BtnTargetsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mBtnTargets.isActivated()) {
                mRadarManager.getTargetsRepeat(0);
                mBtnTargets.setActivated(false);
                mBtnTargets.setBackgroundColor(colorBtnInactivated);
            } else {
                mRadarManager.getTargetsRepeat(100);
                mBtnTargets.setActivated(true);
                mBtnTargets.setBackgroundColor(colorBtnActivated);
            }
        }
    }

    private class BtnObtainListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mBtnObtain.isActivated()) {
                mTaskRadarData.enableFinish();
                mBtnObtain.setActivated(false);
                mBtnObtain.setBackgroundColor(colorBtnInactivated);
            } else {
                if (mBtnTrigger.isActivated() && mBtnTargets.isActivated()) {
                    mTaskRadarData.setFrequency(20);
                    mTaskRadarData.enableStart();
                    mBtnObtain.setActivated(true);
                    mBtnObtain.setBackgroundColor(colorBtnActivated);
                } else {
                    ConsoleActivity.error("Radar is not activated！");
                }

            }
        }
    }

    private class BtnDspListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new ConfigWindow(mInflater, mRadar, mRadarManager, FIELDS_DSP_CONFIG).show(mContentview);
        }
    }

    static synchronized void info(String msg) {
        msg = String.format("<font color=\"#00FF00\">%s<br>",statusDateFormat.format(new Date()) + msg);
        Message message = Message.obtain();
        message.what = ConsoleActivity.MSG_STATUS_TEXT;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    static synchronized void error(String msg) {
        msg = String.format("<font color=\"#FF0000\">%s<br>",statusDateFormat.format(new Date()) + msg);
        Message message = Message.obtain();
        message.what = ConsoleActivity.MSG_STATUS_TEXT;
        message.obj = msg;
        mHandler.sendMessage(message);
    }

    private void checkDirs() {
        if (!checkDir(new File(PATH))) {
            ConsoleActivity.error("Root directory creation failed!");
        }
        if (!checkDir(new File(PATH_DATA))) {
            ConsoleActivity.error("Data directory creation failed!");
        }
    }

    private boolean checkDir(File file) {
        return file.exists() || file.mkdir();
    }
}




