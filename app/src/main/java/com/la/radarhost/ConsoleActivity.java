package com.la.radarhost;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.la.radarhost.comlib.RadarEvent;
import com.la.radarhost.comlib.RadarEventListener;
import com.la.radarhost.comlib.endpoint.targetdetection.TargetInfo;

public class ConsoleActivity extends AppCompatActivity implements RadarEventListener {
    private final static String TAG = ConsoleActivity.class.getSimpleName();


    //todo extend the general Text to a specific one
    // view-about
    private TextView mTxtState;
    private static TextView mTxtTargets;
    private Button mBtnRun;
    private Button mBtnTargets;
    private static RadiusVariationView mViewRadius;

    private PopupMenu


    // radar-about
    private RadarManager mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.main_dparams);

        mBtnRun = findViewById(R.id.btn_run);
        mBtnTargets = findViewById(R.id.btn_targets);
        mTxtState = findViewById(R.id.txt_state);
        mTxtTargets = findViewById(R.id.txt_targets);
        mViewRadius = findViewById(R.id.view_radius);

        mBtnRun.setOnClickListener(new BtnRunListener());
        mBtnTargets.setOnClickListener(new BtnTargetsListener());

        mSensor = new RadarManager(this);
        if (!mSensor.connect(this)) {
            Log.e(TAG, "mSensor connection failed.");
            Intent intent = new Intent(this, NoDevActivity.class);
            startActivity(intent);
        }

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
    public void onRadarChanged(RadarEvent event) {
        Log.e(TAG, "new event occur");
        switch (event.type) {
            case RadarEvent.TYPE_GET_TARGETS:
                TargetInfo[] targets = (TargetInfo[])event.obj;
                StringBuilder sb = new StringBuilder();
                for(TargetInfo target:targets) {
                    sb.append("id: ");
                    sb.append(target.getTargetID());
                    sb.append("\ndistance: ");
                    sb.append(target.getRadius());
                    sb.append("\nspeed: ");
                    sb.append(target.getRadial_speed());
                    sb.append("--------------------\n");
                }
//                mTxtTargets.setText(sb.toString());
                Log.e(TAG, sb.toString());

//                mViewRadius.update(targets[0].getRadius());
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
                mSensor.stop();
                mBtnRun.setActivated(false);
                mBtnRun.setText("Run");
            } else {
                Log.e(TAG, "<runBtn onClick> start");
                mSensor.run();
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

    private class MsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RadarManager.MT_GET_TARGETS:
                    TargetInfo[] targets = (TargetInfo[])msg.obj;
                    StringBuilder sb = new StringBuilder();
                    for(TargetInfo target:targets) {
                        sb.append("id: ");
                        sb.append(target.getTargetID());
                        sb.append("\ndistance: ");
                        sb.append(target.getRadius());
                        sb.append("\nspeed: ");
                        sb.append(target.getRadial_speed());
                        sb.append("--------------------\n");
                    }
                    mTxtTargets.setText(sb.toString());

                    mViewRadius.update(targets[0].getRadius());
                    break;
                default:
                    break;
            }

        }
    }



}
