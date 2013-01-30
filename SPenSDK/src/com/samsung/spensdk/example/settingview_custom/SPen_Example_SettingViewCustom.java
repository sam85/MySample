package com.samsung.spensdk.example.settingview_custom;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_SettingViewCustom extends Activity {
	
	private final String TAG = "SPenSDK Sample";
	
	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 2;
	private final int APPLICATION_ID_VERSION_MINOR = 2;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";
	
	
	//==============================
	// Variables
	//==============================
	Context mContext = null;
	
	private RelativeLayout	mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mFillingBtn;
	private ImageView		mColorPickerBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.editor_settingview_custom);
	
		mContext = this;
		
		//------------------------------------
		// UI Setting
		//------------------------------------
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mPenBtn.setOnLongClickListener(mBtnLongClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn.setOnLongClickListener(mBtnLongClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		mTextBtn.setOnLongClickListener(mBtnLongClickListener);
		mFillingBtn = (ImageView) findViewById(R.id.fillingBtn);
		mFillingBtn.setOnClickListener(mBtnClickListener);
		mFillingBtn.setOnLongClickListener(mBtnLongClickListener);
		mColorPickerBtn = (ImageView) findViewById(R.id.colorPickerBtn);
		mColorPickerBtn.setOnClickListener(mColorPickerListener);
		
		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
		
		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (RelativeLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);
		
		mSCanvas = new SCanvasView(mContext);        
		mCanvasContainer.addView(mSCanvas);
		
		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, true);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, true);
		// Set custom resource path in assets
		settingResourceMapString.put(SCanvasConstants.CUSTOM_RESOURCE_ASSETS_PATH, "spen_sdk_resource_custom");	// set folder of asstes/spen_sdk_resource_custom		
		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);
		
		//====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		//====================================================================================
		//------------------------------------------------
		// SCanvas Listener
		//------------------------------------------------
		mSCanvas.setSCanvasInitializeListener(new SCanvasInitializeListener() {
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
				
				// Set to use custom ClearAll
				mSCanvas.setClearAllByListener(true);
				
				// Update button state
				mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
				mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
				mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
				updateModeState();
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

		//------------------------------------------------
		// Color Picker Listener 
		//------------------------------------------------
		mSCanvas.setColorPickerColorChangeListener(new ColorPickerColorChangeListener(){
			@Override
			public void onColorPickerColorChanged(int nColor) {

				int nCurMode = mSCanvas.getCanvasMode();
				if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN) {
					SettingStrokeInfo strokeInfo = mSCanvas.getSettingViewStrokeInfo();
					if(strokeInfo != null) {
						strokeInfo.setStrokeColor(nColor);	
						mSCanvas.setSettingViewStrokeInfo(strokeInfo);
					}	
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER) {
					// do nothing
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					SettingTextInfo textInfo = mSCanvas.getSettingViewTextInfo();
					if(textInfo != null) {
						textInfo.setTextColor(nColor);
						mSCanvas.setSettingViewTextInfo(textInfo);
					}
				}
				else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING) {
					SettingFillingInfo fillingInfo = mSCanvas.getSettingViewFillingInfo();
					if(fillingInfo != null) {
						fillingInfo.setFillingColor(nColor);
						mSCanvas.setSettingViewFillingInfo(fillingInfo);
					}
				}	
			}			
		});	


		//------------------------------------------------
		// SettingStrokeChangeListener Listener 
		//------------------------------------------------				
		mSCanvas.setSettingStrokeChangeListener(new SettingStrokeChangeListener() {

			@Override
			public void onClearAll(boolean bClearAllCompleted) {				
				setCustomClearAll();
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
		});



		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);
		
		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}
	
	
	private void setCustomClearAll(){
		AlertDialog.Builder ad = new AlertDialog.Builder(mContext);		
		ad.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
		ad.setTitle(getResources().getString(R.string.clear_all))		
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// finish dialog
				dialog.dismiss();		
				// Can not Undo
				mSCanvas.clearScreen(false);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// finish dialog
				dialog.dismiss();						
			}
		})
		.show();		
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
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		updateSettingViewPosition();		
	}
	
	
	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				 mSCanvas.undo();
			}
			else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};
	    
	// color picker mode
	private OnClickListener mColorPickerListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mColorPickerBtn)) {
				// Toggle
				boolean bIsColorPickerMode = !mSCanvas.isColorPickerMode(); 
				mSCanvas.setColorPickerMode(bIsColorPickerMode);
				mColorPickerBtn.setSelected(bIsColorPickerMode);
			}
		}
	};
	
	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			int nSettingViewSizeOption = SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT;
			if(nBtnID == mPenBtn.getId()){				
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, nSettingViewSizeOption, true);
			}
			else if(nBtnID == mEraserBtn.getId()){
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, nSettingViewSizeOption, true);
			}
			else if(nBtnID == mTextBtn.getId()){
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, nSettingViewSizeOption, true);
			}
			else if(nBtnID == mFillingBtn.getId()){
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, nSettingViewSizeOption, true);
			}
		}
	};
	
	
	private OnLongClickListener mBtnLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			
			int nBtnID = v.getId();			
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view.
			int nSettingViewSizeOption = SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_MINI;
			if(nBtnID == mPenBtn.getId()){				
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, nSettingViewSizeOption, false);
				return true;
			}
			else if(nBtnID == mEraserBtn.getId()){
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, nSettingViewSizeOption, false);
				return true;
			}
			else if(nBtnID == mTextBtn.getId()){
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, nSettingViewSizeOption, false);
				return true;
			}
			else if(nBtnID == mFillingBtn.getId()){
				showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, nSettingViewSizeOption, false);
				return true;
			}
			
			return false;
		}
	};
	
	private void updateSettingViewPosition(){
		int nCurMode = mSCanvas.getCanvasMode();
		if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN && mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)){
			moveSettingViewLayout(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
		}
		else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER && mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)){
			moveSettingViewLayout(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
		}
		else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT && mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT)){
			moveSettingViewLayout(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
		}
		else if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING && mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING)){
			moveSettingViewLayout(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
		}		
	}
	
	
	private void showSettingView(int whichSettingView, int nSettingViewSizeOption, boolean bToggleView){
		int nCurMode = mSCanvas.getCanvasMode();
		// Show by Toggle
	
		boolean bToggle;
		if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN && whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_PEN
		|| nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER && whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER
		|| nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT && whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT
		|| nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING && whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING)
			bToggle = true;
		else
			bToggle = false;
	
		if(bToggle){
			mSCanvas.setSettingViewSizeOption(whichSettingView, nSettingViewSizeOption);
			if(mSCanvas.isSettingViewVisible(whichSettingView)){
				if(bToggleView){
				mSCanvas.showSettingView(whichSettingView, false);
			}
			else{
					mSCanvas.showSettingView(whichSettingView, false);
					mSCanvas.showSettingView(whichSettingView, true);
				}
			}
			else{
				moveSettingViewLayout(whichSettingView);
				mSCanvas.showSettingView(whichSettingView, true);
			}
		}
		else{
			int nChangeMode;
			if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_PEN) nChangeMode = SCanvasConstants.SCANVAS_MODE_INPUT_PEN;
			else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER) nChangeMode = SCanvasConstants.SCANVAS_MODE_INPUT_ERASER;
			else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT) nChangeMode = SCanvasConstants.SCANVAS_MODE_INPUT_TEXT;
			else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING) nChangeMode = SCanvasConstants.SCANVAS_MODE_INPUT_FILLING;
			else nChangeMode = nCurMode;
					
			mSCanvas.setCanvasMode(nChangeMode);
			if(bToggleView){
				mSCanvas.showSettingView(whichSettingView, false);										
			}
			else{
				mSCanvas.setSettingViewSizeOption(whichSettingView, nSettingViewSizeOption);
				moveSettingViewLayout(whichSettingView);
				mSCanvas.showSettingView(whichSettingView, true);
			}
			updateModeState();
			
			if(nChangeMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT)
				Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
			else if(nChangeMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING)
				Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
		}
	}
		
	private void moveSettingViewLayout(int whichSettingView){
		
		//---------------------------------------------------
		// Get the Setting View and Container Size
		//---------------------------------------------------
		int nSettingViewWidth = 500;
		int nSettingViewLeft, nSettingViewRight;
		
		//---------------------------------------------------
		// Get the Setting View Center Position(Anchor)
		//---------------------------------------------------
		Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int nDisplayWidth = display.getWidth();
//		int nDisplayHeight = display.getHeight();
		int nLayoutContainerSize = nDisplayWidth;
		int nBtnNum = 7;
		int nBtnWidth = nLayoutContainerSize / nBtnNum;
		
		int nSettingViewCenterPosition;
		if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_PEN){
			nSettingViewCenterPosition = nBtnWidth/2;
		}
		else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER){
			nSettingViewCenterPosition = nBtnWidth + nBtnWidth/2;			
		}
		else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT){
			nSettingViewCenterPosition = nBtnWidth*2 + nBtnWidth/2;
		}
		else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING){
			nSettingViewCenterPosition = nBtnWidth*3 + nBtnWidth/2;			
		}
		else
			return;
		
