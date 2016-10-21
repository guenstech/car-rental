package com.gunz.carrental.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.gunz.carrental.Moduls.Order;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DateUtils;

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
        public ViewHolder(View v) {
            super(v);
            mTextTitle = (TextView)v.findViewById(R.id.lblTitle);
            mTextStatus = (TextView)v.findViewById(R.id.lblStatus);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        String status;
        holder.mTextTitle.setText(orders.get(position).title);
        if (DateUtils.isBeforeDay(new Date(), orders.get(position).endDate)
                || DateUtils.isToday(orders.get(position).endDate)) {
            status = context.getResources().getString(R.string.status_active);
        } else {
            status = context.getResources().getString(R.string.status_finish);
        }
        holder.mTextStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
