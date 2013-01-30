package com.samsung.spensdk.example.hoverpointer;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spen.settings.SettingFillingInfo;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spen.settings.SettingTextInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.ColorPickerColorChangeListener;
import com.samsung.spensdk.applistener.CustomHoverPointerSettingListener;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_HoverPointer extends Activity {
	
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
	// Menu
	//==============================
	private final int MENU_HOVER_STYLE_GROUP = 1000;
	private final int MENU_HOVER_DEFAULT = 1001;
	private final int MENU_HOVER_SIMPLE_ICON = 1002;
	private final int MENU_HOVER_SIMPLE_DRAWABLE = 1003;
	private final int MENU_HOVER_SPENSDK = 1004;
	private final int MENU_HOVER_SNOTE = 1005;
	
	
	private final int MENU_SIDEBUTTON_GROUP = 3000;
	private final int MENU_SIDEBUTTON_CHANGE_PEN= 3001;
	private final int MENU_SIDEBUTTON_SHOW_SETTING = 3002;	
		
	
	private int mHoverButtonAction = 0; // 0:Change Setting :Show SettingView
	
	//==============================
	// Variables
	//==============================
	Context mContext = null;
	
	private FrameLayout		mLayoutContainer;
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
	
		setContentView(R.layout.editor_hoverpointer);
	
		mContext = this;
		
		//------------------------------------
		// UI Setting
		//------------------------------------
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		mTextBtn = (ImageView) findViewById(R.id.textBtn);
		mTextBtn.setOnClickListener(mBtnClickListener);
		mFillingBtn = (ImageView) findViewById(R.id.fillingBtn);
		mFillingBtn.setOnClickListener(mBtnClickListener);
		mColorPickerBtn = (ImageView) findViewById(R.id.colorPickerBtn);
		mColorPickerBtn.setOnClickListener(mColorPickerListener);
	
		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
		
		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
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
				
				// Set Initial Pen setting
				SettingStrokeInfo settingInfo = mSCanvas.getSettingViewStrokeInfo();
				if(settingInfo!=null) {
					settingInfo.setStrokeWidth(20);
					settingInfo.setStrokeColor(Color.RED);		
					mSCanvas.setSettingViewStrokeInfo(settingInfo);	
				}
				Toast.makeText(mContext, "Set the initial color of the pen as RED to show red hover pointer", Toast.LENGTH_SHORT).show();
				
				// Set Hover pointer as S Pen SDK Style
				mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
				
				// Update button state
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
		// Custom Hover Pointer Listener 
		//------------------------------------------------
		// You can change each hover pointer as your taste.
		mSCanvas.setCustomHoverPointerListener(new CustomHoverPointerSettingListener() {

			@Override
			public Drawable onHoverPointerForStroke(SettingStrokeInfo strokeInfo, Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerForText(SettingTextInfo textInfo, Drawable defaultDrawable) {
				return defaultDrawable;
			}
			
			@Override
			public Drawable onHoverPointerForFilling(SettingFillingInfo fillingInfo, Drawable defaultDrawable) {
				return defaultDrawable;
			}
			
			@Override
			public Drawable onHoverPointerForPicker(Drawable defaultDrawable) {
				return defaultDrawable;
			}

			@Override
			public Drawable onHoverPointerDefault(Drawable defaultDrawable) {
				return defaultDrawable;
			}
		});
		
		//--------------------------------------------
		// [Hover Listener & Custom Hover Icon]
		// Set S pen HoverListener & Custom Hover Icon
		//--------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener(){
			@Override
			public boolean onHover(View view, MotionEvent event) {				
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
				//Log.e(TAG, "HOVER_TEST(UI): Down" );
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				//Log.e(TAG, "HOVER_TEST(UI): UP" );
				// Change Setting
				if(mHoverButtonAction==0){
					int nPreviousMode = mSCanvas.getCanvasMode(); 
					
					boolean bIncludeDefinedSetting = true;
					boolean bIncludeCustomSetting = true;
					boolean bIncludeEraserSetting = true;
					SettingStrokeInfo settingInfo = mSCanvas.getNextSettingViewStrokeInfo(bIncludeDefinedSetting, bIncludeCustomSetting, bIncludeEraserSetting);
					if(settingInfo!=null) {
						if(mSCanvas.setSettingViewStrokeInfo(settingInfo)) {
							// Mode Change : Pen => Eraser					
							if(nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_PEN
							&& settingInfo.getStrokeStyle()==SObjectStroke.SAMM_STROKE_STYLE_ERASER){
								// Change Mode
								mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
								// Show Setting View
								if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN)){
									mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, false);
									mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);							
									mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, true);
								}
								updateModeState();
							}
							// Mode Change : Eraser => Pen 
							if(nPreviousMode == SCanvasConstants.SCANVAS_MODE_INPUT_ERASER
							&& settingInfo.getStrokeStyle()!=SObjectStroke.SAMM_STROKE_STYLE_ERASER){
								// Change Mode
								mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
								// Show Setting View
								if(mSCanvas.isSettingViewVisible(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER)){
									mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
									mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);							
									mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, true);
								}
								updateModeState();
							}							
						}						
					}
				}
				// Show SettingView(Toggle SettingView)
				else if(mHoverButtonAction==1){
					if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
					}
					else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
					}
					else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);						
					}
					else if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
						mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
					}
				}
			}
		});
		
		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);
		
		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
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
			}
			else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};
	    
	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_EXT);
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
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER);
				}
				else {
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_ERASER, false);
					updateModeState();
				}
			}
			else if(nBtnID == mTextBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_TEXT, false);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to insert Text", Toast.LENGTH_SHORT).show();
				}
			}
			else if(nBtnID == mFillingBtn.getId()){
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_FILLING){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
					mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING);
				}
				else{
					mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_FILLING);
					mSCanvas.showSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_FILLING, false);										
					updateModeState();
					Toast.makeText(mContext, "Tap Canvas to fill color", Toast.LENGTH_SHORT).show();
				}
			}
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){	
		
		SubMenu hoverStyleMenu = menu.addSubMenu("Hover Pointer Style");
		hoverStyleMenu.add(MENU_HOVER_STYLE_GROUP, MENU_HOVER_DEFAULT, 1, "Default(none)");
		hoverStyleMenu.add(MENU_HOVER_STYLE_GROUP, MENU_HOVER_SIMPLE_ICON, 2, "Simple Icon");
		hoverStyleMenu.add(MENU_HOVER_STYLE_GROUP, MENU_HOVER_SIMPLE_DRAWABLE, 3, "Simple Drawable");
		hoverStyleMenu.add(MENU_HOVER_STYLE_GROUP, MENU_HOVER_SPENSDK, 4, "S Pen SDK Style");
		hoverStyleMenu.add(MENU_HOVER_STYLE_GROUP, MENU_HOVER_SNOTE, 5, "S-Note Style");
		
		SubMenu buttonMenu = menu.addSubMenu("Pen Side Button");
		buttonMenu.add(MENU_SIDEBUTTON_GROUP, MENU_SIDEBUTTON_CHANGE_PEN, 1, "Change Setting");
		buttonMenu.add(MENU_SIDEBUTTON_GROUP, MENU_SIDEBUTTON_SHOW_SETTING, 2, "Show Setting View");
		
		
		return super.onCreateOptionsMenu(menu);
	} 
		

	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()) {
		case MENU_HOVER_DEFAULT:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);
			break;
		case MENU_HOVER_SIMPLE_ICON:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleIcon(SPenEventLibrary.HOVERING_SPENICON_MOVE);			
			break;
		case MENU_HOVER_SIMPLE_DRAWABLE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SIMPLE_CUSTOM);
			mSCanvas.setSCanvasHoverPointerSimpleDrawable(getResources().getDrawable(R.drawable.tool_ic_pen));
			break;
		case MENU_HOVER_SPENSDK:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SPENSDK);
			break;
		case MENU_HOVER_SNOTE:
			mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_SNOTE);
			break;
		case MENU_SIDEBUTTON_CHANGE_PEN:
			mHoverButtonAction = 0;
			break;
		case MENU_SIDEBUTTON_SHOW_SETTING:
			mHoverButtonAction = 1;
			break;
		}
		return true;
	}
}
