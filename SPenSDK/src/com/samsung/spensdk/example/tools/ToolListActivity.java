package com.samsung.spensdk.example.tools;


import java.io.File;

import android.app.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.TextView;
import android.content.Intent;

import com.samsung.spensdk.example.R;

public class ToolListActivity extends Activity {

	static public final String EXTRA_LIST_PATH = "ExtraListPath"; 
	static public final String EXTRA_FILE_EXT_ARRAY = "ExtraFileExts";
	static public final String EXTRA_SELECTED_FILE = "ExtraSelectedPath";
	static public final String EXTRA_SEARCH_ONLY_SAMM_FILE = "ExtraSearchOnlySammFile";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tool_file_list);

		ToolFileListView fListView = (ToolFileListView) findViewById(R.id.fileListView);

		Intent intent = getIntent();

		// Set search file path
		String strPath = intent.getStringExtra(EXTRA_LIST_PATH);
		if(strPath==null){
			File sdcard_path = Environment.getExternalStorageDirectory();
			strPath = sdcard_path.getPath();
		}
		// Set search file extension
		String [] exts = intent.getStringArrayExtra(EXTRA_FILE_EXT_ARRAY);
		boolean bSearchOnlySammFile = intent.getBooleanExtra(EXTRA_SEARCH_ONLY_SAMM_FILE, true);

		// Set list view by search normal file or SAMM file
		fListView.setFilePath(strPath, exts, bSearchOnlySammFile);


		fListView.setOnFileSelectedListener(mFileSelectedListener);
		TextView statusTextView = (TextView) findViewById(R.id.statusTitle);
		String status;

		if(fListView.isEmpty())
			status = "File not Found";		
		else
		{
			int count = fListView.getListCount();

			if(count == 1)
				status = "Total (1) File";
			else
				status = "Total (" + fListView.getListCount() + ") Files";			
		}

		statusTextView.setText(status);

		fListView.setFocusable(true);
		fListView.setFocusableInTouchMode(true);		
	}

	private OnFileSelectedListener mFileSelectedListener = new OnFileSelectedListener() {

		@Override
		public void onSelected(String path, String fileName) {
			Intent intent = getIntent();
			String strSelectFileName = path + fileName; 
			intent.putExtra(EXTRA_SELECTED_FILE, strSelectFileName); 
			setResult(RESULT_OK, intent);	
			finish();
		}
	};
}
