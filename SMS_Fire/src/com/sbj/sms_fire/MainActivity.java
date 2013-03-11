package com.sbj.sms_fire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	EditText edtMessageBody;
	TextView txtMessageCount;
	Button btnClearContacts;
	Button btnSaveSchedule;
	Button btn_getContacts;
	CustomAutoComplete edtContactInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_entry);
		init();
	}

	private void init() {

		edtMessageBody = (EditText) findViewById(R.id.addeventactivity_et_message);
		txtMessageCount = (TextView) findViewById(R.id.addeventactivity_tv_messagebodycount);
		btnClearContacts = (Button) findViewById(R.id.add_event_btn_clearContacts);
		edtContactInfo = (CustomAutoComplete) findViewById(R.id.addeventactivity_et_to);
		
		ContactListAdapter localContactListAdapter = new ContactListAdapter(
				this, managedQuery(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						new String[] { "_id", "display_name", "data1" }, null,
						(String[]) null, "display_name COLLATE LOCALIZED ASC"));
		this.edtContactInfo.setAdapter(localContactListAdapter);
		

		btnSaveSchedule = (Button) findViewById(R.id.addeventactivity_btn_save);

		btn_getContacts = (Button) findViewById(R.id.add_event_btn_getContacts);
		btn_getContacts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent localIntent = new Intent(getBaseContext(),
						ContactsActivity.class);
				localIntent.putExtra("SELECTED_ID", "");
				startActivityForResult(localIntent, 100);
			}
		});
	}

}
