package com.parithi.theorangestation.models;

/**
 * Created by earul on 20/12/16.
 */

public class Taxi {
    String id;
    String name;
    String phone_number;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phone_number;
    }

    @Override
    public String toString() {
        return("id : " + id + " / " + name + " : " + phone_number);
    }
}
