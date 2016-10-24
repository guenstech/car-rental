package com.gunz.carrental.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import com.gunz.carrental.Activities.OrderCar;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Modules.IntentCode;
import com.gunz.carrental.Modules.User;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CircularImageView;
import com.gunz.carrental.Utils.OnOneClickListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Gunz on 24/10/2016.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> users;
    private AsyncHttpClient client;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.users = userList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView mImgThumb;
        public ImageView mImgMore;
        public TextView mTextName;
        public TextView mTextAddress;
        public ViewHolder(View v) {
            super(v);
            mImgThumb = (CircularImageView)v.findViewById(R.id.imgThumb);
            mTextName = (TextView)v.findViewById(R.id.lblName);
            mTextAddress = (TextView)v.findViewById(R.id.lblAddress);
            mImgMore = (ImageView)v.findViewById(R.id.imgMore);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextName.setText(users.get(position).name);
        holder.mTextAddress.setText(users.get(position).address);
        holder.mImgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.menu_popup_user, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_user_detail:
                                userDetail(position);
                                break;
                            case R.id.action_user_delete:
                                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(context.getResources().getString(R.string.dialog_confirmation))
                                        .setContentText(context.getResources().getString(R.string.dialog_delete_question))
                                        .setCancelText(context.getResources().getString(R.string.btn_cancel))
                                        .setConfirmText(context.getResources().getString(R.string.action_delete))
                                        .showCancelButton(true)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                deleteUser(users.get(position).id, users.get(position).name);
                                                sweetAlertDialog.dismiss();
                                            }
                                        })
                                        .show();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    private void deleteUser(int id, final String name) {
        final SweetAlertDialog sweetAlert = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(context.getResources().getColor(R.color.sweetalert_bar));
        sweetAlert.setTitleText(context.getResources().getString(R.string.dialog_wait));
        sweetAlert.setCancelable(false);
        sweetAlert.show();

        client = new AsyncHttpClient();
        client.delete(context, URLConstant.update_delete_user + id + ".json", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                sweetAlert.dismiss();
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(context.getResources().getString(R.string.dialog_error_title))
                        .setContentText("(" + statusCode + ") " + throwable.getMessage())
                        .show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                sweetAlert.dismiss();
                Intent callbackDelete = new Intent(IntentCode.BROADCAST_USER_DELETE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(callbackDelete);
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(context.getResources().getString(R.string.dialog_success_title))
                        .setContentText("User " + name + " " +
                                context.getResources().getString(R.string.dialog_car_success_delete))
                        .show();
            }
        });
    }

    private void userDetail(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_user);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        TextView lblTitle = (TextView)dialog.findViewById(R.id.lblTitle);
        lblTitle.setText(context.getResources().getString(R.string.detail_user_title));
        final MaterialEditText txName = (MaterialEditText)dialog.findViewById(R.id.txName);
        final MaterialEditText txAddress = (MaterialEditText)dialog.findViewById(R.id.txAddress);
        final MaterialEditText txMobile = (MaterialEditText)dialog.findViewById(R.id.txMobile);
        Button btnClose = (Button)dialog.findViewById(R.id.btnSave);
        btnClose.setText(context.getResources().getString(R.string.btn_close));
        ImageView imgClose = (ImageView)dialog.findViewById(R.id.imgClose);
        dialog.show();

        txName.setText(users.get(position).name);
        txAddress.setText(users.get(position).address);
        txMobile.setText(users.get(position).mobilePhone);

        btnClose.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                dialog.dismiss();
            }
        });

        imgClose.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
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

}
