package com.sbj.sms_fire;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.SmsManager;
import android.util.Log;

import com.sbj.sms_fire.constant.ColumnConstants;
import com.sbj.sms_fire.constant.Constant;
import com.sbj.sms_fire.dao.DBAdapter;

public class SMS_Send {

	Context ctx;
	int Event_ID;
	DBAdapter db;
	
	public SMS_Send(Context ctx, int id) {
		super();
		this.ctx = ctx;
		Event_ID = id;
		db = new DBAdapter(ctx);
	}

	public void SendSMS() {
		
		String ContactInfo;
		String Message;
		
		Intent intSent = new Intent(Constant.BROADCAST_SENT);
		intSent.putExtra("Event_id", this.Event_ID);
		

		Intent intDel = new Intent(Constant.BROADCAST_DELIVERED);
		intDel.putExtra("Event_id", this.Event_ID);
			

		db.open();
		Cursor cEvData= db.getEventData(Event_ID);
	
		ContactInfo = cEvData.getString(cEvData
				.getColumnIndex(ColumnConstants.COLUMN_varContacts.getColumnName()));
	
		Message = cEvData.getString(cEvData
				.getColumnIndex(ColumnConstants.COLUMN_varMessage.getColumnName()));
		
		cEvData.close();
		db.close();
		String ContactNumbers[] = ContactInfo.split(",");
		for(int k=0;k<ContactNumbers.length;k++)
		{
			Log.e("Send Mesage "," : " + ContactNumbers[k]);
			
			intSent.putExtra("Con_Number",  ContactNumbers[k]);
			intDel.putExtra("Con_Number",  ContactNumbers[k]);
			
			PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, intSent, 0);
			PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0,
					intDel, 0);	
			
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(ContactNumbers[k], null, Message, sentPI, deliveredPI);
		}
	}	

}
