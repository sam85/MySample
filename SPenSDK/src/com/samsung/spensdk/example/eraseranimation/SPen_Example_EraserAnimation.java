package com.samsung.spensdk.example.eraseranimation;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_EraserAnimation extends Activity {

	private final String TAG = "SPenSDK Sample";

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private String APPLICATION_ID_NAME = "SDK Sample Application";
	private int APPLICATION_ID_VERSION_MAJOR = 2;
	private int APPLICATION_ID_VERSION_MINOR = 0;
	private String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	Context mContext = null;

	private final int MENU_PLAY_ERASER = 1001;	
	private final int MENU_DATA_BG_SET_IMAGE = 1003;
	private final int MENU_IMAGE_OPERATION = 1004;
	private final int MENU_IMAGE_OPERATION_LEVEL = 1005;	
	private final int MENU_RESET = 1006;

	private int mImageOperationIndex = 2; // IMAGE_OPERATION_DARK
	private int mImageOperationLevelIndex = 2; // OPERATION_LEVEL_MEDIUM
	private int mImageOperation = SPenImageFilterConstants.FILTER_DARK;	
	private int mImageOperationLevel = SPenImageFilterConstants.FILTER_LEVEL_MEDIUM;

	private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 106;
	private final int REQUEST_CODE_PRIVIEW_BUTTON_CLICK = 108;

	private SCanvasView		mSCanvas;
	private ImageView		mEraserBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;
	private ImageView		mPreviewBtn;

	private boolean			mbPlayAsEraserAnimation = true; 

	private Bitmap mBmScratchUser = null;

	private boolean mbContentsOrientationHorizontal = false;
	private boolean mbCurrentScreenOrientationHorizontal = false;

	private int mBackgroundResourceId = 0;

	private boolean mbPreviewBtnClick = false;

	// Conversion array to map menu index to Image Filter Index (only 10 Image filter)
	private int[] imageOperationByIndex = {SPenImageFilterConstants.FILTER_GRAY, 
			SPenImageFilterConstants.FILTER_NEGATIVE, SPenImageFilterConstants.FILTER_DARK, SPenImageFilterConstants.FILTER_COLORIZE, 
			SPenImageFilterConstants.FILTER_BLUR, SPenImageFilterConstants.FILTER_PENCILSKETCH,	SPenImageFilterConstants.FILTER_RETRO,
			SPenImageFilterConstants.FILTER_MOSAIC, SPenImageFilterConstants.FILTER_MAGICPEN, SPenImageFilterConstants.FILTER_CARTOONIZE,};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.example_eraser_animation);

		mContext = this;

		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
		mPreviewBtn = (ImageView) findViewById(R.id.previewBtn);
		mPreviewBtn.setOnClickListener(previewBtnClickListener);

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);
		
		RelativeLayout canvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);

		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(false, true, false, false);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(false, true, false, false);
		// New Example Code1 : create SettingView by SCanvasView
		mSCanvas.createSettingView(canvasContainer, settingResourceMapInt, settingResourceMapString);
		// Don't restore setting view
		mSCanvas.setEnableSettingRestore(false);

		//------------------------------------------------
		// SCanvas Listener
		//------------------------------------------------
		SCanvasInitializeListener mSCanvasListener = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() { 
				//--------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				//--------------------------------------------			
				// Application Identifier Setting
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Set Title
				if(!mSCanvas.setTitle("SPen-SDK Test"))
					Toast.makeText(mContext, "Fail to set Title.", Toast.LENGTH_LONG).show();

				mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);

				// Get the direction of contents(Canvas)
				if(mSCanvas.getWidth() > mSCanvas.getHeight())
					mbContentsOrientationHorizontal = true;
				else
					mbContentsOrientationHorizontal = false;

				resetClearImage(true, mImageOperation, mImageOperationLevel);
			}
		};

		//------------------------------------------------
		// History Change
		//------------------------------------------------
		HistoryUpdateListener mHistoryListener = new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		};

		//------------------------------------------------
		// OnSettingStrokeChangeListener Listener 
		//------------------------------------------------				
		SettingStrokeChangeListener	mSettingStrokeChangeListener = new SettingStrokeChangeListener() {
			@Override
			public void onClearAll(boolean bClearAllCompleted) {
				// If don't set eraser mode, then change to pen mode automatically.
				if(bClearAllCompleted)
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
			}
			@Override
			public void onEraserWidthChanged(int eraserWidth) {				
			}

			@Override
			public void onStrokeColorChanged(int strokeColor) {
			}

			@Override
			public void onStrokeStyleChanged(int strokeStyle) {			
			}

			@Override
			public void onStrokeWidthChanged(int strokeWidth) {				
			}

			@Override
			public void onStrokeAlphaChanged(int strokeAlpha) {								
			}
		};


		mSCanvas.setSCanvasInitializeListener(mSCanvasListener);        
		mSCanvas.setHistoryUpdateListener(mHistoryListener);
		mSCanvas.setSettingStrokeChangeListener(mSettingStrokeChangeListener);

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);


		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView/CanvasView Task at onInitialized() of SCanvasInitializeListener
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}
	
	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	} 

	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			} else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};




	private OnClickListener previewBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mPreviewBtn)) {
				if(mbPreviewBtnClick)
					return;
				mbPreviewBtnClick = true;
				previewAnimation(mbPlayAsEraserAnimation);	
			}
		}
	};


	OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mEraserBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
				}
			}			
		}
	};




	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK){
			if(data == null)
				return;

			if(requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {    			
				Uri imageFileUri = data.getData();
				String strBackgroundImagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);
				// Check Valid Image File
				if(!SPenSDKUtils.isValidImagePath(strBackgroundImagePath))
				{
					Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
					return;
				}

				mBmScratchUser = SPenSDKUtils.getSafeResizingBitmap(strBackgroundImagePath, mSCanvas.getWidth()/2, mSCanvas.getHeight()/2, true);
				if(mBmScratchUser == null)
					return;

				// Set Scratch Bitmap
				resetClearImage(false, mImageOperation, mImageOperationLevel);
			}			
			else if(requestCode==REQUEST_CODE_PRIVIEW_BUTTON_CLICK) {    			
				mbPreviewBtnClick = data.getBooleanExtra(SPen_Example_AnimationEraserViewer.EXTRA_PLAY_BUTTON_CLICK, false);
			}
		}
	}



	void resetClearImage(boolean bClearAll, int nImageOperationOption, int nLevel){
		if(bClearAll){
			//Set Image Operation
			//Set edit background (initial background setting)			
			if(mBmScratchUser == null) {
				mBackgroundResourceId = R.drawable.scratch_bg;
				mBmScratchUser = BitmapFactory.decodeResource(getResources(), mBackgroundResourceId);
			}							
			if(!mSCanvas.setClearImageBitmap(mBmScratchUser, nImageOperationOption, nLevel))
				return;
		}else {
			if(!mSCanvas.setClearImageBitmap(mBmScratchUser, nImageOperationOption, nLevel))
				return;	
		}

		// Set SCanvas				
		if(!mSCanvas.setBackgroundImage(mBmScratchUser)){
			Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();
		}
	}


	void previewAnimation(boolean bPlayAsEraserAnimation){
		// temporarily save SAMMData
		String sDataKey = mSCanvas.saveSAMMData();		
		if(mSCanvas.getWidth() > mSCanvas.getHeight())
			mbCurrentScreenOrientationHorizontal = true;
		else
			mbCurrentScreenOrientationHorizontal = false;
		mbPlayAsEraserAnimation = true;		
		Intent intent = new Intent(this, SPen_Example_AnimationEraserViewer.class);
		intent.putExtra(SPen_Example_AnimationEraserViewer.EXTRA_VIEW_FILE_PATH, sDataKey);				
		intent.putExtra(SPen_Example_AnimationEraserViewer.EXTRA_CONTENTS_ORIENTATION, mbContentsOrientationHorizontal);
		intent.putExtra(SPen_Example_AnimationEraserViewer.EXTRA_VIEW_ORIENTATION, mbCurrentScreenOrientationHorizontal);
		intent.putExtra(SPen_Example_AnimationEraserViewer.EXTRA_IMAGE_OPERATION, mImageOperation);
		intent.putExtra(SPen_Example_AnimationEraserViewer.EXTRA_IMAGE_OPERATION_LEVEL, mImageOperationLevel);
		startActivityForResult(intent, REQUEST_CODE_PRIVIEW_BUTTON_CLICK);
	}




	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		menu.add(MENU_PLAY_ERASER, MENU_PLAY_ERASER, Menu.NONE, "Eraser Animation");
		menu.add(MENU_DATA_BG_SET_IMAGE, MENU_DATA_BG_SET_IMAGE, Menu.NONE, "BGImage Setting");
		menu.add(MENU_IMAGE_OPERATION, MENU_IMAGE_OPERATION, Menu.NONE, "Operation");
		menu.add(MENU_IMAGE_OPERATION_LEVEL, MENU_IMAGE_OPERATION_LEVEL, Menu.NONE, "Level");		
		menu.add(MENU_RESET, MENU_RESET, Menu.NONE, "Reset");

		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onMenuOpened(int featureId, Menu menu) 
	{
		super.onMenuOpened(featureId, menu);
		if (menu == null) 
			return true;

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		//================================================
		// Animation Menu
		//================================================
		case MENU_PLAY_ERASER:{
			previewAnimation(true);
		}
		break;

		case MENU_DATA_BG_SET_IMAGE:{
			// call gallery
			callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);
		}
		break;

		case MENU_RESET:
			resetClearImage(false, mImageOperation, mImageOperationLevel);
			break;
		case MENU_IMAGE_OPERATION:
			new AlertDialog.Builder(this)
			.setTitle("Image Operation")
			.setSingleChoiceItems(R.array.imageoperation_simple, mImageOperationIndex, new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {

					if(mImageOperation != imageOperationByIndex[which]){
						applyImageOperation(which);
					}

					dialog.dismiss();
				}
			})
			.show();			
			break;
		case MENU_IMAGE_OPERATION_LEVEL:	
			new AlertDialog.Builder(this)
			.setTitle("Image Operation Level")
			.setSingleChoiceItems(R.array.imageoperation_level, mImageOperationLevelIndex, new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					PreferencesOfEraserAnimationOption.setPreferenceImageOperationLevel(mContext, which);

					if(mImageOperationLevel != which){
						applyOperationLevel(which);
					}		
					dialog.dismiss();
				}
			})
			.show();
			break;	
		}			
		return true;
	}

	private void applyImageOperation(final int whichValue){
		new AlertDialog.Builder(this)
		.setTitle("Change Image Operation?")
		.setMessage("Drawing will be cleared.")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mImageOperationIndex = whichValue;
				mImageOperation = imageOperationByIndex[mImageOperationIndex];					
				resetClearImage(false, mImageOperation, mImageOperationLevel);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})		
		.show();	
	}

	private void applyOperationLevel(final int whichValue){
		new AlertDialog.Builder(this)
		.setTitle("Change Operation Level?")
		.setMessage("Drawing will be cleared.")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mImageOperationLevelIndex = whichValue;
				mImageOperationLevel = mImageOperationLevelIndex; // same as it is. 	
				resetClearImage(false, mImageOperation, mImageOperationLevel);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})		
		.show();	
	}

	//==================================================
	// Call Gallery
	//==================================================
	private void callGalleryForInputImage(int nRequestCode) {
		try {
			Intent galleryIntent;
			galleryIntent = new Intent(); 
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);				
			galleryIntent.setType("image/*");
			galleryIntent.setClassName("com.cooliris.media", "com.cooliris.media.Gallery");
			startActivityForResult(galleryIntent, nRequestCode);
		} catch(ActivityNotFoundException e) {
			Intent galleryIntent;
			galleryIntent = new Intent();
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
			galleryIntent.setType("image/*");
			startActivityForResult(galleryIntent, nRequestCode);
			e.printStackTrace();
		}		
	}

}
