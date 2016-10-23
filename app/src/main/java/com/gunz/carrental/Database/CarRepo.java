package com.gunz.carrental.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gunz on 23/10/2016.
 */
public class CarRepo {
    private DBHandler dbHandler;
    private Context context;

    public CarRepo(Context context) {
        dbHandler = new DBHandler(context);
        this.context = context;
    }

    public boolean insertCar(int car_id, String brand, String model, String licensePlat, double fare,
                             String createdAt, String updated_at, String imgUrl) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CarDBHelper.KEY_CAR_ID, car_id);
        contentValues.put(CarDBHelper.KEY_BRAND, brand);
        contentValues.put(CarDBHelper.KEY_MODEL, model);
        contentValues.put(CarDBHelper.KEY_LICENSE_PLAT, licensePlat);
        contentValues.put(CarDBHelper.KEY_FARE, fare);
        contentValues.put(CarDBHelper.KEY_CREATED_AT, createdAt);
        contentValues.put(CarDBHelper.KEY_UPDATED_AT, updated_at);
        contentValues.put(CarDBHelper.KEY_IMAGE_URL, imgUrl);
        db.insertWithOnConflict(CarDBHelper.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return true;
    }

    public boolean deleteAllCars() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "DELETE FROM " + CarDBHelper.TABLE_NAME;
        db.execSQL(sqlQuery);
        db.close();
        return true;
    }

    public List<CarDBHelper> getAllCar(){
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "SELECT * FROM " + CarDBHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        List<CarDBHelper> carList = new ArrayList<CarDBHelper>();
        if (cursor.moveToFirst()) {
            do {
                CarDBHelper carDBHelper = new CarDBHelper();
                carDBHelper.id = cursor.getInt(cursor.getColumnIndex(CarDBHelper.KEY_ID));
                carDBHelper.car_id = cursor.getInt(cursor.getColumnIndex(CarDBHelper.KEY_CAR_ID));
                carDBHelper.brand = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_BRAND));
                carDBHelper.model = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_MODEL));
                carDBHelper.license_plat = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_LICENSE_PLAT));
                carDBHelper.fare = cursor.getDouble(cursor.getColumnIndex(CarDBHelper.KEY_FARE));
                carDBHelper.created_at = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_CREATED_AT));
                carDBHelper.updated_at = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_UPDATED_AT));
                carDBHelper.image_url = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_IMAGE_URL));
                carList.add(carDBHelper);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carList;
    }

    public CarDBHelper getCarDetail(int id) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "SELECT * FROM " + CarDBHelper.TABLE_NAME + " WHERE " + CarDBHelper.KEY_CAR_ID + " = ?";
        CarDBHelper carDetail = new CarDBHelper();
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            do {
                carDetail.id = cursor.getInt(cursor.getColumnIndex(CarDBHelper.KEY_ID));
                carDetail.car_id = cursor.getInt(cursor.getColumnIndex(CarDBHelper.KEY_CAR_ID));
                carDetail.brand = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_BRAND));
                carDetail.model = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_MODEL));
                carDetail.license_plat = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_LICENSE_PLAT));
                carDetail.fare = cursor.getDouble(cursor.getColumnIndex(CarDBHelper.KEY_FARE));
                carDetail.created_at = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_CREATED_AT));
                carDetail.updated_at = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_UPDATED_AT));
                carDetail.image_url = cursor.getString(cursor.getColumnIndex(CarDBHelper.KEY_IMAGE_URL));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return carDetail;
    }
}
