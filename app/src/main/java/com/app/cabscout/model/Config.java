package com.app.cabscout.model;

/*
 * Created by rishav on 17/1/17.
 */

public class Config {

    private static final String BASE_URL = "http://35.162.151.221/customer_api.php?action=";
    public static final String cab_companies_url = BASE_URL + "company_list";

    static final String get_cabs_url = BASE_URL+"get_cab&cab_alias=";
    static final String registration_url = BASE_URL + "customer_register";

    public static final String login_url = BASE_URL+"customer_login";

    static final String facebook_login_verify_url = BASE_URL+"customerDetailFacebookId&facebook_id=";

    public static final String fb_login_url = BASE_URL+"customer_register";

    static final String nearby_drivers_url = BASE_URL+"get_drivers";
    static final String request_ride_url = BASE_URL+"request_driver";
    static final String cancel_request_url = BASE_URL+"cancelRide&customer_id=";
    static final String schedule_history_url = BASE_URL + "schedule_history&customer_id=";
    static final String trips_history_url = BASE_URL+"trip_history&customer_id=";
    static final String user_details_url = BASE_URL+"user_detail&customer_id=";
    public static final String update_profile_pic_url = BASE_URL+"update_profile_pic";
    static final String update_home_details_url = BASE_URL+"updateHomeDetail";

    static final String update_work_details_url = BASE_URL+"updateWorkDetail";

    public static final String user_pic_url = "http://35.162.151.221/profile_pics/";
    static final String change_cab_url = BASE_URL+"updateCabCompany&customer_id=";
    static final String allow_drivers_url = BASE_URL+"allow_other_drivers&customer_id=";
    static final String change_password_url = BASE_URL+"reset_password&customer_id=";
    static final String driver_details_url = BASE_URL+"get_driver_list&customer_id=";
}
