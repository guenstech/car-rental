package com.gunz.carrental.Fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.gunz.carrental.Activities.MainActivity;
import com.gunz.carrental.Adapter.OrderAdapter;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Modules.Order;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.gunz.carrental.Utils.MySwipeLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Gunz on 22/10/2016.
 */
public class OrdersFragment extends Fragment {
    public OrdersFragment(){}
    private View rootView;
    private MySwipeLayout mySwipeLayout;
    private AsyncHttpClient client;
    private RecyclerView rv;
    private OrderAdapter adapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<Order> orders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        orders = new ArrayList<Order>();
        adapter = new OrderAdapter(getActivity(), orders);
        rv.setAdapter(adapter);

        mySwipeLayout = (MySwipeLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mySwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrder();
            }
        });

        mySwipeLayout.setRefreshing(true);
        getOrder();

        return rootView;
    }

    private void getOrder() {
        client = new AsyncHttpClient();
        client.get(URLConstant.get_order, null, new TextHttpResponseHandler() {
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
                    orders.clear();
                    for (int i = 0; i < arrayData.length(); i++) {
                        try {
                            orders.add(new Order(
                                    arrayData.getJSONObject(i).getInt("id"),
                                    arrayData.getJSONObject(i).getInt("user_id"),
                                    arrayData.getJSONObject(i).getInt("car_id"),
                                    arrayData.getJSONObject(i).getJSONObject("user").getString("name"),
                                    arrayData.getJSONObject(i).getJSONObject("car").getString("model"),
                                    dateFormat.parse(arrayData.getJSONObject(i).getString("start_date")),
                                    dateFormat.parse(arrayData.getJSONObject(i).getString("end_date"))
                            ));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mySwipeLayout.setRefreshing(false);
            }
        });
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
