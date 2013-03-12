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

	public int getCount() {
		return this.contactModels.size();
	}

	public Object getItem(int paramInt) {
		return null;
	}

	public long getItemId(int paramInt) {
		return paramInt;
	}

	public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
		if (paramView == null)
			paramView = this.inflater.inflate(R.layout.contacts_row, null);
		final ContactModel localContactModel = (ContactModel) this.contactModels
				.get(paramInt);
		
		TextView localTextView1 = (TextView) paramView.findViewById(R.id.row_contacts_txtName);
		
		TextView localTextView2 = (TextView) paramView.findViewById(R.id.row_contacts_txtNumber);
		final ImageButton localImageButton = (ImageButton) paramView
				.findViewById(R.id.row_contacts_imgBtnTickMark);
		localTextView1.setText(localContactModel.getContactName());
		localTextView2.setText(localContactModel.getContactNumber());
		
		if (localContactModel.isSelectFlag())
			localImageButton.setVisibility(View.VISIBLE);
		else
			localImageButton.setVisibility(View.INVISIBLE);
	
			localImageButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					if (localImageButton.isShown()) 
					{
						localContactModel.setSelectFlag(false);
						localImageButton.setVisibility(View.INVISIBLE);
					}
					else {
						localContactModel.setSelectFlag(true);
						localImageButton.setVisibility(View.VISIBLE);
					}
				}
			});
			
			paramView.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					if (localImageButton.isShown()) {
						localContactModel.setSelectFlag(false);
						localImageButton.setVisibility(View.INVISIBLE);
					}
					else{
						localContactModel.setSelectFlag(true);
						localImageButton.setVisibility(View.VISIBLE);
					}
				}
			});
		
			return paramView;
		
	}
}
