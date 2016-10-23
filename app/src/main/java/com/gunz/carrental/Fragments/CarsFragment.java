package com.gunz.carrental.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.github.clans.fab.FloatingActionButton;
import com.gunz.carrental.Activities.MainActivity;
import com.gunz.carrental.Adapter.CarAdapter;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Database.CarDBHelper;
import com.gunz.carrental.Database.CarRepo;
import com.gunz.carrental.Database.DBHandler;
import com.gunz.carrental.Modules.Car;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.gunz.carrental.Utils.MySwipeLayout;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

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
                addCar();
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

    private void addCar() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_car);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        MaterialEditText txBrand = (MaterialEditText)dialog.findViewById(R.id.txBrand);
        MaterialEditText txModel = (MaterialEditText)dialog.findViewById(R.id.txModel);
        MaterialEditText txLicense = (MaterialEditText)dialog.findViewById(R.id.txLicense);
        MaterialEditText txFare = (MaterialEditText)dialog.findViewById(R.id.txFare);
        Button btnSave = (Button)dialog.findViewById(R.id.btnSave);
        ImageView imgClose = (ImageView)dialog.findViewById(R.id.imgClose);
        dialog.show();

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
                addCar();
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