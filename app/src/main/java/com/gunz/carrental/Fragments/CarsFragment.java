package com.gunz.carrental.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.gunz.carrental.Activities.MainActivity;
import com.gunz.carrental.Adapter.CarAdapter;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Database.CarDBHelper;
import com.gunz.carrental.Database.CarRepo;
import com.gunz.carrental.Database.DBHandler;
import com.gunz.carrental.Modules.Car;
import com.gunz.carrental.Modules.IntentCode;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.gunz.carrental.Utils.MySwipeLayout;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CarsFragment extends Fragment {
    public CarsFragment() {}
    private View rootView;
    private MySwipeLayout mySwipeLayout;
    private AsyncHttpClient client;
    private RecyclerView rv;
    private CarAdapter adapter;
    private List<Car> cars;
    private FloatingActionButton fab;
    private CarRepo repo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cars, container, false);
        setHasOptionsMenu(true);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastDeleteCar,
                new IntentFilter(IntentCode.BROADCAST_CAR_DELETE));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastUpdateCar,
                new IntentFilter(IntentCode.BROADCAST_CAR_UPDATE));
        rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        repo = new CarRepo(getActivity());
        cars = new ArrayList<Car>();
        adapter = new CarAdapter(getActivity(), cars);
        rv.setAdapter(adapter);

        fab = ((MainActivity)getActivity()).getFab();
        fab.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                addCar(true, -1);
            }
        });

        rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isAdded() && isVisible() && getUserVisibleHint()) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        fab.show(true);
                    } else if (!recyclerView.canScrollVertically(1)) {
                        fab.hide(true);
                    } else if (dy < 0) {
                        if (fab.isHidden()) {
                            fab.show(true);
                        }
                    } else if (dy > 0) {
                        //onScrolledDown();
                    }
                }
            }
        });

        mySwipeLayout = (MySwipeLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mySwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCarList();
            }
        });

        getCarsFromDatabase();

        return rootView;
    }

    private void getCarsFromDatabase() {
        List<CarDBHelper> allCars = repo.getAllCar();
        if (allCars.size() == 0) {
            getCarList();
        } else {
            cars.clear();
            for (int i = 0; i < allCars.size(); i++) {
                cars.add(new Car(
                        allCars.get(i).car_id,
                        allCars.get(i).brand,
                        allCars.get(i).model,
                        allCars.get(i).license_plat,
                        0,
                        "",
                        allCars.get(i).fare,
                        Status.AVAILABLE.status(),
                        allCars.get(i).image_url
                ));
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getCarList() {
        client = new AsyncHttpClient();
        client.get(URLConstant.get_car_list, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mySwipeLayout.setRefreshing(false);
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getActivity().getResources().getString(R.string.dialog_error_title))
                        .setContentText("(" + statusCode + ") " + throwable.getMessage())
                        .show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    repo.deleteAllCars();
                    JSONArray arrayData = new JSONArray(responseString);
                    for (int i = 0; i < arrayData.length(); i++) {
                        String imgUrl = "";
                        if (!arrayData.getJSONObject(i).isNull("image_url")) {
                            imgUrl = arrayData.getJSONObject(i).getString("image_url");
                        }
                        repo.insertCar(
                                arrayData.getJSONObject(i).getInt("id"),
                                arrayData.getJSONObject(i).getString("brand"),
                                arrayData.getJSONObject(i).getString("model"),
                                arrayData.getJSONObject(i).getString("license_plat"),
                                arrayData.getJSONObject(i).getDouble("fare"),
                                arrayData.getJSONObject(i).getString("created_at"),
                                arrayData.getJSONObject(i).getString("updated_at"),
                                imgUrl
                        );
                    }
                    getCarsFromDatabase();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mySwipeLayout.setRefreshing(false);
            }
        });
    }

    private void addCar(final boolean isAddCar, final int position) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_car);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView lblTitle = (TextView)dialog.findViewById(R.id.lblTitle);
        final MaterialEditText txBrand = (MaterialEditText)dialog.findViewById(R.id.txBrand);
        final MaterialEditText txModel = (MaterialEditText)dialog.findViewById(R.id.txModel);
        final MaterialEditText txLicense = (MaterialEditText)dialog.findViewById(R.id.txLicense);
        final MaterialEditText txFare = (MaterialEditText)dialog.findViewById(R.id.txFare);
        Button btnSave = (Button)dialog.findViewById(R.id.btnSave);
        ImageView imgClose = (ImageView)dialog.findViewById(R.id.imgClose);
        dialog.show();
        clearErrorMsg(dialog);

        if (!isAddCar) {
            lblTitle.setText(getActivity().getResources().getString(R.string.update_car_title));
            txBrand.setText(cars.get(position).brand);
            txModel.setText(cars.get(position).type);
            txLicense.setText(cars.get(position).licensePlat);
            txFare.setText(String.valueOf((int)cars.get(position).farePerDay));
        } else {
            lblTitle.setText(getActivity().getResources().getString(R.string.add_new_car_title));
        }

        btnSave.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (TextUtils.isEmpty(txBrand.getText().toString().trim())) {
                    txBrand.setText("");
                    txBrand.setError(getActivity().getResources().getString(R.string.validation_required));
                    txBrand.requestFocus();
                } else if (TextUtils.isEmpty(txModel.getText().toString().trim())) {
                    txModel.setText("");
                    txModel.setError(getActivity().getResources().getString(R.string.validation_required));
                    txModel.requestFocus();
                } else if (TextUtils.isEmpty(txLicense.getText().toString().trim())) {
                    txLicense.setText("");
                    txLicense.setError(getActivity().getResources().getString(R.string.validation_required));
                    txLicense.requestFocus();
                } else if (TextUtils.isEmpty(txFare.getText().toString().trim())) {
                    txFare.setText("");
                    txFare.setError(getActivity().getResources().getString(R.string.validation_required));
                    txFare.requestFocus();
                } else {
                    String confirmText;
                    if (isAddCar) {
                        confirmText = getActivity().getResources().getString(R.string.dialog_add_car_question);
                    } else {
                        confirmText = getActivity().getResources().getString(R.string.dialog_update_car_question);
                    }
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText(getActivity().getResources().getString(R.string.dialog_confirmation))
                            .setContentText(confirmText)
                            .setCancelText(getActivity().getResources().getString(R.string.btn_cancel))
                            .setConfirmText(getActivity().getResources().getString(R.string.btn_save))
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    if (isAddCar) {
                                        saveNewCar(
                                                dialog,
                                                txBrand.getText().toString().trim(),
                                                txModel.getText().toString().trim(),
                                                Integer.parseInt(txFare.getText().toString().trim()),
                                                txLicense.getText().toString().trim()
                                        );
                                    } else {
                                        updateCar(
                                                dialog,
                                                cars.get(position).id,
                                                txBrand.getText().toString().trim(),
                                                txModel.getText().toString().trim(),
                                                Integer.parseInt(txFare.getText().toString().trim()),
                                                txLicense.getText().toString().trim()
                                        );
                                    }
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });

        imgClose.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(animation);
            }
        });
    }

    private void saveNewCar(final Dialog dialog, final String brand, final String model, int fare, String licensePlat) {
        final SweetAlertDialog sweetAlert = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(getResources().getColor(R.color.sweetalert_bar));
        sweetAlert.setTitleText(getActivity().getResources().getString(R.string.dialog_wait));
        sweetAlert.setCancelable(false);
        sweetAlert.show();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("brand", brand);
            jsonParams.put("model", model);
            jsonParams.put("license_plat", licensePlat);
            jsonParams.put("fare", fare);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            StringEntity entity = new StringEntity(jsonParams.toString());
            client = new AsyncHttpClient();
            client.post(getActivity(), URLConstant.get_car_list, entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    sweetAlert.dismiss();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getActivity().getResources().getString(R.string.dialog_error_title))
                            .setContentText("(" + statusCode + ") " + throwable.getMessage())
                            .show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    getCarList();
                    sweetAlert.dismiss();
                    dialog.dismiss();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getActivity().getResources().getString(R.string.dialog_success_title))
                            .setContentText("Car " + brand + " | " + model + " " +
                                    getActivity().getResources().getString(R.string.dialog_car_success_add))
                            .show();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void updateCar(final Dialog dialog, final int id, final String brand, final String model, int fare, String licensePlat) {
        final SweetAlertDialog sweetAlert = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(getResources().getColor(R.color.sweetalert_bar));
        sweetAlert.setTitleText(getActivity().getResources().getString(R.string.dialog_wait));
        sweetAlert.setCancelable(false);
        sweetAlert.show();

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("brand", brand);
            jsonParams.put("model", model);
            jsonParams.put("license_plat", licensePlat);
            jsonParams.put("fare", fare);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            StringEntity entity = new StringEntity(jsonParams.toString());
            client = new AsyncHttpClient();
            client.put(getActivity(), URLConstant.update_delete_car + id + ".json", entity, "application/json", new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    sweetAlert.dismiss();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getActivity().getResources().getString(R.string.dialog_error_title))
                            .setContentText("(" + statusCode + ") " + throwable.getMessage())
                            .show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    getCarList();
                    sweetAlert.dismiss();
                    dialog.dismiss();
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText(getActivity().getResources().getString(R.string.dialog_success_title))
                            .setContentText("Car " + brand + " | " + model + " " +
                                    getActivity().getResources().getString(R.string.dialog_car_success_update))
                            .show();
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastUpdateCar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isAddCar = intent.getExtras().getBoolean("ADD");
            int position = intent.getExtras().getInt("POSITION");
            addCar(isAddCar, position);
        }
    };

    private BroadcastReceiver broadcastDeleteCar = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getCarList();
            if (fab.isHidden()) {
                fab.show(true);
            }
        }
    };

    private void clearErrorMsg(Dialog dialog) {
        for (int i = 0; i < inputField.length; i++) {
            final MaterialEditText txTmp = (MaterialEditText)dialog.findViewById(inputField[i]);
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
            R.id.txBrand,
            R.id.txModel,
            R.id.txFare,
            R.id.txLicense
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addCar(true, -1);
                break;
            default:
                break;
        }
        return false;
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastUpdateCar);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastDeleteCar);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}