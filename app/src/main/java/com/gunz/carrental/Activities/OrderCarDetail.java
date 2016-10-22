package com.gunz.carrental.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Modules.Order;
import com.gunz.carrental.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class OrderCarDetail extends AppCompatActivity {
    private Bundle bundle;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
    private TextView lblTitle, lblPrice, lblStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_car_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblTitle = (TextView)findViewById(R.id.lblTitle);
        lblPrice = (TextView)findViewById(R.id.lblPrice);
        lblStatus = (TextView)findViewById(R.id.lblStatus);

        bundle = getIntent().getExtras();
        try {
            JSONObject jsonObject = new JSONObject(bundle.getString("DATA"));
            try {
                Date date = dateFormat.parse(jsonObject.getString("start_date"));
                Log.e("","sDate: " + dateFormat2.format(date));
                getCarDetail(jsonObject.getString("car_id"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCarDetail(String car_id) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URLConstant.get_car_detail + car_id + ".json", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                new SweetAlertDialog(OrderCarDetail.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.dialog_error_title))
                        .setContentText("(" + statusCode + ") " + throwable.getMessage())
                        .show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    Log.e("",""+jsonObject.toString());
                    lblTitle.setText(
                            jsonObject.getString("license_plat") + " | " + jsonObject.getString("model")
                    );
                    lblPrice.setText(jsonObject.getString("fare"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