//		//---------------------------------------------------
//		// Get the Setting View Center Position(Anchor) : Error
//		//---------------------------------------------------
//		View viewAbove;
//		if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_PEN){
//			viewAbove = mPenBtn;
//		}
//		else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER){
//			viewAbove = mEraserBtn;			
//		}
//		else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT){
//			viewAbove = mTextBtn;
//		}
//		else if(whichSettingView==SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING){
//			viewAbove = mFillingBtn;			
//		}
//		else
//			return;
//		
//		nSettingViewLeft = viewAbove.getLeft();
//		nSettingViewRight = viewAbove.getRight();
//		nSettingViewCenterPosition = (nSettingViewLeft+nSettingViewRight)/2;
//		nLayoutContainerSize = mLayoutContainer.getRight() - mLayoutContainer.getLeft();		
//		Log.e(TAG, "nLeft="+nSettingViewLeft + " nRight="+nSettingViewRight +"nLayoutSize="+nLayoutContainerSize);
		
		//---------------------------------------------------
		// Adjust the Setting View Position
		//---------------------------------------------------
		nSettingViewLeft = nSettingViewCenterPosition - nSettingViewWidth/2;
		nSettingViewRight= nSettingViewLeft + nSettingViewWidth;
		if(nSettingViewRight>nLayoutContainerSize){
			nSettingViewRight = nLayoutContainerSize;
			nSettingViewLeft = nSettingViewRight - nSettingViewWidth;			
		}
		if(nSettingViewLeft<0){
			nSettingViewLeft = 0;
//			nSettingViewRight = nSettingViewLeft + nSettingViewWidth; // don't need anymore
		}
		//---------------------------------------------------
		// Change the Setting View Layout
		//---------------------------------------------------
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mLayoutContainer.getLayoutParams();
		lp.leftMargin = nSettingViewLeft;
		mLayoutContainer.setLayoutParams(lp);
	}
	
	
	// Update tool button
	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
		mTextBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
		mFillingBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
		
		// Reset color picker tool when Eraser Mode 
		if(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER)
			mSCanvas.setColorPickerMode(false);
		mColorPickerBtn.setEnabled(nCurMode!=SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
		mColorPickerBtn.setSelected(mSCanvas.isColorPickerMode());
	}
}
