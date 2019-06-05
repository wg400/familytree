package com.wanggang.familytree.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public abstract class CombinedBaseView extends LinearLayout {

    protected boolean initlized = false;

    public CombinedBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CombinedBaseView(Context context) {
        super(context);
    }

    abstract protected int layoutResource();

    abstract protected void onCreate(Context context);

    public boolean initlized() {
        return initlized;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if (!initlized) {
            initlized = true;
            LayoutInflater.from(getContext()).inflate(layoutResource(), this, true);
            onCreate(getContext());
        }
    }

}
