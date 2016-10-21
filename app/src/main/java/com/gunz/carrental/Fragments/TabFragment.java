package com.gunz.carrental.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.baoyz.widget.PullRefreshLayout;
import com.gunz.carrental.Adapter.RecyclerAdapter;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

public class TabFragment extends Fragment {
    public TabFragment(){}
    private PullRefreshLayout pullRefreshLayout;
    private AsyncHttpClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tab_fragment, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        RecyclerAdapter adapter = new RecyclerAdapter(new String[]{"test one", "test two", "test three", "test four", "test five" , "test six" , "test seven", "test eight" , "test nine", "test one", "test two", "test three", "test four", "test five" , "test six" , "test seven", "test eight" , "test nine"});
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        pullRefreshLayout = (PullRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_SMARTISAN);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                client = new AsyncHttpClient();
                client.get("http://192.168.1.4/vappystore/get_categorys", null, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("","Failure: Ooops (" + statusCode + ") " + throwable.getMessage());
                        pullRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.e("","Success: " + responseString);
                        pullRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        return rootView;
    }
}