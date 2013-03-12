package com.sbj.sms_fire;

import java.util.Calendar;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sbj.sms_fire.adapter.ContactListAutoAdapter;
import com.sbj.sms_fire.dao.DBAdapter;
import com.sbj.sms_fire.model.clsEntryModule;
import com.sbj.sms_fire.view.CustomAutoComplete;
import com.vcs.function.MyFunction;

public class MainActivity extends Activity implements OnItemSelectedListener {

	EditText edtMessageBody;
	EditText edtEventName;
	TextView txtMessageCount;
	Button btnClearContacts;
	Button btnSaveSchedule;
	Button btn_getContacts;
	CustomAutoComplete edtContactInfo;
	Spinner interval_spinner;
	DBAdapter db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_entry);
		CreateDatabase();
		init();
	}
	
	private void CreateDatabase() {
		try {	
			db = new DBAdapter(this);
			db.createDatabase();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}		
	}

	private void init() {
	
		edtEventName = (EditText)findViewById(R.id.addeventactivity_et_eventname);
		edtMessageBody = (EditText) findViewById(R.id.addeventactivity_et_message);
		txtMessageCount = (TextView) findViewById(R.id.addeventactivity_tv_messagebodycount);
		edtMessageBody.addTextChangedListener(countTextWatcher);
		btnClearContacts = (Button) findViewById(R.id.add_event_btn_clearContacts);
		btnClearContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showClearContactDialog();
			}
		});
		edtContactInfo = (CustomAutoComplete) findViewById(R.id.addeventactivity_et_to);
		SetAdapter();

		// ContactListAdapter localContactListAdapter = new ContactListAdapter(
		// this, managedQuery(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		// new String[] { "_id", "display_name", "data1" }, null,
		// (String[]) null, "display_name COLLATE LOCALIZED ASC"));
		// this.edtContactInfo.setAdapter(localContactListAdapter);

		btnSaveSchedule = (Button) findViewById(R.id.addeventactivity_btn_save);
		btnSaveSchedule.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showAlertSchedule();
			}
		});

		btn_getContacts = (Button) findViewById(R.id.add_event_btn_getContacts);
		btn_getContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent localIntent = new Intent(getBaseContext(),
						ContactsList.class);
				localIntent.putExtra("SELECTED_ID", edtContactInfo.getText()
						.toString());
				startActivityForResult(localIntent, 100);
			}
		});

		interval_spinner = (Spinner) findViewById(R.id.interval_spinner);
		SetSpinnerData();
		
	}

	private void SetSpinnerData() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.interval_array,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		interval_spinner.setAdapter(adapter);
		interval_spinner.setOnItemSelectedListener(this);
	}

	private void SetAdapter() {
		final Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		final String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER };
		final String[] selectionArgs = null;

		final String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";
		Cursor cContacts = managedQuery(uri, projection, null, selectionArgs,
				sortOrder);
		ContactListAutoAdapter adapter = new ContactListAutoAdapter(this, cContacts);

		edtContactInfo.setAdapter(adapter);
	}

	TextWatcher countTextWatcher = new TextWatcher() {
		public void afterTextChanged(Editable paramAnonymousEditable) {
		}

		public void beforeTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
		}

		public void onTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
			txtMessageCount.setText(String.valueOf(edtMessageBody.getText()
					.toString().length()));
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 100) {
			if (resultCode == Activity.RESULT_OK) {

				String strContact = data.getExtras().getString("CONTACT_DATA");
				edtContactInfo.setText(strContact);

				String strEdtText = edtContactInfo.getText().toString().trim();
				if (strEdtText != null && !strEdtText.equals("")) {
					strEdtText = strEdtText.replace(",,", ",");
					edtContactInfo.setText(strEdtText);
					char c = strEdtText.charAt(0);
					if (c == ',') {
						strEdtText = strEdtText.substring(1);
						edtContactInfo.setText(strEdtText);
					}
					edtContactInfo.setSelection(edtContactInfo.getText()
							.length());
				}

			}
		}
	}

	private void setAlarm(int eventid, long timeinmillis) {
		Intent intent = new Intent(this, ContactsList.class);
		intent.putExtra("Event id", eventid);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, eventid,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, timeinmillis, pendingIntent);
	}

	private void showClearContactDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Do you want clear all contacts?")
				.setCancelable(false)
				.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								edtContactInfo.setText("");
							}
						})
				.setNegativeButton(getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showAlertSchedule() {

		String strMsg, contactData;
		boolean flag = true;

		strMsg = edtMessageBody.getText().toString().trim();
		contactData = edtContactInfo.getText().toString().trim();

		// Validations for different fields.
		// Validations Starts...

		 if(edtEventName == null || edtEventName.equals("")) {
				flag = false;
				showAlert(this, getString(R.string.AlertTitle),
						getString(R.string.enterEventName));
				edtEventName.requestFocus();
			}
		 else if (contactData == null || contactData.equals("")) {
			flag = false;
			showAlert(this, getString(R.string.AlertTitle),
					getString(R.string.enterContactNumber));
			edtContactInfo.requestFocus();

		}else if (strMsg == null || strMsg.equals("")) {
			flag = false;
			showAlert(this, getString(R.string.AlertTitle),
					getString(R.string.enterMsg));
			edtMessageBody.requestFocus();

		}
		else
		{
			SaveSmsEntry();
		}
		// else if (!validateContactNumber()) {
		// flag = false;
		// showAlert(this, getString(R.string.AlertTitle),
		// getString(R.string.enterValidNumber));
		// }
		//
		// if (flag == true) {
		// // saveEvent();
		// new SaveEventDBTask().execute();
		// }

		// Validations Ends...
	}
	
	private void SaveSmsEntry()
	{
		clsEntryModule objEntModule = new clsEntryModule(edtEventName.getText().toString().trim(),GetContactSplit(), 
				edtMessageBody.getText().toString().trim(), interval_spinner.getSelectedItem().toString(),
				MyFunction.GetCurrentDate("MM/dd/yyyy HH:mm:ss"));
		db.open();
		db.InsertEntry(objEntModule);
		db.close();	
		showAlert(this, getString(R.string.AlertTitle),
				getString(R.string.SaveData));
	}
	
	private String GetContactSplit()
	{
		String ContactList = "";
		String contactData = edtContactInfo.getText().toString().trim();
		String[] tokens = contactData.split(",");
		for (int i = 0; i < tokens.length; i++) {
			
			if (tokens[i].contains("<") && tokens[i].contains(">")) {

				StringTokenizer stringTokenizer = new StringTokenizer(
						tokens[i], "<>");

				if (stringTokenizer.countTokens() == 2) {

					String name = stringTokenizer.nextToken();
					String number = stringTokenizer.nextToken();
//					if (lstSelectedConatcs.contains(number)) {
//						continue;
//					} else {
//						lstSelectedConatcs.add(number);
					  if(ContactList.length() > 0)
						ContactList +=  ", "+number;
					  else
						  ContactList += number;
//					}

				} else if (stringTokenizer.countTokens() > 0) {

					String strData = stringTokenizer.nextToken().toString();
					if(ContactList.length() > 0)
					ContactList += ", "+strData;
					else
						ContactList += strData;
							

//					if ((isNumber(strData))) {
//						if (lstSelectedConatcs.contains(strData)) {
//							continue;
//						} else {
//							eventContactModel.setContactName("");
//							eventContactModel.setContactNumber(strData);
//							lstSelectedConatcs.add(strData);
//						}
//
//					} else {
//						flagAlert = false;
//					}
				}
			} else {
				if(ContactList.length() > 0)
				ContactList += ", "+tokens[i];
				else
					ContactList += tokens[i];
//				if ((isNumber(tokens[i]))) {
//					// its number only
//					eventContactModel.setContactName("");
//					eventContactModel.setContactNumber(tokens[i]);
//				}
			}

			
		}
		Log.e("ContactList "," : "+ContactList);
		return ContactList;
	}

	public void showAlert(Context mContext, String title, String message) {
		AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
		alt_bld.setMessage(message)
				.setCancelable(false)
				.setNegativeButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_bld.create();
		alert.setTitle(title);
		alert.show();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		Log.e("Position ", ":" + arg0.getItemAtPosition(arg2));
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
