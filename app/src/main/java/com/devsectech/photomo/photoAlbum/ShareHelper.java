package com.devsectech.photomo.photoAlbum;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ShareCompat.IntentBuilder;
import androidx.core.content.FileProvider;

import com.devsectech.photomo.R;

import java.io.File;

public class ShareHelper {
    public static String FACEBOOK = "com.facebook.katana";
    private static String FACEBOOK_NOT_INSATLLED = "Facebook have not been installed...";
    private static String INSATAGRAM_NOT_INSATLLED = "Instagram have not been installed...";
    public static String INSTAGRAM = "com.instagram.android";
    private static String TYPE_GIF = "image/gif";
    private static String TYPE_VIDEO = "video/*";
    public static String WHATSAPP = "com.whatsapp";
    private static String WHATSAPP_NOT_INSATLLED = "WhatsApp have not been installed...";

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("JNP__");
        sb.append(ShareHelper.class.getSimpleName());
    }

    public static void share(Activity activity, String str, boolean z) {
        try {
            share(activity, str, null, z);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void share(Activity activity, String str, String str2, boolean z) {
        if (str2 != null) {
            try {
                if (activity.getPackageManager().getLaunchIntentForPackage(str2) == null) {
                    String str3 = null;
                    if (str2.equals(WHATSAPP)) {
                        str3 = WHATSAPP_NOT_INSATLLED;
                    } else if (str2.equals(INSTAGRAM)) {
                        str3 = INSATAGRAM_NOT_INSATLLED;
                    } else if (str2.equals(FACEBOOK)) {
                        str3 = FACEBOOK_NOT_INSATLLED;
                    }
                    Toast.makeText(activity, str3, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                    StringBuilder sb = new StringBuilder();
                    sb.append("market://details?id=");
                    sb.append(str2);
                    intent.setData(Uri.parse(sb.toString()));
                    activity.startActivity(intent);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
       // StringBuilder sb2 = new StringBuilder();
       // sb2.append(activity.getPackageName());
       // sb2.append(".FileProvider");
        Uri uriForFile = null;
//        FileProvider.getUriForFile(activity, sb2.toString(), new File(str));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            uriForFile = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", new File(str));
        }else {
            uriForFile = Uri.parse(str);
        }

        Intent intent2 = IntentBuilder.from(activity).getIntent();
        StringBuilder sb3 = new StringBuilder();
        sb3.append("share: ");
        sb3.append(uriForFile.toString());
        Log.e("URI_FILE", sb3.toString());
        intent2.setAction("android.intent.action.SEND");
        if (z) {
            intent2.setType(TYPE_GIF);
            intent2.setDataAndType(uriForFile, TYPE_GIF);
        } else {
            intent2.setType(TYPE_VIDEO);
            intent2.setDataAndType(uriForFile, TYPE_VIDEO);
        }
        if (str2 != null) {
            intent2.setPackage(str2);
        }
        intent2.putExtra("android.intent.extra.STREAM", uriForFile);
        intent2.putExtra("android.intent.extra.SUBJECT", activity.getResources().getString(R.string.app_name));
        activity.startActivity(Intent.createChooser(intent2, "Select"));
    }
}
