package com.hypnotriod.web_view_helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class PreferenceConnector {
    private static Gson gson;

    public static final String PREF_NAME = "APP_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;

    public static void writeObject(Context context, String key, Object object) {
        writeString(context, key, getGson().toJson(object));
    }

    public static <T> T readObject(Context context, String key, Type objectType)
    {
        String jsonString = readString(context, key, null);
        if(jsonString == null) return null;
        else return getGson().fromJson(jsonString, objectType);
    }

    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static boolean readBoolean(Context context, String key, boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();
    }

    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();
    }

    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Gson getGson() {
        if (gson == null) gson = new Gson();
        return gson;
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

}
