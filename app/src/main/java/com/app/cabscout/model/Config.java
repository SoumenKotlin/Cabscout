package com.app.cabscout.model;

/**
 * Created by rishav on 17/1/17.
 */

public class Config {

    private static final String BASE_URL = "http://35.162.151.221/customer_api.php?action=";
    public static final String cab_companies_url = BASE_URL + "company_list";
    public static final String registration_url = BASE_URL + "customer_register";
    public static final String login_url = BASE_URL+"customer_login";
    public static final String request_ride_url = BASE_URL+"request_driver";
}
