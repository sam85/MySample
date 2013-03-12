package com.sbj.sms_fire.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {

	Log.e("BootBroadcastReceiver SMS_FIRE"," : "+intent.getAction());
	}
}
