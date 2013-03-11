package com.sbj.sms_fire;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BroadcastService  extends Service {
	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.sbj.sms_fire.displayevent";
	private final Handler handler = new Handler();
	Intent intent;
	int counter = 0;
	
	@Override
	public void onCreate() {
		super.onCreate();
    	intent = new Intent(BROADCAST_ACTION);	
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
       
    	handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 60000*2); // 1 second
        handler.postDelayed(sendUpdatesToUI4, 60000*4); // 1 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		// 2 Minute
    		int min = 2;
    		DisplayLoggingInfo(min);    		
    	    handler.postDelayed(this, 60000 * min); // 60 seconds
    	
    	}
    };   
    
    private Runnable sendUpdatesToUI4 = new Runnable() {
    	public void run() {
    			// 4 Minute
    		int min = 4;
    		DisplayLoggingInfo(min);    		
    	    handler.postDelayed(this, 60000 * min); // 60 seconds
    	}
    };   
    
    
    private void DisplayLoggingInfo(int min) {
    	Log.d(TAG, "entered DisplayLoggingInfo" +min );

    	intent.putExtra("time", String.valueOf(min));
    	intent.putExtra("counter", String.valueOf(++counter));
    	sendBroadcast(intent);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}		
}
