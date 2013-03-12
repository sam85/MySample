package com.sbj.sms_fire.receiver;

import java.util.ArrayList;
import java.util.Calendar;

import com.sbj.sms_fire.R;
import com.sbj.sms_fire.R.id;
import com.sbj.sms_fire.R.layout;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class BroadcastTest extends Activity {
	private static final String TAG = "BroadcastTest";
	private Intent intent;
	ArrayList<AlarmManager> alarmintent = new ArrayList<AlarmManager>();
	ArrayList<PendingIntent> PendingIntentD = new ArrayList<PendingIntent>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        intent = new Intent(this, BroadcastService.class);
        Calendar cSetAlarm = Calendar.getInstance();
        cSetAlarm.add(Calendar.MINUTE, +1);
		long timeInMillis = cSetAlarm.getTimeInMillis();
        setAlarm(10,timeInMillis);
        setAlarm(30,timeInMillis);
        setAlarm(60,timeInMillis);
    }

   
    
    private void setAlarm(int eventid, long timeinmillis) {
		Intent intent = new Intent(BroadcastService.BROADCAST_ACTION);
		intent.putExtra("time", String.valueOf(eventid));
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, eventid,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeinmillis, 1000 * eventid, pendingIntent);
	}
    
    
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	Bundle bundle = intent.getExtras();
        	String interval= bundle.getString("time");
        	Log.e("Receiver Called","Receiver : " + interval);
        	
        	//updateUI(intent);       
        }
    };    
    
	@Override
	public void onResume() {
		super.onResume();		
		//startService(intent);
		registerReceiver(broadcastReceiver, new IntentFilter(BroadcastService.BROADCAST_ACTION));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//unregisterReceiver(broadcastReceiver);
		//stopService(intent); 		
	}	
	    
    private void updateUI(Intent intent) {
    	String counter = intent.getStringExtra("counter"); 
    	String time = intent.getStringExtra("time");
    	Log.d(TAG, counter);
    	Log.d(TAG, time);
    	
    	TextView txtDateTime = (TextView) findViewById(R.id.txtDateTime);  	
    	TextView txtCounter = (TextView) findViewById(R.id.txtCounter);
    	txtDateTime.setText(time);
    	txtCounter.setText(counter);
    }
}