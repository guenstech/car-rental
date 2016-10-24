package com.gunz.carrental.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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
import com.gunz.carrental.Activities.OrderCar;
import com.gunz.carrental.Api.URLConstant;
import com.gunz.carrental.Modules.Car;
import com.gunz.carrental.Modules.IntentCode;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CircularImageView;
import com.gunz.carrental.Utils.CurrencyFormatter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Gunz on 23/10/2016.
 */
public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private Context context;
    private List<Car> cars;
    private AsyncHttpClient client;

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
                final PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.menu_popup_car, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_car_order:
                                Intent intent = new Intent(context, OrderCar.class);
                                intent.putExtra("CAR_ID", cars.get(position).id);
                                context.startActivity(intent);
                                break;
                            case R.id.action_car_update:
                                Intent callbackDelete = new Intent(IntentCode.BROADCAST_CAR_UPDATE);
                                callbackDelete.putExtra("ADD", false);
                                callbackDelete.putExtra("POSITION", position);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(callbackDelete);
                                break;
                            case R.id.action_car_delete:
                                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(context.getResources().getString(R.string.dialog_confirmation))
                                        .setContentText(context.getResources().getString(R.string.dialog_delete_question))
                                        .setCancelText(context.getResources().getString(R.string.btn_cancel))
                                        .setConfirmText(context.getResources().getString(R.string.action_delete))
                                        .showCancelButton(true)
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                deleteCar(cars.get(position).id, cars.get(position).brand, cars.get(position).type);
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
        return cars.size();
    }

    private void deleteCar(int id, final String brand, final String model) {
        final SweetAlertDialog sweetAlert = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlert.getProgressHelper().setBarColor(context.getResources().getColor(R.color.sweetalert_bar));
        sweetAlert.setTitleText(context.getResources().getString(R.string.dialog_wait));
        sweetAlert.setCancelable(false);
        sweetAlert.show();

        client = new AsyncHttpClient();
        client.delete(context, URLConstant.update_delete_car + id + ".json", new TextHttpResponseHandler() {
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
                Intent callbackDelete = new Intent(IntentCode.BROADCAST_CAR_DELETE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(callbackDelete);
                new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(context.getResources().getString(R.string.dialog_success_title))
                        .setContentText("Car " + brand + " | " + model + " " +
                                context.getResources().getString(R.string.dialog_car_success_delete))
                        .show();
            }
        });
    }

}
