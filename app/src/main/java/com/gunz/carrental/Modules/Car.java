package com.gunz.carrental.Modules;

/**
 * Created by Gunz on 23/10/2016.
 */
public class Car {
    public int id;
    public String brand;
    public String type;
    public String licensePlat;
    public int year;
    public String color;
    public double farePerDay;
    public String status;
    public String title;
    public String thumb;

    public Car(int id, String brand, String type, String licensePlat, int year,
               String color, double farePerDay, String status, String thumb) {
        this.id = id;
        this.brand = brand;
        this.type = type;
        this.licensePlat = licensePlat;
        this.year = year;
        this.color = color;
        this.farePerDay = farePerDay;
        this.status = status;
        this.title = licensePlat + " | " + type;
        this.thumb = thumb;
    }
}
