package com.app.cabscout.model.Beans;

/*
 * Created by rishav on 19/1/17.
 */

public class SearchAddressBeans {

    private  String address, area;

    public SearchAddressBeans(String address, String area) {
        this.address = address;
        this.area = area;
    }

    public String getAddress() {
        return address;
    }

    public String getArea() {
        return area;
    }
}
