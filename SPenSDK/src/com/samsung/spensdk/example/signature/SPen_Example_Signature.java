package com.samsung.spensdk.example.signature;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_Signature extends Activity {

	public ListView mSignatureList;
	public ArrayList<ListItem> mSignatureListItem;
	public ListAdapter mSignatureAdapter;
	private Toast m_Toast = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_signature_signature);

		mSignatureListItem = new ArrayList<ListItem>();
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_start_check_title), getResources().getString(R.string.signature_start_check_sub_title)));
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_start_registration_title), getResources().getString(R.string.signature_start_registration_sub_title)));
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_start_verification_title), getResources().getString(R.string.signature_start_verification_sub_title)));
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_start_delete_title), getResources().getString(R.string.signature_start_delete_sub_title)));

		mSignatureAdapter = new ListAdapter(this);

		mSignatureList = (ListView) findViewById(R.id.signature_list);
		mSignatureList.setAdapter(mSignatureAdapter);

		mSignatureList.setItemsCanFocus(false);
		mSignatureList.setTextFilterEnabled(true);
		mSignatureList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					if (SCanvasView.isSignatureExist(SPen_Example_Signature.this)) {
						// Registered signature is existed
						sToastS(getResources().getString(R.string.signature_start_check_success));
					} else {
						// Registered signature is not existed
						sToastS(getResources().getString(R.string.signature_start_check_failure));
					}
				} else if (position == 1) {
					Intent intent = new Intent(SPen_Example_Signature.this, SPen_Example_SignatureRegistration.class);
					startActivity(intent); // create RegistrationActivity
				} else if (position == 2) {
					if (SCanvasView.isSignatureExist(SPen_Example_Signature.this)) {
						Intent intent = new Intent(SPen_Example_Signature.this, SPen_Example_SignatureVerification.class);
						startActivity(intent); // create VerificationActivity
					} else {
						// Registered signature is not existed
						sToastS(getResources().getString(R.string.signature_start_verification_failure));
					}
				} else if (position == 3) {
					if (SCanvasView.unregisterSignature(SPen_Example_Signature.this)) {
						// Registered signature is existed
						sToastS(getResources().getString(R.string.signature_start_delete_failure));
					} else {
						// Registered signature is not existed
						sToastS(getResources().getString(R.string.signature_start_delete_success));
					}
				}
				mSignatureAdapter.notifyDataSetChanged();
			}
		});

	}
	
	
	@Override
	protected void onResume() {
		mSignatureAdapter.notifyDataSetChanged();
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}

	class ListItem {
		ListItem(String iTitle, String isubTitle) {
			Title = iTitle;
			subTitle = isubTitle;
		}

		String Title;
		String subTitle;
	}

	class ListAdapter extends BaseAdapter {
		LayoutInflater Inflater;

		public ListAdapter(Context context) {
			Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSignatureListItem.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = Inflater.inflate(R.layout.example_signature_list_item, parent, false);
			}

			if ((position == 2 || position == 3) && !SCanvasView.isSignatureExist(SPen_Example_Signature.this)) {
				TextView title = (TextView) convertView.findViewById(R.id.signature_list_title);
				title.setText(mSignatureListItem.get(position).Title);
				title.setTextColor(0xFF005D87);
				TextView subtitle = (TextView) convertView.findViewById(R.id.signature_list_subtitle);
				subtitle.setText(mSignatureListItem.get(position).subTitle);
				subtitle.setTextColor(0xFF777777);
			} else {
				TextView title = (TextView) convertView.findViewById(R.id.signature_list_title);
				title.setText(mSignatureListItem.get(position).Title);
				title.setTextColor(0xFF00B8FF);
				TextView subtitle = (TextView) convertView.findViewById(R.id.signature_list_subtitle);
				subtitle.setText(mSignatureListItem.get(position).subTitle);
				subtitle.setTextColor(0xFFFFFFFF);
			}
			return convertView;
		}
	}

	public void sToastS(String i_String) {
		if (m_Toast == null) {
			m_Toast = Toast.makeText(this, i_String, Toast.LENGTH_SHORT);
		} else {
			m_Toast.setText(i_String);
		}
		m_Toast.show();
	}

}
