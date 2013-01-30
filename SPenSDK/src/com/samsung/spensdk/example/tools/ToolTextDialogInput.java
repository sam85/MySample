package com.samsung.spensdk.example.tools;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;

import com.samsung.spensdk.example.R;

public class ToolTextDialogInput extends Activity {

	private EditText mEt;
	private Button mBtnOk;
	private Button mBtnCancel;
	private Intent mIntent;	
	private String mTmpStr = null;

	public static final String  TEXT_DIALOG_INPUT = "key_text_dialog_input";	

	private InputMethodManager mIME;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mIntent = getIntent();

		setContentView(R.layout.tool_text_dialog_input);		

		mEt = (EditText)findViewById(R.id.inputfilename);
		mBtnOk = (Button)findViewById(R.id.btn_input_ok);
		mBtnCancel = (Button)findViewById(R.id.btn_input_cancel);

		mEt.addTextChangedListener(watcher);		

		mIME = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 

		// Set initial text
		mTmpStr = mIntent.getStringExtra(TEXT_DIALOG_INPUT);
		if(mTmpStr == null) {
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);			
			mEt.setLayoutParams(params);				
			inputTextAction();
		}
		else {
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);			
			mEt.setLayoutParams(params);				
			mEt.setText(mTmpStr);			
			inputTextAction();
		}		

		int len = mEt.length();
		if(len>0) mEt.setSelection(len);

		// Set OK button 
		if (mEt.getText().toString().trim().length() > 0){
			mBtnOk.setEnabled(true);
		}else{
			mBtnOk.setEnabled(false);
		}		
	}

	void startIME()
	{
		TimerTask myTask = new TimerTask(){
			public void run(){
				if(mIME==null)
					mIME = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if(!mIME.isActive())
				{								 
					mIME.showSoftInput(mEt, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		};

		Timer timer = new Timer();
		timer.schedule(myTask, 500);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mEt	= null;
	}

	TextWatcher watcher = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			s.toString();
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			// Set OK button 
			if (mEt.getText().toString().trim().length() > 0){
				mBtnOk.setEnabled(true);
			}else{
				mBtnOk.setEnabled(false);
			}
		}
	};

	@Override
	protected void onPause() {
		hideSoftInput();		
		mIME=null;

		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();		

		startIME();
	}

	private void hideSoftInput() {
		if(mIME != null)
			mIME.hideSoftInputFromWindow(mEt.getWindowToken(), 0);
	}

	private void inputTextModeSelcted (){

		String inputStr = mEt.getText().toString();

		if(inputStr.length() == 0) {
			finish();
		}else {
			mIntent.putExtra(TEXT_DIALOG_INPUT, inputStr);
			setResult(RESULT_OK, mIntent);
			finish();	
		}	

	}	

	private void inputTextAction() {

		mBtnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				inputTextModeSelcted ();
			}
		});

		mBtnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}        
}
