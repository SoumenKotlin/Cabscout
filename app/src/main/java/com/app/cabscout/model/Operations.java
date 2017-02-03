package com.app.cabscout.model;

import android.content.Context;
import android.util.Log;

/*
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

    public static String facebookLoginTask(Context context, String fb_id) {
        String params = Config.facebook_login_verify_url+fb_id;

        Log.e(TAG, "login_verify params-- "+params);

        return params;
    }

    public static String fbLoginParams(Context context, String company_id, String email, String password,
                                       String name, String token, String mobile, String imageUrl, String fb_id) {
        String params = Config.fb_login_url+company_id+"&email="+email+"&password="+password+"&name="+name
                +"&device_token="+token+"&device_type=A"+"&mobile="+mobile+"&profileImage="+imageUrl+"&facebook_id="+fb_id;

        Log.e(TAG, "fb_login params-- "+params);

        return params;
    }

    public static String requestRideTask(Context context, String customer_id, String pickup_location, String drop_location,
                                          String vehicle_type, String src_latLng, String dest_latLng, String request_type,
                                         String date, String time, String payment_type, String price) {

        String params = Config.request_ride_url+"&customer_id="+customer_id+"&pickup_location="+pickup_location+
                "&drop_location="+drop_location+"&vehicle_type="+vehicle_type+"&pickup_cordinates="+src_latLng+
                "&drop_cordinates="+dest_latLng+"&request_type="+request_type+"&date="+date+"&time="+time+
                "&payment_type="+payment_type+"&price="+price;

        Log.e(TAG, "request_ride params-- "+ params);

        return params;


    }

    public static String getScheduledRides(Context context, String customer_id) {
        String params = Config.schedule_history_url+customer_id;

        Log.e(TAG, "scheduled_rides params-- "+params);

        return params;
    }

    public static String getTripsHistory(Context context, String customer_id) {
        String params = Config.trips_history_url+customer_id;

        Log.e(TAG, "trips_history params-- "+params);

        return params;
    }

    public static String updateHomeDetails(Context context, String latitude, String longitude,
                                           String customer_id, String location) {

        String params = Config.update_home_details_url+"&latitude="+latitude+"&longitude="+longitude+
                "&customer_id="+customer_id+"&LocationName="+location;

        Log.e(TAG, "update_home params-- "+ params);

        return params;

    }

    public static String updateWorkDetails(Context context, String latitude, String longitude,
                                           String customer_id, String location) {

        String params = Config.update_work_details_url+"&latitude="+latitude+"&longitude="+longitude+
                "&customer_id="+customer_id+"&LocationName="+location;

        Log.e(TAG, "update_work params-- "+ params);

        return params;

    }

    public static String updateProfileImage(Context context, String customer_id, String base64) {
        String params = Config.update_profile_pic_url+customer_id+"&profile_pic="+base64;

        Log.e(TAG, "update_image params-- "+params);

        return params;
    }

    public static String getUserDetails(Context context, String customer_id) {
        String params = Config.user_details_url+customer_id;

        Log.e(TAG, "user_details url-- "+params);

        return params;
    }


}
