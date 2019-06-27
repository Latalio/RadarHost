package com.la.radarhost;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;

public class PushButton extends AppCompatButton {
    private boolean pushed = false;

    public PushButton(Context context) {
        super(context);
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return super.getOnFocusChangeListener();
    }

    public boolean isPushed() {
        return pushed;
    }


}
