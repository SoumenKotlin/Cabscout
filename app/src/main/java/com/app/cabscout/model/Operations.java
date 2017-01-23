package com.app.cabscout.model;

import android.content.Context;
import android.util.Log;

/**
 * Created by rishav on 17/1/17.
 */

public class Operations {
    private static final String TAG = Operations.class.getSimpleName();

    public static String getCabCompaniesTask(Context context) {
        String params = Config.cab_companies_url;
        Log.e(TAG, "cab_companies list--"+params);

        return params;
    }

    public static String registrationTask(Context context, String email, String password, String company_id, String name,
                                          String deviceToken, String mobile) {
        String params = Config.registration_url +"&email="+email+"&password="+password+"&company_id="+company_id+
                "&name="+name +"&device_token="+deviceToken+"&device_type=A"+"&mobile="+mobile;

        Log.e(TAG, "registration params---"+params);

        return params;
    }

    public static String loginTask(Context context, String email, String password, String deviceToken) {
        String params = Config.login_url+"&email="+email+"&password="+password+"&device_token="+deviceToken+"&device_type=A";

        Log.e(TAG, "login parameters--"+params);

        return params;
    }

}
