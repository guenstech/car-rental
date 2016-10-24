package com.gunz.carrental.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Database.CarDBHelper;
import com.gunz.carrental.Database.CarRepo;
import com.gunz.carrental.Database.UserRepo;
import com.gunz.carrental.Modules.Order;
import com.gunz.carrental.Modules.User;
import com.gunz.carrental.Modules.UserObject;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CurrencyFormatter;
import com.gunz.carrental.Utils.DateUtils;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class OrderCar extends AppCompatActivity {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Bundle bundle;
    private CarRepo repo;
    private CardView cardView;
    private AsyncHttpClient client;
    private DatePickerDialog datePickerDialog;
    private String[] item = new String[]{};
    private int[] mUserId = new int[]{};
    private ArrayAdapter<String> userAutocompleteAdapter;
    private TextView lblTitle, lblPrice, lblStatus;
    private MaterialAutoCompleteTextView txName;
    private MaterialEditText txStartDate, txEndDate;
    private Button btnConfirm;
    private int userSelectedID;

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
        txName = (MaterialAutoCompleteTextView)findViewById(R.id.txName);
        txStartDate = (MaterialEditText)findViewById(R.id.txStartDate);
        txEndDate = (MaterialEditText)findViewById(R.id.txEndDate);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);

        bundle = getIntent().getExtras();
        repo = new CarRepo(this);
        final CarDBHelper carDetail = repo.getCarDetail(bundle.getInt("CAR_ID"));
        lblTitle.setText(carDetail.license_plat + " | " + carDetail.model);
        CurrencyFormatter currencyFormatter = new CurrencyFormatter(carDetail.fare);
        lblPrice.setText(currencyFormatter.format());
        lblStatus.setText(Status.AVAILABLE.status());
        clearErrorMsg();

        userAutocompleteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, item);
        txName.setAdapter(userAutocompleteAdapter);

        txName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                item = getUsersFromDb(s.toString());
                userAutocompleteAdapter.notifyDataSetChanged();
                userAutocompleteAdapter = new ArrayAdapter<String>(OrderCar.this, android.R.layout.simple_dropdown_item_1line, item);
                txName.setAdapter(userAutocompleteAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserRepo userRepo = new UserRepo(OrderCar.this);
                userSelectedID = userRepo.getUserIDbyName(item[position].toString());
            }
        });

        txStartDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showStartDatePicker();
                }
                return false;
            }
        });

        txEndDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showEndDatePicker();
                }
                return false;
            }
        });

        btnConfirm.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (TextUtils.isEmpty(txName.getText().toString().trim())) {
                    txName.setText("");
                    txName.setError(getResources().getString(R.string.validation_required));
                    txName.requestFocus();
                } else if (TextUtils.isEmpty(txStartDate.getText().toString().trim())) {
                    txStartDate.setText("");
                    txStartDate.setError(getResources().getString(R.string.validation_required));
                    txStartDate.requestFocus();
                } else if (TextUtils.isEmpty(txEndDate.getText().toString().trim())) {
                    txEndDate.setText("");
                    txEndDate.setError(getResources().getString(R.string.validation_required));
                    txEndDate.requestFocus();
                } else {
                    new SweetAlertDialog(OrderCar.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText(getResources().getString(R.string.dialog_confirmation))
                            .setContentText(getResources().getString(R.string.dialog_submit_order_question))
                            .setCancelText(getResources().getString(R.string.btn_cancel))
                            .setConfirmText(getResources().getString(R.string.btn_confirm))
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    submitOrder(
                                            txStartDate.getText().toString().trim(),
                                            txEndDate.getText().toString().trim(),
                                            carDetail.car_id,
                                            userSelectedID
                                    );
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
    }

    private void showStartDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar todayDate = Calendar.getInstance();
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (DateUtils.isSameDay(todayDate, newDate) || DateUtils.isAfterDay(newDate, todayDate)) {
                    if (!TextUtils.isEmpty(txEndDate.getText().toString().trim())) {
                        try {
                            Date startDate = newDate.getTime();
                            Date endDate = dateFormat.parse(txEndDate.getText().toString().toString());
                            if (DateUtils.isAfterDay(startDate, endDate)) {
                                new SweetAlertDialog(OrderCar.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.dialog_error_title))
                                        .setContentText(getResources().getString(R.string.date_not_correct))
                                        .show();
                            } else {
                                txStartDate.setText(dateFormat.format(newDate.getTime()));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        txStartDate.setText(dateFormat.format(newDate.getTime()));
                        txEndDate.setEnabled(true);
                    }
                } else {
                    new SweetAlertDialog(OrderCar.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.dialog_error_title))
                            .setContentText(getResources().getString(R.string.date_not_correct))
                            .show();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showEndDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                try {
                    Date startDate = dateFormat.parse(txStartDate.getText().toString().toString());
                    Date endDate = newDate.getTime();
                    if (DateUtils.isSameDay(startDate, endDate) || DateUtils.isAfterDay(endDate, startDate)){
                        txEndDate.setText(dateFormat.format(newDate.getTime()));
                    } else {
                        new SweetAlertDialog(OrderCar.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.dialog_error_title))
                                .setContentText(getResources().getString(R.string.date_not_correct))
                                .show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void submitOrder(final String startDate, final String endDate, int carID, int userID) {
        final SweetAlertDialog sweetAlert = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(getResources().getColor(R.color.sweetalert_bar));
        sweetAlert.setTitleText(getResources().getString(R.string.dialog_wait));
        sweetAlert.setCancelable(false);
        sweetAlert.show();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("start_date", startDate);
            jsonParams.put("end_date", endDate);
            jsonParams.put("car_id", carID);
            jsonParams.put("user_id", userID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            StringEntity entity = new StringEntity(jsonParams.toString());
            client = new AsyncHttpClient();
            client.post(this, URLConstant.get_order, entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    sweetAlert.dismiss();
                    new SweetAlertDialog(OrderCar.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.dialog_error_title))
                            .setContentText("(" + statusCode + ") " + throwable.getMessage())
                            .show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    sweetAlert.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        if (jsonObject.has("messages")) {
                            String msg = "";
                            for (int i = 0; i < jsonObject.getJSONArray("messages").length(); i++) {
                                msg = msg + jsonObject.getJSONArray("messages").get(i);
                                if (i < jsonObject.getJSONArray("messages").length()) {
                                    msg = msg + "\n";
                                }
                            }
                            new SweetAlertDialog(OrderCar.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.dialog_error_title))
                                    .setContentText(msg)
                                    .show();
                        } else {
                            final SweetAlertDialog sweetSuccess = new SweetAlertDialog(OrderCar.this, SweetAlertDialog.SUCCESS_TYPE);
                            sweetSuccess.setTitleText(getResources().getString(R.string.dialog_success_title));
                            sweetSuccess.setContentText(getResources().getString(R.string.dialog_order_success));
                            sweetSuccess.setCanceledOnTouchOutside(false);
                            sweetSuccess.setCancelable(false);
                            sweetSuccess.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetSuccess.dismiss();
                                    finish();
                                }
                            });
                            sweetSuccess.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void clearErrorMsg() {
        for (int i = 0; i < inputField.length; i++) {
            final EditText txTmp = (EditText)findViewById(inputField[i]);
            txTmp.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count > 0) {
                        txTmp.setError(null);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    private int[] inputField = new int[] {
            R.id.txName,
            R.id.txStartDate,
            R.id.txEndDate
    };

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

    private String[] getUsersFromDb(String searchTerm){
        UserRepo userRepo = new UserRepo(this);
        List<UserObject> userList = userRepo.getUserByName(searchTerm);
        int rowCount = userList.size();
        String[] item = new String[rowCount];
        int x = 0;
        for (UserObject record : userList) {
            item[x] = record.objectName;
            x++;
        }
        return item;
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