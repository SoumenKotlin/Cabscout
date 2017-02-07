package com.app.cabscout.model;

/*
 * Created by rishav on 17/1/17.
 */

public class Config {

    private static final String BASE_URL = "http://35.162.151.221/customer_api.php?action=";
    public static final String cab_companies_url = BASE_URL + "company_list";
    public static final String registration_url = BASE_URL + "customer_register";
    public static final String login_url = BASE_URL+"customer_login";
    public static final String facebook_login_verify_url = BASE_URL+"customerDetailFacebookId&facebook_id=";
    public static final String fb_login_url = BASE_URL+"customer_register";
    public static final String request_ride_url = BASE_URL+"request_driver";
    public static final String schedule_history_url = BASE_URL + "schedule_history&customer_id=";
    public static final String trips_history_url = BASE_URL+"trip_history&customer_id=";
    public static final String user_details_url = BASE_URL+"user_detail&customer_id=";
    public static final String update_profile_pic_url = BASE_URL+"update_profile_pic&customer_id=";
    public static final String update_home_details_url = BASE_URL+"updateHomeDetail";
    public static final String update_work_details_url = BASE_URL+"updateWorkDetail";
    public static final String user_pic_url = "http://35.162.151.221/profile_pics/";
}
