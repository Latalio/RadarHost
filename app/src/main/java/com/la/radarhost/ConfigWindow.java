package com.la.radarhost;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.la.radar.Radar;
import com.la.radar.RadarConfig;
import com.la.radar.RadarConfigListener;
import com.la.radar.RadarManager;

public class ConfigWindow extends PopupWindow implements RadarConfigListener {
    private final String[] fields;
    private final TableLayout mForm;
    private RadarConfig mConfig;

    ConfigWindow(LayoutInflater inflater, final Radar radar, final RadarManager manager, final String[] fields) {
        // -1. params initialize
        this.fields = fields;
        // 0. window initialize
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 1. inflate layout
        FrameLayout contentView = (FrameLayout)inflater.inflate(R.layout.setting_dsp, null);
        setContentView(contentView);
        // 2. obtain Form and Button
        LinearLayout linearLayout = (LinearLayout)(contentView.getChildAt(0));
        Button btnConfirm = (Button)linearLayout.getChildAt(2);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pull();
                manager.setConfigRequest(mConfig);
            }
        });
        ScrollView scrollView = (ScrollView)linearLayout.getChildAt(1);
        mForm = (TableLayout)scrollView.getChildAt(0);
        // 3. fill the form
        mConfig = radar.getDspConfig();
        fill(mConfig);
    }

    private void fill(RadarConfig config) {
        for (int i = 0; i< fields.length; i++) {
            TableRow row = (TableRow) mForm.getChildAt(i);
            EditText editText = (EditText) row.getChildAt(1);
            editText.setText(config.getValue(fields[i]));
        }
    }

    private void pull() {
        for (int i = 0; i< fields.length; i++) {
            TableRow row = (TableRow)mForm.getChildAt(i);
            EditText editText = (EditText)row.getChildAt(1);
            mConfig.setValue(fields[i], editText.getText().toString());
        }
    }

    void show(View parent) {
        showAtLocation(parent, Gravity.CENTER,0,0);
    }

    @Override
    public void onConfigChanged(RadarConfig config) {
        fill(config);
    }
}
