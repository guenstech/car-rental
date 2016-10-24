package com.gunz.carrental.Fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.gunz.carrental.Activities.MainActivity;
import com.gunz.carrental.Adapter.UserAdapter;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Database.UserDBHelper;
import com.gunz.carrental.Database.UserRepo;
import com.gunz.carrental.Modules.User;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.gunz.carrental.Utils.MySwipeLayout;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

public class UsersFragment extends Fragment {
    public UsersFragment() {}
    private View rootView;
    private MySwipeLayout mySwipeLayout;
    private AsyncHttpClient client;
    private RecyclerView rv;
    private UserAdapter adapter;
    private List<User> users;
    private FloatingActionButton fab;
    private UserRepo repo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_users, container, false);
        setHasOptionsMenu(true);

        rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        rv.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        repo = new UserRepo(getActivity());
        users = new ArrayList<User>();
        adapter = new UserAdapter(getActivity(), users);
        rv.setAdapter(adapter);

        fab = ((MainActivity)getActivity()).getFab();
        fab.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
//                addCar(true, -1);
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
                getUserList();
            }
        });

        getCarsFromDatabase();

        return rootView;
    }

    private void getCarsFromDatabase() {
        List<UserDBHelper> allUsers = repo.getAllUser();
        if (allUsers.size() == 0) {
            getUserList();
        } else {
            users.clear();
            for (int i = 0; i < allUsers.size(); i++) {
                users.add(new User(
                        allUsers.get(i).user_id,
                        allUsers.get(i).name,
                        allUsers.get(i).address,
                        allUsers.get(i).mobile
                ));
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void getUserList() {
        client = new AsyncHttpClient();
        client.get(URLConstant.get_user_list, null, new TextHttpResponseHandler() {
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
                    repo.deleteAllUser();
                    JSONArray arrayData = new JSONArray(responseString);
                    for (int i = 0; i < arrayData.length(); i++) {
                        repo.insertUser(
                                arrayData.getJSONObject(i).getInt("id"),
                                arrayData.getJSONObject(i).getString("name"),
                                arrayData.getJSONObject(i).getString("address"),
                                arrayData.getJSONObject(i).getString("mobile"),
                                arrayData.getJSONObject(i).getString("created_at"),
                                arrayData.getJSONObject(i).getString("updated_at"),
                                arrayData.getJSONObject(i).getString("url")
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
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