package com.gunz.carrental.Fragments;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.gunz.carrental.Activities.MainActivity;
import com.gunz.carrental.Adapter.RecyclerAdapter;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;

public class TabFragment extends Fragment {
    public TabFragment(){}
    private PullRefreshLayout pullRefreshLayout;

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
                pullRefreshLayout.setRefreshing(false); //cancel refresh when done or failed request
            }
        });

        return rootView;
    }
}