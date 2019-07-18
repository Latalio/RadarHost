package com.la.radarhost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.la.radarhost.comlib.RadarData;
import com.la.radarhost.comlib.RadarEvent;
import com.la.radarhost.comlib.RadarDataListener;
import com.la.radarhost.comlib.RadarManager;
import com.la.radarhost.comlib.endpoint.targetdetection.TargetInfo;

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
    private BaseAdapter


    // radar-about
    private RadarManager mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.main_dparams);

        mInflater = getLayoutInflater();
        mContentview = (ViewGroup)findViewById(R.id.rootlayout);

        mBtnRun = findViewById(R.id.btn_run);
        mBtnTargets = findViewById(R.id.btn_targets);
        mTxtState = findViewById(R.id.txt_state);
        mTxtTargets = findViewById(R.id.txt_targets);
        mViewRadius = findViewById(R.id.view_radius);

        mBtnDsp = findViewById(R.id.btn_dsp);
        mBtnDsp.setOnClickListener(new BtnDspListener());

        mBtnRun.setOnClickListener(new BtnRunListener());
        mBtnTargets.setOnClickListener(new BtnTargetsListener());

        // things need to do here
        // 1.check device's existence
        //  if exist then go on(go to 2)
        //  el indicate the user to plug in the device
        // 2.open the device (consider success or failure)
        // 3.launch the MsgPipe
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onDataChanged(RadarData data) {
        Log.e(TAG, "new event occur");
        switch (data.getDataType()) {

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensor.disconnect();



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
                mSensor.getTargetsRepeat(1000); //1000ms = 1s
                mBtnTargets.setActivated(true);
//                mBtnTargets.setText("Running");
            }
        }
    }

    private class BtnDspListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            View view = mInflater.inflate(R.layout.setting_dsp, null);
            PopupWindow window = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.showAtLocation(mContentview, Gravity.CENTER, 0,0);
        }
    }
}




