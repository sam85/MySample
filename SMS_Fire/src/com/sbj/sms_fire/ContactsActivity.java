package com.sbj.sms_fire;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ContactsActivity extends Activity {

	private ContactsAdapter adapter;
	private ArrayList<ContactModel> arrListContactModel;
	private StringBuilder contactList;
	private EditText edContacts;
	private List<String> lstSelectedContacts;
	private ListView lstView;
	private TextWatcher onSearchTextChange = new TextWatcher() {
		public void afterTextChanged(Editable paramAnonymousEditable) {
		}

		public void beforeTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
		}

		public void onTextChanged(CharSequence paramAnonymousCharSequence,
				int paramAnonymousInt1, int paramAnonymousInt2,
				int paramAnonymousInt3) {
			ContactsActivity.this.adapter = new ContactsAdapter(
					ContactsActivity.this,
					ContactsActivity.this
							.filterText(paramAnonymousCharSequence));
			ContactsActivity.this.lstView
					.setAdapter(ContactsActivity.this.adapter);
		}
	};

	private String selctedContacts;
	private StringBuilder usersContactList;

	private ArrayList<ContactModel> filterText(CharSequence paramCharSequence) {
		ArrayList localArrayList = new ArrayList();
		for (int i = 0;; i++) {
			if (i >= this.arrListContactModel.size())
				return localArrayList;
			ContactModel localContactModel = (ContactModel) this.arrListContactModel
					.get(i);
			if (localContactModel
					.getContactName()
					.trim()
					.toLowerCase()
					.contains(paramCharSequence.toString().trim().toLowerCase()))
				localArrayList.add(localContactModel);
		}
	}

	private Cursor getContacts() {
		return managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] { "_id", "display_name", "data1" }, null,
				(String[]) null, "display_name COLLATE LOCALIZED ASC");

		// try {
		//
		// String[] projection = new String[] {
		// ContactsContract.Contacts.DISPLAY_NAME,
		// ContactsContract.Contacts.HAS_PHONE_NUMBER,
		// ContactsContract.Contacts._ID };
		//
		// Cursor cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI,
		// projection, ContactsContract.Contacts.HAS_PHONE_NUMBER
		// + "=?", new String[] { "1" },
		// ContactsContract.Contacts.DISPLAY_NAME);
		//
		// return cursor;
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// return null;
		// }

	}

	private void initializeComponents() {

		this.edContacts = ((EditText) findViewById(R.id.locateme_contacts_edContacts));
		this.lstView = ((ListView) findViewById(R.id.locateme_contacts_lstView));
		this.arrListContactModel = new ArrayList();
		this.selctedContacts = getIntent().getStringExtra("SELECTED_ID");
		this.usersContactList = new StringBuilder();
		String[] arrayOfString = this.selctedContacts.split(",");
		this.lstSelectedContacts = new ArrayList();
		Cursor localCursor;

		this.edContacts.addTextChangedListener(this.onSearchTextChange);
		localCursor = getContacts();
		Log.e("", "Count :" + localCursor.getCount());
		localCursor.moveToFirst();

		do {

			ContactModel localContactModel = new ContactModel();
			localContactModel.setContactID(localCursor.getString(localCursor
					.getColumnIndex("_id")));
			localContactModel.setContactName(localCursor.getString(localCursor
					.getColumnIndex("display_name")));
			String str2 = localCursor.getString(localCursor
					.getColumnIndex("data1"));
			localContactModel.setContactNumber(str2);

			if (this.lstSelectedContacts.contains(str2))
				localContactModel.setSelectFlag(true);
			else {
				this.arrListContactModel.add(localContactModel);
				localContactModel.setSelectFlag(false);
			}

			if (localCursor.isLast()) {
				localCursor.close();
				this.adapter = new ContactsAdapter(this,
						this.arrListContactModel);
				this.lstView.setAdapter(this.adapter);
				break;
			}

		} while (localCursor.moveToNext());
	}

	public void onButtonClick(View paramView) {
		((InputMethodManager) getSystemService("input_method"))
				.hideSoftInputFromWindow(this.edContacts.getWindowToken(), 0);
		this.contactList = new StringBuilder();
		for (int i = 0;; i++) {
			if (i >= this.arrListContactModel.size()) {
				Intent localIntent = new Intent();
				localIntent.putExtra(
						"CONTACT_DATA",
						this.contactList.toString()
								+ this.usersContactList.toString());
				setResult(-1, localIntent);
				finish();
				return;
			}
			ContactModel localContactModel = (ContactModel) this.arrListContactModel
					.get(i);
			if (localContactModel.isSelectFlag())
				this.contactList.append(localContactModel.getContactName()
						+ "<" + localContactModel.getContactNumber() + ">,");
		}
	}

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		requestWindowFeature(1);
		setContentView(R.layout.contacts_activity);
		initializeComponents();
		this.edContacts
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(
							TextView paramAnonymousTextView,
							int paramAnonymousInt,
							KeyEvent paramAnonymousKeyEvent) {
						if (paramAnonymousInt == 6)
							((InputMethodManager) ContactsActivity.this
									.getSystemService("input_method"))
									.hideSoftInputFromWindow(
											ContactsActivity.this.edContacts
													.getWindowToken(), 0);
						return false;
					}
				});
	}
}
