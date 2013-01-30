package com.samsung.spensdk.example.bgfg;

import java.io.File;

import android.app.Activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.widget.TextView;
import android.content.Intent;
import com.samsung.spensdk.example.R;

public class ListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editor_bg_fg_list);

		FileListView fListView = (FileListView) findViewById(R.id.fileListView);

		File sdcard_path = Environment.getExternalStorageDirectory();
		String strPath = sdcard_path.getPath() + "/" + SPen_Example_BackgroundForeground.DEFAULT_APP_IMAGEDATA_DIRECTORY;

		fListView.setPath(strPath);

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
			intent.putExtra(SPen_Example_BackgroundForeground.EXTRA_IMAGE_PATH, path); 
			intent.putExtra(SPen_Example_BackgroundForeground.EXTRA_IMAGE_NAME, fileName);
			setResult(RESULT_OK, intent);	
			finish();
		}
	};
}
