package com.devsectech.photomo.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;

public class BitmapHelper {
    public static String TAG;

    static {
        StringBuilder outline24 = new StringBuilder();
        outline24.append(BitmapHelper.class.getSimpleName());
        TAG = outline24.toString();
    }


    public static Bitmap getBitmap2(View view) {
        int width = view.getWidth();
        int height = view.getHeight();
        view.measure(MeasureSpec.makeMeasureSpec(width, 1073741824), MeasureSpec.makeMeasureSpec(height, 1073741824));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        view.draw(new Canvas(createBitmap));
        return createBitmap;
    }

}
