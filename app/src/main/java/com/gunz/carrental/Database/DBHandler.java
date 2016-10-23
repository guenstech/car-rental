package com.gunz.carrental.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gunz on 23/10/2016.
 */
public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "car_rental.db";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_CAR = "CREATE TABLE " + CarDBHelper.TABLE_NAME + "("
                + CarDBHelper.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + CarDBHelper.KEY_CAR_ID + " INTEGER, "
                + CarDBHelper.KEY_BRAND + " TEXT, "
                + CarDBHelper.KEY_MODEL + " TEXT, "
                + CarDBHelper.KEY_LICENSE_PLAT + " TEXT UNIQUE, "
                + CarDBHelper.KEY_FARE + " DOUBLE, "
                + CarDBHelper.KEY_CREATED_AT + " TEXT, "
                + CarDBHelper.KEY_UPDATED_AT + " TEXT, "
                + CarDBHelper.KEY_IMAGE_URL + " TEXT )";
        db.execSQL(CREATE_TABLE_CAR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CarDBHelper.TABLE_NAME);
        onCreate(db);
    }
}
