package com.gunz.carrental.Activities;

import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.gunz.carrental.Adapter.PagerAdapter;
import com.gunz.carrental.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    public static ImageLoader imageLoader;
    public static DisplayImageOptions imageOption;
    private DisplayImageOptions imageOptionRounded;
    public static ImageLoaderConfiguration imageConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageLoader = ImageLoader.getInstance();
        imageConfig = new ImageLoaderConfiguration.Builder(this).build();
        imageOption = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        imageLoader.init(imageConfig);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(pagerAdapter);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.hide(true);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
                switch (position) {
                    case 0:
                        fab.hide(true);
                        break;
                    case 1:
                        fab.show(true);
                        break;
                    case 2:
                        fab.show(true);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public FloatingActionButton getFab() {
        return fab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (viewPager.getCurrentItem() == 0){
            menu.findItem(R.id.action_add).setVisible(false);
        } else if(viewPager.getCurrentItem() == 1){
            menu.findItem(R.id.action_add).setVisible(true);
        } else if(viewPager.getCurrentItem() == 2){
            menu.findItem(R.id.action_add).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
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
        imageLoader.clearMemoryCache();
        imageLoader.clearDiskCache();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
