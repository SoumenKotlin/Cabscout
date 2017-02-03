package com.app.cabscout.model.Beans;

/*
 * Created by rishav on 25/1/17.
 */

public class ScheduleHistoryBeans {
    private String pickup_lat, pickup_lng, drop_lat, drop_lng, dateTime, payment_type, price, car_name;

    public ScheduleHistoryBeans(String pickup_lat, String pickup_lng, String drop_lat, String drop_lng, String dateTime,
                                String payment_type, String price, String car_name) {
        this.pickup_lat = pickup_lat;
        this.pickup_lng = pickup_lng;
        this.drop_lat = drop_lat;
        this.drop_lng = drop_lng;
        this.dateTime = dateTime;
        this.payment_type = payment_type;
        this.price = price;
        this.car_name = car_name;
    }

    public String getPickup_lat() {
        return pickup_lat;
    }

    public String getPickup_lng() {
        return pickup_lng;
    }

    public String getDrop_lat() {
        return drop_lat;
    }

    public String getDrop_lng() {
        return drop_lng;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public String getPrice() {
        return price;
    }

    public String getCar_name() {
        return car_name;
    }
}
