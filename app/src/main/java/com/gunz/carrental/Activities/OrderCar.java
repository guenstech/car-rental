package com.gunz.carrental.Activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gunz.carrental.Database.CarDBHelper;
import com.gunz.carrental.Database.CarRepo;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CurrencyFormatter;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class OrderCar extends AppCompatActivity {
    private Bundle bundle;
    private CarRepo repo;
    private CardView cardView;
    private TextView lblTitle, lblPrice, lblStatus;
    private MaterialEditText txStartDate, txEndDate;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_car);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardView = (CardView)findViewById(R.id.cardView);
        cardView.setCardBackgroundColor(Color.WHITE);
        lblTitle = (TextView)findViewById(R.id.lblTitle);
        lblPrice = (TextView)findViewById(R.id.lblPrice);
        lblStatus = (TextView)findViewById(R.id.lblStatus);
        txStartDate = (MaterialEditText)findViewById(R.id.txStartDate);
        txEndDate = (MaterialEditText)findViewById(R.id.txEndDate);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);

        bundle = getIntent().getExtras();
        repo = new CarRepo(this);
        CarDBHelper carDetail = repo.getCarDetail(bundle.getInt("CAR_ID"));
        lblTitle.setText(carDetail.license_plat + " | " + carDetail.model);
        CurrencyFormatter currencyFormatter = new CurrencyFormatter(carDetail.fare);
        lblPrice.setText(currencyFormatter.format());
        lblStatus.setText(Status.AVAILABLE.status());

        txStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.e("","Start date toouched!");
                }
                return false;
            }
        });
    }

    private enum Status {
        AVAILABLE("available"),
        RENTED("rented");

        private String arg;

        Status(String arg) {
            this.arg = arg;
        }

        public String status() {
            return arg;
        }
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}