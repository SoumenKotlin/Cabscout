package com.app.cabscout.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by rishav on 17/1/17.
 */

public class Operations {
    private static final String TAG = Operations.class.getSimpleName();

    public static String getCabCompaniesTask(Context context, String cab_alias) {
        String params = Config.get_cabs_url+cab_alias;
        Log.e(TAG, "cab_company params-- "+params);

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

    public static String nearbyDriversTask(Context context, double lat, double lng, String car_type) {
        String params = Config.nearby_drivers_url+"&latitude="+lat+"&longitude="+lng+"&car_type="+car_type;

        Log.e(TAG, "nearby_drivers parameter-- "+params);

        return params;
    }

    public static String facebookLoginTask(Context context, String fb_id) {
        String params = Config.facebook_login_verify_url+fb_id;

        Log.e(TAG, "login_verify params-- "+params);

        return params;
    }

    public static String fbLoginParams(Context context, String company_id, String email,
                                       String name, String token, String mobile, String imageUrl, String fb_id) {
        /*String params = Config.fb_login_url+company_id+"&email="+email+"&password=WJBJvfHTRNT"+"&name="+name
                +"&device_token="+token+"&device_type=A"+"&mobile="+mobile+"&profileImage="+imageUrl+"&facebook_id="+fb_id;*/

       try {
           JSONObject postDataParams = new JSONObject();
           postDataParams.put("company_id", company_id);
           postDataParams.put("email", email);
           postDataParams.put("password", "welcomeUser");
           postDataParams.put("name", name);
           postDataParams.put("device_token", token);
           postDataParams.put("device_type", "A");
           postDataParams.put("mobile", mobile);
           postDataParams.put("profileImage", imageUrl);
           postDataParams.put("facebook_id", fb_id);

           String params = null;
           try {
               params = Utils.getPostDataString(postDataParams);
           } catch (Exception e) {
               e.printStackTrace();
           }
      //     Log.e(TAG, "fb_login params-- "+params);

           return params;

       } catch (JSONException e) {
           e.printStackTrace();
       }
      return null;
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
        try {
            JSONObject postDataParams = new JSONObject();
            postDataParams.put("customer_id", customer_id);
            postDataParams.put("profile_pic", base64);

            String params = null;
            try {
                params = Utils.getPostDataString(postDataParams);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(TAG, "update_image params-- "+params);

            return params;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
       /* String params = Config.update_profile_pic_url+customer_id+"&profile_pic="+base64;

        Log.e(TAG, "update_image params-- "+params);

        return params;*/
    }

    public static String getUserDetails(Context context, String customer_id) {
        String params = Config.user_details_url+customer_id;

        Log.e(TAG, "user_details url-- "+params);

        return params;
    }

    public static String updateCabCompany(Context context,String customer_id, String cab_id) {
        String params = Config.change_cab_url+customer_id+"&company_id="+cab_id;

        Log.e(TAG, "update_company params-- "+params);

        return params;
    }

    public static String updateAllowedDrivers(Context context, String customer_id, String allowed_status) {
        String params = Config.allow_drivers_url+customer_id+"&allow="+allowed_status;

        Log.e(TAG, "allowed_drivers params-- "+params);

        return params;
    }

    public static String changePasswordTask(Context context, String customer_id, String oldPassword, String newPassword) {
        String params = Config.change_password_url+customer_id+"&oldpassword="+oldPassword+"&newpassword="+newPassword;

        Log.e(TAG, "change_password params-- "+params);

        return params;
    }

    public static String requestAcceptedTask(Context context, String customer_id) {
        String params = Config.driver_details_url+customer_id;

        Log.e(TAG, "driver_details_params-- "+params);

        return params;
    }


}
