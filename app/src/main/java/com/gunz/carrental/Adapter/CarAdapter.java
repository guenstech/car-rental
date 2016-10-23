package com.gunz.carrental.Adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunz.carrental.Activities.MainActivity;
import com.gunz.carrental.Modules.Car;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CircularImageView;
import com.gunz.carrental.Utils.CurrencyFormatter;

import java.util.List;

/**
 * Created by Gunz on 23/10/2016.
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private Context context;
    List<Car> cars;

    public CarAdapter(Context context, List<Car> carList) {
        this.context = context;
        this.cars = carList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView mImgThumb;
        public ImageView mImgMore;
        public TextView mTextTitle;
        public TextView mTextStatus;
        public TextView mTextFare;
        public ViewHolder(View v) {
            super(v);
            mImgThumb = (CircularImageView)v.findViewById(R.id.imgThumb);
            mTextTitle = (TextView)v.findViewById(R.id.lblTitle);
            mTextStatus = (TextView)v.findViewById(R.id.lblStatus);
            mTextFare = (TextView)v.findViewById(R.id.lblFare);
            mImgMore = (ImageView)v.findViewById(R.id.imgMore);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextTitle.setText(cars.get(position).title);
//        MainActivity.imageLoader.displayImage(cars.get(position).thumb, holder.mImgThumb, MainActivity.imageOption);
        CurrencyFormatter currencyFormatter = new CurrencyFormatter(cars.get(position).farePerDay);
        holder.mTextFare.setText(currencyFormatter.format());
        holder.mTextStatus.setText(cars.get(position).status);
        holder.mImgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("","Menu pos (" + position + ") clicked!");
                Log.e("","Car ID: " + cars.get(position).id);
                final PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.menu_popup_car, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_car_order:
                                Log.e("","Order");
                                break;
                            case R.id.action_car_update:
                                Log.e("","Update");
                                break;
                            case R.id.action_car_delete:
                                Log.e("","Delete");
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
        return cars.size();
    }

}
