package com.devsectech.photomo.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import java.lang.reflect.Field;
import java.util.HashMap;

public class TypefaceUtil {
    public static void overrideFont(Context context, String str, String str2) {
        Typeface createFromAsset = Typeface.createFromAsset(context.getAssets(), str2);
        int i = VERSION.SDK_INT;
        HashMap hashMap = new HashMap();
        hashMap.put("serif", createFromAsset);
        try {
            Field declaredField = Typeface.class.getDeclaredField("sSystemFontMap");
            declaredField.setAccessible(true);
            declaredField.set(null, hashMap);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }
}
