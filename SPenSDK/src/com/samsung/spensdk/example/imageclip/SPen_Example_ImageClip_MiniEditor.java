package com.samsung.spensdk.example.imageclip;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_ImageClip_MiniEditor extends Activity {

	private SCanvasView mSCanvas;

	private ImageView mPenBtn;
	private ImageView mEraserBtn;

	private ImageView mUndoBtn;
	private ImageView mRedoBtn;

	private Button mDoneBtn;
	private Button mCancelBtn;

	Context mContext = null;
	public static final String TAG = "Example2";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sdk_example_mini_edit);

		mContext = this;

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);

		// Create Setting View
		RelativeLayout settingViewContainer = (RelativeLayout) findViewById(R.id.canvas_container);		

		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, false, false);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, false, false);
		// Create Setting View
		mSCanvas.createSettingView(settingViewContainer, settingResourceMapInt, settingResourceMapString);

		//------------------------------------------------
		// Set SCanvas Initialize Listener
		//------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
			@Override
			public void onInitialized() { 
				// Set Background as bright yellow
				if(!mSCanvas.setBGColor(0xFFFFFFBB))
					Toast.makeText(mContext, "Fail to set Background color.", Toast.LENGTH_LONG).show();
			}
		});
		//------------------------------------------------
		// History Change Listener
		//------------------------------------------------
		mSCanvas.setHistoryUpdateListener(new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		});
		
		//------------------------------------------------
		// SCanvas Mode Changed Listener 
		//------------------------------------------------
		mSCanvas.setSCanvasModeChangedListener(new SCanvasModeChangedListener() {

			@Override
			public void onModeChanged(int mode) {
				updateModeState();				
			}
		});
		
		
		mPenBtn = (ImageView) findViewById(R.id.penSetting_btn);
		mEraserBtn = (ImageView) findViewById(R.id.eraserSetting_btn);
		mPenBtn.setOnClickListener(toolClickListener);
		mEraserBtn.setOnClickListener(toolClickListener);
		
		mPenBtn.setSelected(true);

		mUndoBtn = (ImageView) findViewById(R.id.undo_btn);
		mRedoBtn = (ImageView) findViewById(R.id.redo_btn);
		mUndoBtn.setOnClickListener(undoRedoClickListener);
		mRedoBtn.setOnClickListener(undoRedoClickListener);
		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);

		mDoneBtn = (Button) findViewById(R.id.done_btn);
		mCancelBtn = (Button) findViewById(R.id.cancel_btn);
		mDoneBtn.setOnClickListener(doneClickListener);
		mCancelBtn.setOnClickListener(doneClickListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}

	
	private OnClickListener toolClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
					updateModeState();
				}
			}
			else if(nBtnID == mEraserBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			}			
		}
	};

	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
	}

	private OnClickListener doneClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mDoneBtn)) {

				File file = saveCanvasImage();
				if (file != null) {
					Intent attachIntent = new Intent();
					attachIntent.setData(Uri.fromFile(file));
					setResult(RESULT_OK, attachIntent);

					finish();
				}

			} else if (v.equals(mCancelBtn)) {
				finish();
			}
		}
	};



	private OnClickListener undoRedoClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			} else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
		}
	};

	private File saveCanvasImage() {
		byte[] imgData = mSCanvas.getSCanvasBitmapData();
		if(imgData == null)
			return null;

		String fileName = "tempImg.jpg";
		OutputStream out = null;
		try {
			File imgFile = new File(getFilesDir(), fileName);
			out = openFileOutput(imgFile.getName(), MODE_WORLD_READABLE | MODE_WORLD_WRITEABLE);
			out.write(imgData);
			return imgFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
