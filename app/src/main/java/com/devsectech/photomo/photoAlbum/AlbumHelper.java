package com.devsectech.photomo.photoAlbum;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class AlbumHelper {
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("AH__");
        sb.append(AlbumHelper.class.getSimpleName());
    }

    public static boolean isExtension(String str, String str2) {
        if (str2.equalsIgnoreCase(str.substring(str.lastIndexOf(".") + 1, str.length()))) {
            return true;
        }
        return false;
    }

    public static ArrayList<String> loadFiles(Context context) {
        ArrayList<String> arrayList = new ArrayList<>();
        File myDir = new File(Environment.getExternalStorageDirectory().toString() + File.separator + Environment.DIRECTORY_MOVIES + File.separator + "PhotoMotion");
        File[] files = myDir.listFiles();
        if (files != null) {
            for (File file : files) {
                arrayList.add(file.getAbsolutePath());
            }
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public static boolean delete(String str) {
        return new File(str).delete();
    }
}
