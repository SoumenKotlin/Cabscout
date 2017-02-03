package com.app.cabscout.model;

import android.content.Context;
import android.content.SharedPreferences;

/*
 * Created by rishav on 18/1/17.
 */

public class CSPreferences {

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("CS_PREF", Context.MODE_PRIVATE);
    }

    public static String readString(Context context, String key) {
        return getPreferences(context).getString(key, "");
    }

    public static void putString(Context context, String key, String value) {
         getPreferences(context).edit().putString(key, value).apply();
    }

    public static void getString(Context context , String key , String value){
        getPreferences(context).getString(key,value);
    }

    public static void clearPref(Context context) {
        getPreferences(context).edit().clear().apply();
    }
}
