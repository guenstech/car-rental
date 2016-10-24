package com.gunz.carrental.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
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
import com.gunz.carrental.Adapter.UserAdapter;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Database.UserDBHelper;
import com.gunz.carrental.Database.UserRepo;
import com.gunz.carrental.Modules.IntentCode;
import com.gunz.carrental.Modules.User;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DividerItemDecoration;
import com.gunz.carrental.Utils.MySwipeLayout;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.loopj.android.http.AsyncHttpClient;
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
import cz.msebera.android.httpclient.entity.StringEntity;

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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastDeleteUser,
                new IntentFilter(IntentCode.BROADCAST_USER_DELETE));
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

    private void showUserForm() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_user);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView lblTitle = (TextView)dialog.findViewById(R.id.lblTitle);
        lblTitle.setText(getActivity().getResources().getString(R.string.add_new_user_title));
        final MaterialEditText txName = (MaterialEditText)dialog.findViewById(R.id.txName);
        final MaterialEditText txAddress = (MaterialEditText)dialog.findViewById(R.id.txAddress);
        final MaterialEditText txMobile = (MaterialEditText)dialog.findViewById(R.id.txMobile);
        txName.setEnabled(true);
        txAddress.setEnabled(true);
        txMobile.setEnabled(true);
        Button btnSave = (Button)dialog.findViewById(R.id.btnSave);
        btnSave.setText(getActivity().getResources().getString(R.string.btn_save));
        ImageView imgClose = (ImageView)dialog.findViewById(R.id.imgClose);
        dialog.show();
        clearErrorMsg(dialog);

        btnSave.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                if (TextUtils.isEmpty(txName.getText().toString().trim())) {
                    txName.setText("");
                    txName.setError(getActivity().getResources().getString(R.string.validation_required));
                    txName.requestFocus();
                } else if (TextUtils.isEmpty(txAddress.getText().toString().trim())) {
                    txAddress.setText("");
                    txAddress.setError(getActivity().getResources().getString(R.string.validation_required));
                    txAddress.requestFocus();
                } else if (TextUtils.isEmpty(txMobile.getText().toString().trim())) {
                    txMobile.setText("");
                    txMobile.setError(getActivity().getResources().getString(R.string.validation_required));
                    txMobile.requestFocus();
                } else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText(getActivity().getResources().getString(R.string.dialog_confirmation))
                            .setContentText(getActivity().getResources().getString(R.string.add_user_question))
                            .setCancelText(getActivity().getResources().getString(R.string.btn_cancel))
                            .setConfirmText(getActivity().getResources().getString(R.string.btn_save))
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    JSONObject jsonParams = new JSONObject();
                                    try {
                                        jsonParams.put("name", txName.getText().toString().trim());
                                        jsonParams.put("address", txAddress.getText().toString().trim());
                                        jsonParams.put("mobile", txMobile.getText().toString().trim());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    submitNewUser(dialog, jsonParams);
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

    private void submitNewUser(final Dialog dialog, JSONObject jsonParams) {
        final SweetAlertDialog sweetAlert = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(getResources().getColor(R.color.sweetalert_bar));
        sweetAlert.setTitleText(getActivity().getResources().getString(R.string.dialog_wait));
        sweetAlert.setCancelable(false);
        sweetAlert.show();

        try {
            StringEntity entity = new StringEntity(jsonParams.toString());
            client = new AsyncHttpClient();
            client.post(getActivity(), URLConstant.get_user_list, entity, "application/json", new TextHttpResponseHandler() {
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
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        getUserList();
                        sweetAlert.dismiss();
                        dialog.dismiss();
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(getActivity().getResources().getString(R.string.dialog_success_title))
                                .setContentText("User " + jsonObject.getString("name") + " " +
                                        getActivity().getResources().getString(R.string.dialog_car_success_add))
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

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
            R.id.txName,
            R.id.txAddress,
            R.id.txMobile
    };

    private BroadcastReceiver broadcastDeleteUser = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getUserList();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                showUserForm();
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastDeleteUser);
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