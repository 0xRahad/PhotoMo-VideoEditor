package com.devsectech.photomo.utils;

import android.content.Context;

import com.devsectech.photomo.ApplicationClass;

public class SharedPref {

    public static final String B = ".com/app/";
    private static SharedPref instance;

    private android.content.SharedPreferences settings;

    private android.content.SharedPreferences.Editor editor;

    private SharedPref() {
        settings = ApplicationClass.getContext().getSharedPreferences("PhotoMotion", 0);
        editor = settings.edit();
    }

    public static SharedPref getInstance() {
        if (instance == null)
            instance = new SharedPref();
        return instance;
    }

    public static SharedPref getInstance(Context context) {
        if (instance == null)
            instance = new SharedPref();
        return instance;
    }

    public String getString(String key, String defValue) {

        return settings.getString(key, defValue);
    }

    public SharedPref setString(String key, String value) {

        editor.putString(key, value);
        editor.commit();

        return this;
    }

    public SharedPref setStatus(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();

        return this;
    }

    public int getInt(String key, int defValue) {

        return settings.getInt(key, defValue);
    }

    public SharedPref setInt(String key, int value) {

        editor.putInt(key, value);
        editor.commit();

        return this;
    }

    public boolean getBoolean(String key, boolean defValue) {

        return settings.getBoolean(key, defValue);
    }

    public SharedPref setBoolean(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();

        return this;
    }

    public SharedPref setLong(String key, long value) {

        editor.putLong(key, value);
        editor.commit();

        return this;
    }

    public long getLong(String key, long defValue) {

        return settings.getLong(key, defValue);
    }

    public void clearData() {

        editor.clear();
        editor.commit();
    }
}
