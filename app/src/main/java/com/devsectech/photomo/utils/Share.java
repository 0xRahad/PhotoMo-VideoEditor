package com.devsectech.photomo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Share {

    public static final String IMAGE_PATH;
    public static Uri SAVED_BITMAP = null;
    public static String croppedImage;
    public static Boolean has_text;
    public static String imageUrl;
    public static Boolean is_sticker_mode;

    public static ArrayList<String> lst_album_image = new ArrayList<>();

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getPath());
        sb.append(File.separator);
        sb.append("Gif Effect Display Picture");
        IMAGE_PATH = sb.toString();
        Boolean valueOf = Boolean.valueOf(false);
        has_text = valueOf;
        is_sticker_mode = valueOf;
    }

    Exception e = null;

    public static Boolean RestartApp(Activity activity) {

        Intent launchIntentForPackage = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        launchIntentForPackage.addFlags(67108864);
        activity.startActivity(launchIntentForPackage);
        return Boolean.valueOf(false);
    }

    public static String saveFaceInternalStorage(Bitmap bitmap, Context context) {
        // Throwable th = null;

        File file = new File(new ContextWrapper(context).getDir("imageDir", 0), "profile.png");
        if (bitmap != null) {
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(file);
                try {
                    bitmap.compress(CompressFormat.PNG, 100, fileOutputStream2);
                } catch (Exception e) {
                    fileOutputStream = fileOutputStream2;
                    try {
                        e.printStackTrace();
                        fileOutputStream.close();
                        return file.getAbsolutePath();
                    } catch (Throwable th) {
                    }
                }
                try {
                    fileOutputStream2.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            } catch (Exception e4) {
                e4.printStackTrace();
            }
        } else {
            Log.e("TAGF", "Not Saved Image ------------------------------------------------------->");
        }
        return file.getAbsolutePath();
    }

    public class KEYNAME {
        public static final String ALBUM_NAME = "album_name";
        public static final String SELECTED_PHONE_IMAGE = "selected_phone_image";

        public KEYNAME() {
        }
    }
}
