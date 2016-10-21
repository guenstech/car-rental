package com.gunz.carrental.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gunz.carrental.R;
import com.gunz.carrental.Utils.DateChecker;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Gunz on 22/10/2016.
 */
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private JSONArray arrayData;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextTitle;
        public TextView mTextStatus;
        public ViewHolder(View v) {
            super(v);
            mTextTitle = (TextView)v.findViewById(R.id.lblTitle);
            mTextStatus = (TextView)v.findViewById(R.id.lblStatus);
        }
    }

    public OrderAdapter(Context context, JSONArray arrayData) {
        this.context = context;
        this.arrayData = arrayData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            holder.mTextTitle.setText(arrayData.getJSONObject(position).getJSONObject("user").getString("name")
                    + " | "
                    + arrayData.getJSONObject(position).getJSONObject("car").getString("model")
            );
            DateChecker dateChecker = new DateChecker(context,
                    arrayData.getJSONObject(position).getString("end_date")
            );
            holder.mTextStatus.setText(dateChecker.getStatus());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayData.length();
    }
}
