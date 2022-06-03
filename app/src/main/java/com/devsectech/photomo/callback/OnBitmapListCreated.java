package com.devsectech.photomo.callback;

import android.graphics.Bitmap;
import java.util.List;

public interface OnBitmapListCreated {
    void onBitmapRecived(List<Bitmap> list);
}
