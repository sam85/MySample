package com.sbj.sms_fire.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sbj.sms_fire.SMS_Send;

public class SMSBroadcastReceiver extends BroadcastReceiver {
	//private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {

		Log.e("SMSBroadcastReceiver SMS_FIRE", " : " + intent.getAction());
		Log.e("SMSBroadcastReceiver Event_id ",
				" : " + intent.getIntExtra("Event_id", 0));
		// Bundle bundle = intent.getExtras();
		// int interval = bundle.getInt("Event_id");
		//
		// Log.e("SMSBroadcastReceiver Event_id ", " : " + interval);
		SMS_Send st = new SMS_Send(context, intent.getIntExtra("Event_id", 0));
		st.SendSMS();
	}
}
