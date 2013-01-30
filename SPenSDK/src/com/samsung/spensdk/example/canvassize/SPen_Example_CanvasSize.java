package com.samsung.spensdk.example.canvassize;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
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


public class SPen_Example_CanvasSize extends Activity {
	
	private final String TAG = "SPenSDK Sample";
	private Context mContext = null;
	
	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	private final String APPLICATION_ID_NAME = "SDK Sample Application";
	private final int APPLICATION_ID_VERSION_MAJOR = 1;
	private final int APPLICATION_ID_VERSION_MINOR = 0;
	private final String APPLICATION_ID_VERSION_PATCHNAME = "Debug";
	
	private Bitmap 	mBGBitmap;
	private Rect	mSrcImageRect = null;
	
	private final int    CANVAS_HEIGHT_MARGIN = 160; // Top,Bottom margin  
	private final int    CANVAS_WIDTH_MARGIN = 50; // Left,Right margin

	private FrameLayout	mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;	
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.editor_canvas_size);
        mContext = this;
        
		//------------------------------------
		// UI Setting
		//------------------------------------
		mPenBtn = (ImageView) findViewById(R.id.penBtn);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.eraseBtn);
		mEraserBtn.setOnClickListener(mBtnClickListener);
		
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
		
		mBGBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.letter_bg_grass);
		if(mBGBitmap != null){
			mSrcImageRect = new Rect(0,0,mBGBitmap.getWidth(), mBGBitmap.getHeight());
		}
		
		setSCanvasViewLayout();
		// Set Background of layout container
		mLayoutContainer.setBackgroundResource(R.drawable.bg_edit);
		
		 
		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, false, false);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, false, false);
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
		SCanvasInitializeListener mSCanvasInitializeListener = new SCanvasInitializeListener() {
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
				
				// Update button state
				updateModeState();
				
				// Set Background Image
				if(!mSCanvas.setBGImage(mBGBitmap)){
					Toast.makeText(mContext, "Fail to set Background Image Bitmap.", Toast.LENGTH_LONG).show();
				}
			}
		};
		
		//------------------------------------------------
		// History Change
		//------------------------------------------------
		HistoryUpdateListener mHistoryUpdateListener = new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		};
		
		//------------------------------------------------
		// SCanvas Mode Changed Listener 
		//------------------------------------------------
		SCanvasModeChangedListener mModeChangedListener = new SCanvasModeChangedListener() {

			@Override
			public void onModeChanged(int mode) {
				updateModeState();				
			}
		};
		
		

		
		// Register Application Listener
		mSCanvas.setSCanvasInitializeListener(mSCanvasInitializeListener);
		mSCanvas.setHistoryUpdateListener(mHistoryUpdateListener);
		mSCanvas.setSCanvasModeChangedListener(mModeChangedListener);
		
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
	
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		setSCanvasViewLayout();
		super.onConfigurationChanged(newConfig);
	}
	

	void setSCanvasViewLayout(){
		Rect rectCanvas = getMaximumCanvasRect(mSrcImageRect, CANVAS_WIDTH_MARGIN, CANVAS_HEIGHT_MARGIN);
		int nCurWidth = rectCanvas.right-rectCanvas.left;
		int nCurHeight = rectCanvas.bottom-rectCanvas.top;
		// Place SCanvasView In the Center
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)mCanvasContainer.getLayoutParams();		
		layoutParams.width = nCurWidth;
		layoutParams.height= nCurHeight;
		layoutParams.gravity = Gravity.CENTER; 
		mCanvasContainer.setLayoutParams(layoutParams);
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
	
	
	    
	OnClickListener mBtnClickListener = new OnClickListener() {
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
		}
	};
	
	
	// Update tool button
	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
	}

		
	// Get the minimum image scaled rect which is fit to current screen 
	Rect getMaximumCanvasRect(Rect rectImage, int nMarginWidth, int nMarginHeight){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int nScreenWidth =  displayMetrics.widthPixels - nMarginWidth;
		int nScreenHeight = displayMetrics.heightPixels - nMarginHeight;

		int nImageWidth = rectImage.right - rectImage.left;
		int nImageHeight = rectImage.bottom - rectImage.top;			
		
		float fResizeWidth = (float) nScreenWidth / nImageWidth;
		float fResizeHeight = (float) nScreenHeight / nImageHeight;
		float fResizeRatio;
		
		// Fit to Height
		if(fResizeWidth>fResizeHeight){
			fResizeRatio = fResizeHeight;
		}
		// Fit to Width
		else {	
			fResizeRatio = fResizeWidth;
		}
		//return new Rect(0,0, (int)(nImageWidth*fResizeRatio), (int)(nImageHeight*fResizeRatio));
		// Adjust more detail
		int nResizeWidth = (int)(nImageWidth*fResizeRatio);
		int nResizeHeight = (int)(0.5 + (nResizeWidth * nImageHeight)/(float)nImageWidth);		
		return new Rect(0,0, nResizeWidth, nResizeHeight);
	}	
}
