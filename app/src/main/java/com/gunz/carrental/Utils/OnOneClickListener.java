package com.gunz.carrental.Utils;


import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.View;

public abstract class OnOneClickListener implements View.OnClickListener {
    private static final long MIN_CLICK_INTERVAL = 700; //in millis
    private long lastClickTime = 0;

    @Override
    public final void onClick(View v) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > MIN_CLICK_INTERVAL) {
        	if (waitingList.waiting == false) {
        		waitingList.waiting = true;
                lastClickTime = currentTime;
                onOneClick(v);
                new CountDownTimer(MIN_CLICK_INTERVAL, MIN_CLICK_INTERVAL) {
    				@Override
    				public void onTick(long millisUntilFinished) {
    					//Log.e("", ""+millisUntilFinished / 1000);
    				}
    				@Override
    				public void onFinish() {
    					waitingList.waiting = false;
    				}
    			}.start();        		
        	}
        }
    }

    public abstract void onOneClick(View v);
}
