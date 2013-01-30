package com.samsung.spensdk.example.signature;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;

public class SPen_Example_SignatureVerification extends Activity {

	private final String TAG = "SPenSDK Sample";
	public ArrayList<ListItem> mSignatureListItem;
	public SCanvasView mSCanvas;
	public ListAdapter mSignatureAdapter;
	public ListView mSignatureList;
	private Toast m_Toast = null;
	int mDialogSelect = 1;
	int mVerificationLevel = SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_MEDIUM;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_signature_verification);

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
		sToastS(getResources().getString(R.string.signature_verification_draw));

		mSignatureListItem = new ArrayList<ListItem>();
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_verification_verification_title), getResources().getString(
				R.string.signature_verification_verification_sub_title)));
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_verification_level_title), getResources().getString(R.string.signature_verification_level_sub_title)));
		mSignatureListItem.add(new ListItem(getResources().getString(R.string.signature_retry_title), getResources().getString(R.string.signature_retry_sub_title)));

		mSignatureAdapter = new ListAdapter(this);

		mSignatureList = (ListView) findViewById(R.id.signature_list);
		mSignatureList.setAdapter(mSignatureAdapter);
		mSignatureList.setItemsCanFocus(false);
		mSignatureList.setTextFilterEnabled(true);

		mSignatureList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					if (mSCanvas.verifySignature(mVerificationLevel)) {
						// Signature verification success
						sToastS(getResources().getString(R.string.signature_verification_verification_success));
					} else {
						// Signature verification failure
						sToastS(getResources().getString(R.string.signature_verification_verification_failure));
					}
				} else if (position == 1) {

					AlertDialog.Builder ab = new AlertDialog.Builder(SPen_Example_SignatureVerification.this);

					if (mVerificationLevel == SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_LOW) {
						mDialogSelect = 0;
					} else if (mVerificationLevel == SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_MEDIUM) {
						mDialogSelect = 1;
					} else if (mVerificationLevel == SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_HIGH) {
						mDialogSelect = 2;
					}

					ab.setTitle(getResources().getString(R.string.signature_verification_level_sub_title))
					.setSingleChoiceItems(R.array.signature_threshold, mDialogSelect, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mDialogSelect = which;
						}
					}).setPositiveButton(getResources().getString(R.string.signature_verification_level_confirm), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							if (mDialogSelect == 0) {
								mVerificationLevel = SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_LOW;
							} else if (mDialogSelect == 1) {
								mVerificationLevel = SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_MEDIUM;
							} else if (mDialogSelect == 2) {
								mVerificationLevel = SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_HIGH;
							}
							mSignatureAdapter.notifyDataSetChanged();
						}
					}).setNegativeButton(getResources().getString(R.string.signature_verification_level_cancel), null).show();
				} else if (position == 2) {
					if (mSCanvas.clearSignatureScreen()) {
						// Canvas reset success
						sToastS(getResources().getString(R.string.signature_verification_draw));
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


	// Items for ListView
	class ListItem {
		ListItem(String iTitle, String isubTitle) {
			Title = iTitle;
			subTitle = isubTitle;
		}

		String Title;
		String subTitle;
	}

	// Adapter class for list Item
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
			return mSignatureListItem.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = Inflater.inflate(R.layout.example_signature_list_item, parent, false);
			}

			if (position == 0) {
				TextView title = (TextView) convertView.findViewById(R.id.signature_list_title);
				if (mVerificationLevel == SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_LOW) {
					title.setText(mSignatureListItem.get(position).Title + "  ( Level = " + getResources().getString(R.string.signature_verification_level_low) + " )");
				} else if (mVerificationLevel == SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_MEDIUM) {
					title.setText(mSignatureListItem.get(position).Title + "  ( Level = " + getResources().getString(R.string.signature_verification_level_medium) + " )");
				} else if (mVerificationLevel == SCanvasConstants.SIGNATURE_VERIFICATION_LEVEL_HIGH) {
					title.setText(mSignatureListItem.get(position).Title + "  ( Level = " + getResources().getString(R.string.signature_verification_level_high) + " )");
				}

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
