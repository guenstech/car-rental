package com.gunz.carrental.Database;

/**
 * Created by Gunz on 23/10/2016.
 */
public class CarDBHelper {
    public static final String TABLE_NAME = "car_table";
    public static final String KEY_ID = "id";
    public static final String KEY_CAR_ID = "car_id";
    public static final String KEY_BRAND = "brand";
    public static final String KEY_MODEL = "model";
    public static final String KEY_LICENSE_PLAT = "license_plat";
    public static final String KEY_FARE = "fare";
    public static final String KEY_CREATED_AT = "created_at";
    public static final String KEY_UPDATED_AT = "updated_at";
    public static final String KEY_IMAGE_URL = "image_url";

    public int id;
    public int car_id;
    public String brand;
    public String model;
    public String license_plat;
    public double fare;
    public String created_at;
    public String updated_at;
    public String image_url;

}
