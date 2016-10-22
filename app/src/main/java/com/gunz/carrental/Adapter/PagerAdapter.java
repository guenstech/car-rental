package com.gunz.carrental.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.gunz.carrental.Fragments.CarsFragment;
import com.gunz.carrental.Fragments.OrdersFragment;
import com.gunz.carrental.R;

/**
 * Created by Gunz on 21/10/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    String tabTitles[];

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.tabTitles = context.getResources().getStringArray(R.array.tab_name);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OrdersFragment();
            case 1:
                return new CarsFragment();
            case 2:
                return new CarsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

//    public View getTabView(int position) {
//        View tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
//        TextView tv = (TextView) tab.findViewById(R.id.custom_text);
//        tv.setText(tabTitles[position]);
//        return tab;
//    }
}
