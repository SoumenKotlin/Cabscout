package com.app.cabscout.model.Beans;

/*
 * Created by rishav on 31/1/17.
 */

public class TripsHistoryBeans {

    private String request_id, pickup_location, drop_location, datetime, price, driver_name, profile_pic;

    public TripsHistoryBeans(String request_id, String pickup_location, String drop_location,
                             String datetime, String price, String driver_name, String profile_pic) {
        this.request_id = request_id;
        this.pickup_location = pickup_location;
        this.drop_location = drop_location;
        this.datetime = datetime;
        this.price = price;
        this.driver_name = driver_name;
        this.profile_pic = profile_pic;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getPickup_location() {
        return pickup_location;
    }

    public String getDrop_location() {
        return drop_location;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getPrice() {
        return price;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public String getProfile_pic() {
        return profile_pic;
    }
}
