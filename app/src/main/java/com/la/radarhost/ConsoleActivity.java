package com.la.radarhost;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.la.radarhost.comlib.ProtocolWorker;
import com.la.radarhost.comlib.endpoint.targetdetection.TargetInfo;

public class ConsoleActivity extends AppCompatActivity {
    private final static String TAG = ConsoleActivity.class.getSimpleName();

    //todo extend the general Text to a specific one
    // view-about
    private TextView mTxtState;
    private TextView mTxtTargets;
    private Button mBtnRun;
    private Button mBtnTargets;
    private RadiusVariationView mViewRadius;


    // radar-about
    private D2GRadar mSensor;

    private Handler mHandler;

    private ProtocolWorker mTargetDetectWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.main_tparams);

        mBtnRun = findViewById(R.id.btn_run);
        mBtnTargets = findViewById(R.id.btn_targets);
        mTxtState = findViewById(R.id.txt_state);
        mTxtTargets = findViewById(R.id.txt_targets);
        mViewRadius = findViewById(R.id.view_radius);

        mBtnRun.setOnClickListener(new BtnRunListener());
        mBtnTargets.setOnClickListener(new BtnTargetsListener());

        mSensor = new D2GRadar();
        mSensor.connect(this);

        // things need to do here
        // 1.check device's existence
        //  if exist then go on(go to 2)
        //  el indicate the user to plug in the device
        // 2.open the device (consider success or failure)
        // 3.launch the MsgPipe

        mHandler = new MsgHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                Log.d(TAG, "<runBtn onClick> stop");
                mSensor.stop();
                mBtnRun.setActivated(false);
                mBtnRun.setText("Run");
            } else {
                Log.d(TAG, "<runBtn onClick> start");
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
                Log.d(TAG, "<btnTarget onClick> inactivated");
//                mSensor.stop();
                mBtnTargets.setActivated(false);
//                mBtnTargets.setText("Run");
            } else {
                Log.d(TAG, "<runBtn onClick> activated");
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
                case D2GRadar.MT_GET_TARGETS:
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
