package com.la.radarhost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.la.radar.Radar;
import com.la.radar.RadarData;
import com.la.radar.RadarDataListener;
import com.la.radar.RadarManager;


public class ConsoleActivity extends AppCompatActivity implements RadarDataListener {
    private final static String TAG = ConsoleActivity.class.getSimpleName();

    //todo extend the general Text to a specific one
    // view-about
    private TextView mTxtState;
    private TextView mTxtTargets;
    private Button mBtnRun;
    private Button mBtnTargets;
    private RadiusVariationView mViewRadius;
    private LayoutInflater mInflater;
    private ViewGroup mContentview;

    private Button mBtnDsp;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.main_dparams);

        mInflater = getLayoutInflater();
        mContentview = findViewById(R.id.rootlayout);

        mBtnRun = findViewById(R.id.btn_run);
        mBtnTargets = findViewById(R.id.btn_targets);
        mTxtState = findViewById(R.id.txt_state);
        mTxtTargets = findViewById(R.id.txt_targets);
        mViewRadius = findViewById(R.id.view_radius);

        mBtnDsp = findViewById(R.id.btn_dsp);
        mBtnDsp.setOnClickListener(new BtnDspListener());

        mBtnRun.setOnClickListener(new BtnRunListener());
        mBtnTargets.setOnClickListener(new BtnTargetsListener());

        mRadarManager = new RadarManager(this);
        mRadar = mRadarManager.getRadarInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRadarManager.registerListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mRadarManager.unregisterListener(this);
    }

    @Override
    public void onDataChanged(RadarData data) {
        Log.e(TAG, "new event occur");
//        switch (data.getDataType()) {
//
//                break;
//            default:
//                break;
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private class BtnRunListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mBtnRun.isActivated()) {
                Log.e(TAG, "<runBtn onClick> finish");
                mBtnRun.setActivated(false);
                mBtnRun.setText("Run");
            } else {
                Log.e(TAG, "<runBtn onClick> start");
                mBtnRun.setActivated(true);
                mBtnRun.setText("Running");
            }
        }
    }

    private class BtnTargetsListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mBtnTargets.isActivated()) {
                Log.e(TAG, "<btnTarget onClick> inactivated");
//                mSensor.finish();
                mBtnTargets.setActivated(false);
//                mBtnTargets.setText("Run");
            } else {
                Log.e(TAG, "<btnTarget onClick> activated");
                mBtnTargets.setActivated(true);
//                mBtnTargets.setText("Running");
            }
        }
    }

    private class BtnDspListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new ConfigWindow(mInflater, mRadar, mRadarManager, FIELDS_DSP_CONFIG).show(mContentview);
        }
    }
}




