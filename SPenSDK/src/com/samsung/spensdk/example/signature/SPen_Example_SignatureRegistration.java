package com.samsung.spensdk.example.signature;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.example.R;

public class SPen_Example_SignatureRegistration extends Activity {

	private final String TAG = "SPenSDK Sample";
	public ArrayList<ListItem> mSignatureListItem;
	public SCanvasView mSCanvas;
	public ListAdapter mSignatureAdapter;
	public int mSigntureRegistrationNum = 0;
	public int mSigntureRegistrationNumMax = 3;
	public ListView mSignatureList;
	private Toast m_Toast = null;
	private int mResult = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_signature_registration);

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);


		// ====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		// ====================================================================================
		SCanvasInitializeListener scanvasInitializeListener = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {

				// --------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				// --------------------------------------------
				// Start Signature Mode
				mSCanvas.openSignatureEngine();

				mSCanvas.setCanvasZoomEnable(false); // disable Zoom
			}
		};

		mSCanvas.setSCanvasInitializeListener(scanvasInitializeListener);

		sToastS(getResources().getString(R.string.signature_registration_draw));

		mSignatureListItem = new ArrayList<ListItem>();
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_registration_registration_title),getResources().getString(R.string.signature_registration_registration_sub_title)));
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_retry_title),getResources().getString(R.string.signature_retry_sub_title)));

		mSignatureAdapter = new ListAdapter(this);

		mSignatureList = (ListView) findViewById(R.id.signature_list);
		mSignatureList.setAdapter(mSignatureAdapter);

		mSignatureList.setItemsCanFocus(false);
		mSignatureList.setTextFilterEnabled(true);
		mSignatureList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					mResult = mSCanvas.registerSignature();
					if (mSCanvas.isSignatureRegistrationCompleted() && mResult > 0) {
						finish();
						// Signature registration is completed
						Toast.makeText(SPen_Example_SignatureRegistration.this, getResources().getString(R.string.signature_registration_registration_success_3), Toast.LENGTH_SHORT).show();
					} else {
						if (mResult == 2) {
							mSigntureRegistrationNum = mResult;
							// Signature registration is not completed
							sToastS(getResources().getString(R.string.signature_registration_registration_success_2));
						} else if (mResult == 1) {
							mSigntureRegistrationNum = mResult;
							// Signature registration is not completed
							sToastS(getResources().getString(R.string.signature_registration_registration_success_1));
						} else {
							// Signature registration error
							sToastS(getResources().getString(R.string.signature_registration_registration_failure));
						}							
					}
					mSignatureAdapter.notifyDataSetChanged();
				} else if (position == 1) {
					if (mSCanvas.clearSignatureScreen()) {
						// Canvas reset success
						sToastS(getResources().getString(R.string.signature_registration_draw));
					}
				}
			}
		});

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
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

			if (position == 0) {
				TextView title = (TextView) convertView.findViewById(R.id.signature_list_title);
				title.setText(mSignatureListItem.get(position).Title + " - ( " + mSigntureRegistrationNum + " / " + mSigntureRegistrationNumMax + " )");

				TextView subtitle = (TextView) convertView.findViewById(R.id.signature_list_subtitle);
				subtitle.setText(mSignatureListItem.get(position).subTitle);
			} else {
				TextView title = (TextView) convertView.findViewById(R.id.signature_list_title);
				title.setText(mSignatureListItem.get(position).Title);

				TextView subtitle = (TextView) convertView.findViewById(R.id.signature_list_subtitle);
				subtitle.setText(mSignatureListItem.get(position).subTitle);
			}

			return convertView;
		}

	}

	@Override
	public void onBackPressed() {
		mSCanvas.closeSignatureEngine();
		super.onBackPressed();
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
