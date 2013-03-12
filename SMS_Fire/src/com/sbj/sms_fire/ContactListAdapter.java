package com.sbj.sms_fire;

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

public class ContactListAdapter extends CursorAdapter implements Filterable {
	private ContentResolver mContent;

	public ContactListAdapter(Context paramContext, Cursor paramCursor) {
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

	public View newView(Context paramContext, Cursor paramCursor,
			ViewGroup paramViewGroup) {
		LinearLayout localLinearLayout = (LinearLayout) LayoutInflater.from(
				paramContext).inflate(R.layout.autofillcontact, paramViewGroup, false);
		int i = paramCursor.getColumnIndex("display_name");
		int j = paramCursor.getColumnIndex("data1");
		TextView localTextView1 = (TextView) localLinearLayout
				.findViewById(R.id.txtAutoCName);
		TextView localTextView2 = (TextView) localLinearLayout
				.findViewById(R.id.txtAutoCNumber);
		localTextView1.setText(paramCursor.getString(i));
		localTextView2.setText("<" + paramCursor.getString(j) + ">");
		return localLinearLayout;
	}

	public Cursor runQueryOnBackgroundThread(CharSequence paramCharSequence) {
		Cursor localCursor;
		if (getFilterQueryProvider() != null) {
			localCursor = getFilterQueryProvider().runQuery(paramCharSequence);
			return localCursor;
		}
		String[] arrayOfString1 = (String[]) null;
		StringBuilder localStringBuilder = null;
		if (paramCharSequence != null) {
			localStringBuilder = new StringBuilder();
			localStringBuilder.append("UPPER(");
			localStringBuilder.append("display_name");
			localStringBuilder.append(") GLOB ?");
			arrayOfString1 = new String[1];
			arrayOfString1[0] = (paramCharSequence.toString().toUpperCase() + "*");
		}
		String[] arrayOfString2 = { "_id", "display_name", "data1", "data2" };
		ContentResolver localContentResolver = this.mContent;
		Uri localUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		
		if(localStringBuilder == null)
			return null;
		
		localCursor = localContentResolver.query(localUri, arrayOfString2,
				localStringBuilder.toString(), arrayOfString1, null);
		return localCursor;
		
	}
}
