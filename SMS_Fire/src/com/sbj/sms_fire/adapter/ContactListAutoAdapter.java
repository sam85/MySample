package com.sbj.sms_fire.adapter;

import com.sbj.sms_fire.R;
import com.sbj.sms_fire.R.id;
import com.sbj.sms_fire.R.layout;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ContactListAutoAdapter extends CursorAdapter implements Filterable {
	private ContentResolver mContent;

	public ContactListAutoAdapter(Context paramContext, Cursor paramCursor) {
		super(paramContext, paramCursor);
		this.mContent = paramContext.getContentResolver();
		Log.e("paramCursor "," : "+paramCursor.getCount());
	}

	public void bindView(View paramView, Context paramContext,
			Cursor paramCursor) {
		int i = paramCursor.getColumnIndex("display_name");
		int j = paramCursor.getColumnIndex("data1");
		TextView localTextView1 = (TextView) paramView.findViewById(R.id.txtAutoCName);
		TextView localTextView2 = (TextView) paramView.findViewById(R.id.txtAutoCNumber);
		localTextView1.setText(paramCursor.getString(i));
		localTextView2.setText("<" + paramCursor.getString(j) + ">");
	}

	public String convertToString(Cursor paramCursor) {
		int i = paramCursor.getColumnIndex("display_name");
		int j = paramCursor.getColumnIndex("data1");
		return paramCursor.getString(i) + "<" + paramCursor.getString(j) + ">";
	}

	
}
