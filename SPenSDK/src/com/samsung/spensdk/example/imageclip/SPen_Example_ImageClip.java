package com.samsung.spensdk.example.imageclip;
/*
 * Sample application constraint
 * Max VM Application heap size should not be less than 64 Mbyte
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;

import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_ImageClip extends Activity{

	private final int REQUEST_CODE_CANVAS = 1000;

	private final int MAX_BITMAP_SIZE = 500;

	private Button		mNewButton;
	private EditText 	mEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example_imageclip_main);

		mNewButton = (Button)findViewById(R.id.new_btn);
		mNewButton.setOnClickListener(newClickListener);

		mEditText = (EditText)findViewById(R.id.main_edittext);
		mEditText.setScroller(new Scroller(this));
		mEditText.addTextChangedListener(watcher);
	}

	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	} 

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE_CANVAS){
			if(resultCode == RESULT_OK){
				Uri uri = data.getData();

				if(uri != null){
					Bitmap bitmap = getBitmapFromFile(uri);
					// resizie
					bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true);

					if(bitmap != null){
						int selStart = mEditText.getSelectionStart();
						int selEnd = mEditText.getSelectionEnd();

						int min = Math.min(selStart, selEnd);
						int max = Math.max(selStart, selEnd);

						if (min < 0) {
							min = 0;
						}
						if (max < 0) {
							max = 0;
						}

						CharSequence span = "\u3000";

						Selection.setSelection(mEditText.getText(), max);
						(mEditText.getText()).replace(min, max, span);
						ImageSpan imageSpan = new ImageSpan(this, bitmap);
						int textLength = mEditText.getText().length();
						if(min >= textLength)
							min = textLength-1;
						mEditText.getText().setSpan(imageSpan,  min, min + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					}
				}
			}
		}
	}

	private OnClickListener newClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(SPen_Example_ImageClip.this, SPen_Example_ImageClip_MiniEditor.class);
			startActivityForResult(intent, REQUEST_CODE_CANVAS);
		}
	};

	private Bitmap getBitmapFromFile(Uri imageUri){
		Bitmap bmp = null;
		AssetFileDescriptor  aFD = null;
		try {
			aFD = getContentResolver().openAssetFileDescriptor(imageUri, "r"); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (aFD != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			InputStream is;
			try {
				is = aFD.createInputStream();
				BitmapFactory.decodeStream(is, null, options);
				is.close();

				options.inJustDecodeBounds = false;

				int bw = options.outWidth;
				int bh = options.outHeight;

				float sw = (float) MAX_BITMAP_SIZE/bw;
				float sh = (float) MAX_BITMAP_SIZE/bh;
				float scale = Math.min(sw, sh);

				if(scale >= 1)
					scale = 1;

				options.inSampleSize = (int)(1/scale);    
				try {
					aFD = getContentResolver().openAssetFileDescriptor(imageUri, "r"); 
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				is = aFD.createInputStream();
				bmp = BitmapFactory.decodeStream(is, null, options);
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bmp;
	}

	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable arg0) {
		}
	};

}
