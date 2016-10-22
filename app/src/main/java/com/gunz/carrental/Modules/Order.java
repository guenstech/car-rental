package com.gunz.carrental.Modules;

import java.util.Date;

/**
 * Created by Gunz on 22/10/2016.
 */
public class Order {
    public int orderId;
    public int userId;
    public int carId;
    public String title;
    public String user;
    public String car;
    public Date startDate;
    public Date endDate;

    public Order(int orderId, int userId, int carId, String user, String car, Date startDate, Date endDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.carId = carId;
        this.user = user;
        this.car = car;
        this.title = user + " | " + car;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
