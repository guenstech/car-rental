package com.gunz.carrental.Activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Modules.Order;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CurrencyFormatter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

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
    private CardView cardView;
    private TextView lblTitle, lblPrice, lblStatus;
    private MaterialEditText txName, txStartDate, txEndDate;
    private Button btnClose;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_car_detail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardView = (CardView)findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(Color.WHITE);
        lblTitle = (TextView)findViewById(R.id.lblTitle);
        lblPrice = (TextView)findViewById(R.id.lblPrice);
        lblStatus = (TextView)findViewById(R.id.lblStatus);
        txName = (MaterialEditText)findViewById(R.id.txName);
        txStartDate = (MaterialEditText)findViewById(R.id.txStartDate);
        txEndDate = (MaterialEditText)findViewById(R.id.txEndDate);
        btnClose = (Button)findViewById(R.id.btnClose);

        bundle = getIntent().getExtras();
        try {
            JSONObject jsonObject = new JSONObject(bundle.getString("DATA"));
            try {
                status = jsonObject.getString("status");
                txName.setText(jsonObject.getString("user"));
                Date startDate = dateFormat.parse(jsonObject.getString("start_date"));
                Date endDate = dateFormat.parse(jsonObject.getString("end_date"));
                txStartDate.setText(dateFormat2.format(startDate));
                txEndDate.setText(dateFormat2.format(endDate));
                getCarDetail(jsonObject.getString("car_id"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                    lblTitle.setText(
                            jsonObject.getString("license_plat") + " | " + jsonObject.getString("model")
                    );
                    CurrencyFormatter currencyFormatter = new CurrencyFormatter(jsonObject.getDouble("fare"));
                    lblPrice.setText(currencyFormatter.format());
                    lblStatus.setText(status);
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
