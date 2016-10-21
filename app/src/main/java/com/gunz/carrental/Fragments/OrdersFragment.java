package com.gunz.carrental.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.gunz.carrental.Adapter.OrderAdapter;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Gunz on 22/10/2016.
 */
public class OrdersFragment extends Fragment {
    public OrdersFragment(){}
    private View rootView;
    private PullRefreshLayout pullRefreshLayout;
    private AsyncHttpClient client;
    private RecyclerView rv;
    private OrderAdapter adapter;
    private JSONArray arrayData = new JSONArray();

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
        adapter = new OrderAdapter(getActivity(), arrayData);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        pullRefreshLayout = (PullRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrder();
            }
        });

        if (arrayData.length() == 0) {
            pullRefreshLayout.setRefreshing(true);
            getOrder();
        }

        return rootView;
    }

    private void getOrder() {
        client = new AsyncHttpClient();
        client.get(URLConstant.get_order, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("", "Failure: Ooops (" + statusCode + ") " + throwable.getMessage());
                pullRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("", "Success: " + responseString);
                try {
                    arrayData = new JSONArray(responseString);
                    adapter = new OrderAdapter(getActivity(), arrayData);
                    rv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pullRefreshLayout.setRefreshing(false);
            }
        });
    }
}
