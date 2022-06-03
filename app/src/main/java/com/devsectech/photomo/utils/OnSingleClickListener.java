package com.devsectech.photomo.utils;

import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class OnSingleClickListener implements OnClickListener {
    public long mLastClickTime;

    public final void onClick(View view) {
        long uptimeMillis = SystemClock.uptimeMillis();
        long j = uptimeMillis - this.mLastClickTime;
        this.mLastClickTime = uptimeMillis;
        if (j > 1000) {
            onSingleClick(view);
        }
    }

    public abstract void onSingleClick(View view);
}
