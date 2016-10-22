package com.gunz.carrental.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.gunz.carrental.Activities.OrderCar;
import com.gunz.carrental.Activities.OrderCarDetail;
import com.gunz.carrental.Modules.Order;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DateUtils;
import com.gunz.carrental.Utils.OnOneClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gunz on 22/10/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orders;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextTitle;
        public TextView mTextStatus;
        public TextView mTextDetail;
        public ViewHolder(View v) {
            super(v);
            mTextTitle = (TextView)v.findViewById(R.id.lblTitle);
            mTextStatus = (TextView)v.findViewById(R.id.lblStatus);
            mTextDetail = (TextView)v.findViewById(R.id.lblDetail);
        }
    }

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orders = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String status;
        holder.mTextTitle.setText(orders.get(position).title);
        if (DateUtils.isBeforeDay(new Date(), orders.get(position).endDate)
                || DateUtils.isToday(orders.get(position).endDate)) {
            status = context.getResources().getString(R.string.status_active);
        } else {
            status = context.getResources().getString(R.string.status_finish);
        }
        holder.mTextStatus.setText(status);
        holder.mTextDetail.setOnClickListener(new OnOneClickListener() {
            @Override
            public void onOneClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.bounce);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("order_id", orders.get(position).orderId);
                            jsonObject.put("user_id", orders.get(position).userId);
                            jsonObject.put("car_id", orders.get(position).carId);
                            jsonObject.put("user", orders.get(position).user);
                            jsonObject.put("car", orders.get(position).car);
                            jsonObject.put("start_date", orders.get(position).startDate);
                            jsonObject.put("end_date", orders.get(position).endDate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(context, OrderCarDetail.class);
                        intent.putExtra("DATA", jsonObject.toString());
                        context.startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.startAnimation(animation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

//    private void showDetail() {
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_dialog);
//        TextView lblTitle = (TextView)dialog.findViewById(R.id.lblTitle);
//        dialog.show();
//    }
}
