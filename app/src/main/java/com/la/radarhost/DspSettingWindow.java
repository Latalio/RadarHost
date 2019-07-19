package com.la.radarhost;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.la.radar.Radar;
import com.la.radar.RadarConfig;
import com.la.radar.RadarConfigListener;
import com.la.radar.RadarManager;
import com.la.radar.endpoint.targetdetection.DspSetting;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DspSettingWindow extends SettingWindow implements RadarConfigListener {
    private final String TAG = DspSettingWindow.class.getSimpleName();

    private Button mBtnConfirm;
    private DspSetting mSetting;

    private TableLayout mForm;

    private final String[] fieldList = {
            "range_mvg_avg_length",
            "range_thresh_type",
//            "min_range_cm",
//            "max_range_cm",
//            "min_speed_kmh",
//            "max_speed_kmh",
//            "min_angle_degree",
//            "max_angle_degree",
//            "range_threshold",
//            "speed_threshold",
//            "adaptive_offset",
//            "enable_tracking",
//            "num_of_tracks",
//            "median_filter_length",
//            "enable_mti_filter",
//            "mti_filter_length"
    };

    public DspSettingWindow(LayoutInflater inflater, Radar radar, RadarManager manager) {
        // 0. window initialize
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 1. inflate layout
        FrameLayout contentView = (FrameLayout)inflater.inflate(R.layout.setting_dsp, null);
        setContentView(contentView);
        // 2. obtain Form and Button
        LinearLayout linearLayout = (LinearLayout)(contentView.getChildAt(0));
        mBtnConfirm = (Button)linearLayout.getChildAt(2);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ScrollView scrollView = (ScrollView)linearLayout.getChildAt(1);
        mForm = (TableLayout)scrollView.getChildAt(0);
        // 3.
        mSetting = radar.getDspSetting();
        fill(mSetting);
    }

    @Override
    public void onConfigChanged(RadarConfig config) {
        fill((DspSetting)config);
    }


    void fill(DspSetting setting) {
        for (int i=0;i<fieldList.length;i++) {
            try {
                String fieldName = fieldList[i];
                String methodName = "get"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
                Method getter = DspSetting.class.getMethod(methodName);

                TableRow row = (TableRow)mForm.getChildAt(i);
                EditText editText = (EditText)row.getChildAt(1);
                editText.setText(""+getter.invoke(setting));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e2) {
                e2.printStackTrace();
            }



        }
        Log.e(TAG, "[END]fill");
    }

    void pull() {
        DspSetting setting = new DspSetting();
    }

    void show(View parent) {
        showAtLocation(parent, Gravity.CENTER,0,0);
    }


}
