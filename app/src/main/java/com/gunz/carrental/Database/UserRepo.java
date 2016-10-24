package com.gunz.carrental.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gunz.carrental.Modules.UserObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gunz on 24/10/2016.
 */
public class UserRepo {
    private DBHandler dbHandler;
    private Context context;

    public UserRepo(Context context) {
        dbHandler = new DBHandler(context);
        this.context = context;
    }

    public boolean insertUser(int user_id, String name, String address, String mobile,
                             String createdAt, String updated_at, String url) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UserDBHelper.KEY_USER_ID, user_id);
        contentValues.put(UserDBHelper.KEY_NAME, name);
        contentValues.put(UserDBHelper.KEY_ADDRESS, address);
        contentValues.put(UserDBHelper.KEY_MOBILE, mobile);
        contentValues.put(UserDBHelper.KEY_CREATED_AT, createdAt);
        contentValues.put(UserDBHelper.KEY_UPDATED_AT, updated_at);
        contentValues.put(UserDBHelper.KEY_URL, url);
        db.insertWithOnConflict(UserDBHelper.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return true;
    }

    public boolean deleteAllUser() {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "DELETE FROM " + UserDBHelper.TABLE_NAME;
        db.execSQL(sqlQuery);
        db.close();
        return true;
    }

    public List<UserDBHelper> getAllUser(){
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "SELECT * FROM " + UserDBHelper.TABLE_NAME;
        Cursor cursor = db.rawQuery(sqlQuery, null);
        List<UserDBHelper> userList = new ArrayList<UserDBHelper>();
        if (cursor.moveToFirst()) {
            do {
                UserDBHelper userDBHelper = new UserDBHelper();
                userDBHelper.id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.KEY_ID));
                userDBHelper.user_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.KEY_USER_ID));
                userDBHelper.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_NAME));
                userDBHelper.address = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_ADDRESS));
                userDBHelper.mobile = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_MOBILE));
                userDBHelper.created_at = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_CREATED_AT));
                userDBHelper.updated_at = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_UPDATED_AT));
                userDBHelper.url = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_URL));
                userList.add(userDBHelper);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }

    public int getUserIDbyName(String name) {
        int resultID = 0;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "SELECT * FROM " + UserDBHelper.TABLE_NAME + " WHERE " + UserDBHelper.KEY_NAME + " = ?";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{name});
        if (cursor.moveToFirst()) {
            do {
                resultID = cursor.getInt(cursor.getColumnIndex(UserDBHelper.KEY_USER_ID));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resultID;
    }

    public UserDBHelper getUserDetail(int id) {
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        String sqlQuery =  "SELECT * FROM " + UserDBHelper.TABLE_NAME + " WHERE " + UserDBHelper.KEY_USER_ID + " = ?";
        UserDBHelper userDetail = new UserDBHelper();
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            do {
                userDetail.id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.KEY_ID));
                userDetail.user_id = cursor.getInt(cursor.getColumnIndex(UserDBHelper.KEY_USER_ID));
                userDetail.name = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_NAME));
                userDetail.address = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_ADDRESS));
                userDetail.mobile = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_MOBILE));
                userDetail.created_at = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_CREATED_AT));
                userDetail.updated_at = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_UPDATED_AT));
                userDetail.url = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_URL));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userDetail;
    }

    public List<UserObject> getUserByName(String searchTerm) {
        List<UserObject> recordsList = new ArrayList<UserObject>();
        // select query
        String sql = "";
        sql += "SELECT * FROM " + UserDBHelper.TABLE_NAME;
        sql += " WHERE " + UserDBHelper.KEY_NAME + " LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY " + UserDBHelper.KEY_NAME + " ASC";
        sql += " LIMIT 0,5";
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                String objectName = cursor.getString(cursor.getColumnIndex(UserDBHelper.KEY_NAME));
                UserObject myObject = new UserObject(objectName);
                recordsList.add(myObject);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return recordsList;
    }

}
