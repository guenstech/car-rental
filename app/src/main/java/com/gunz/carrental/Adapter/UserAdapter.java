package com.gunz.carrental.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gunz.carrental.Modules.User;
import com.gunz.carrental.R;
import com.gunz.carrental.Utils.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;

import java.util.List;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextName.setText(users.get(position).name);
        holder.mTextAddress.setText(users.get(position).address);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

}
