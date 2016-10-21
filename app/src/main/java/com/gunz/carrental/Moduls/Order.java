package com.gunz.carrental.Moduls;

import java.util.Date;

/**
 * Created by Gunz on 22/10/2016.
 */
public class Order {
    public String title;
    public String user;
    public String car;
    public Date startDate;
    public Date endDate;

    public Order(String user, String car, Date startDate, Date endDate) {
        this.user = user;
        this.car = car;
        this.title = user + " | " + car;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
