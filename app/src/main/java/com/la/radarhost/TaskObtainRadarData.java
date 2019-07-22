package com.la.radarhost;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;

import com.la.radar.RadarManager;
import com.la.radar.endpoint.targetdetection.Target;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

class TaskObtainRadarData extends TaskThread {
    private final String TAG = TaskObtainRadarData.class.getSimpleName();

    private final ConsoleActivity mActivity;

    TaskObtainRadarData(ConsoleActivity activity) {
        mActivity =  activity;
    }

    @Override
    void idle() {
        if (start) {
            start = false;
            mState = ProcessState.PRE_OPERATING;
            ConsoleActivity.info("Radar data obtaining...");
        }
    }

    @Override
    void pre() {
        mCount = 0;

        sb.delete(0, sb.length());
        sb.append(Build.MODEL).append("\n");
        sb.append("index").append(",").append("stepTimestamp").append("\n");
        sb.append("target_id").append(",")
                .append("level").append(",")
                .append("radius").append(",")
                .append("azimuth").append(",")
                .append("elevation").append(",")
                .append("radial_speed").append(",")
                .append("azimuth_speed").append(",")
                .append("elevation_speed").append("\n");

        mState = ProcessState.OPERATING;
    }

    @Override
    void oper() {
        for(;;) {
            sb.append("#").append(mCount++).append(",")
                    .append(System.currentTimeMillis()).append("\n");

            for(Target target:mActivity.getTargets()) {
                sb.append(target.getTargetID()).append(",")
                        .append(target.getLevel()).append(",")
                        .append(target.getRadius()).append(",")
                        .append(target.getAzimuth()).append(",")
                        .append(target.getElevation()).append(",")
                        .append(target.getRadial_speed()).append(",")
                        .append(target.getAzimuth_speed()).append(",")
                        .append(target.getElevation_speed()).append("\n");
            }

            try {
                Thread.sleep(interval);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if (finish) {
                finish = false;
                mState = ProcessState.POST_OPERATING;
                break;
            }
        }
    }

    @Override
    void post() {
        mState = ProcessState.IDLE;

        String dirpath = ConsoleActivity.PATH_DATA;
        String filename = "/ra " + filenameDF.format(new Date()) + ".txt";

        try {
            File file = new File(dirpath + filename);
            if (!file.createNewFile()) {
                ConsoleActivity.error("File creation failed!");
                return;
            }

            FileWriter writer = new FileWriter(file);
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            ConsoleActivity.error("File storage failed!");
        }

        ConsoleActivity.info("Data obtained.");

    }
}
