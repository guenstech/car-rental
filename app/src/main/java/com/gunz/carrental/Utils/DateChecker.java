package com.gunz.carrental.Utils;

import android.content.Context;
import android.util.Log;

import com.gunz.carrental.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gunz on 22/10/2016.
 */
public class DateChecker {
    private Context context;
    private String endDate;
    private String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DateChecker(Context context, String endDate) {
        this.context = context;
        this.endDate = endDate;
    }

    public String getStatus() {
        Log.e("",""+todayDate);
        String status = null;

        try {
            if (dateFormat.parse(endDate).before(dateFormat.parse(todayDate)) ||
                    dateFormat.parse(endDate).equals(dateFormat.parse(todayDate))) {
                status = context.getResources().getString(R.string.status_active);
            } else {
                status = context.getResources().getString(R.string.status_finish);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return status;
    }
}
