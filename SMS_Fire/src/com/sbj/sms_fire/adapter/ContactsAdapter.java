package com.sbj.sms_fire.adapter;

import java.util.ArrayList;

import com.sbj.sms_fire.R;
import com.sbj.sms_fire.R.id;
import com.sbj.sms_fire.R.layout;
import com.sbj.sms_fire.model.ContactModel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactsAdapter extends BaseAdapter {

	private ArrayList<ContactModel> contactModels;
	private LayoutInflater inflater;
	private Context mContext;

	public ContactsAdapter(Context paramContext,
			ArrayList<ContactModel> paramArrayList) {
		this.mContext = paramContext;
		this.contactModels = paramArrayList;
		Log.e("","contactModels.size() "+contactModels.size());
		this.inflater = ((LayoutInflater) this.mContext
				.getSystemService("layout_inflater"));
	}

	
}
