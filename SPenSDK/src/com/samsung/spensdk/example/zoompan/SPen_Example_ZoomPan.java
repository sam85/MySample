package com.samsung.spensdk.example.zoompan;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.applistener.SCanvasMatrixChangeListener;
import com.samsung.spensdk.applistener.SCanvasModeChangedListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_ZoomPan extends Activity {
	
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
	
	private FrameLayout		mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mTextBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;
	private TextView		mZoomScale;
	
	private Button mUpBtn;
    private Button mDownBtn;
    private Button mLeftBtn;
    private Button mRightBtn;
    private Button mZoomInBtn;
    private Button mZoomOutBtn;
    
	private float mZoomValue = 1f;

	private final int MENU_MOVING = 1000;


	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.editor_zoompan);
	
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
		
		mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
		
		mUpBtn = (Button) findViewById(R.id.panUp);
        mUpBtn.setOnClickListener(arrowBtnClickListener);
        mDownBtn = (Button) findViewById(R.id.panDown);
        mDownBtn.setOnClickListener(arrowBtnClickListener);
        mLeftBtn = (Button) findViewById(R.id.panLeft);
        mLeftBtn.setOnClickListener(arrowBtnClickListener);
        mRightBtn = (Button) findViewById(R.id.panRight);
        mRightBtn.setOnClickListener(arrowBtnClickListener);

        mZoomInBtn = (Button) findViewById(R.id.zoomIn);
        mZoomInBtn.setOnClickListener(zoomBtnClickListener);
        mZoomOutBtn = (Button) findViewById(R.id.zoomOut);
        mZoomOutBtn.setOnClickListener(zoomBtnClickListener);
        mRightBtn.setOnClickListener(arrowBtnClickListener);
        mZoomScale = (TextView) findViewById(R.id.zoomScale);
		
		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);
		
		mSCanvas = new SCanvasView(mContext);
		mCanvasContainer.addView(mSCanvas);
		
		// Do not encode background/foreground image
		mSCanvas.setImageBufferEncodingOption(false, false);
		
		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, false);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, false);
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
				
				// Update button state
				updateModeState();
				
				// Init Background
				initBackground();
				
				float fZoomScale = mSCanvas.getCanvasZoomScale();
				Float f = fZoomScale;				
				mZoomScale.setText(f.toString());
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
		// Matrix Change Listener 
		//------------------------------------------------
		mSCanvas.setSCanvasMatrixChangeListener(new SCanvasMatrixChangeListener() {

			@Override
			public void onMatrixChanged(Matrix matrix) {
				float[] matrixValues = new float[9];
				matrix.getValues(matrixValues);
				mZoomValue = matrixValues[Matrix.MSCALE_X];
				
				// To display to the first decimal place
				float result = mZoomValue * 10f;
				result = Math.round(result);		
				Float f = result/10f;
								
				mZoomScale.setText(f.toString());	
				
				//float panningX = matrixValues[Matrix.MTRANS_X];
				//float panningY = matrixValues[Matrix.MTRANS_Y];
			}
	
			@Override
			public void onMatrixChangeFinished() {
				// TODO Auto-generated method stub
	
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
	    
	OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				if(mSCanvas.getCanvasMode()==SCanvasConstants.SCANVAS_MODE_INPUT_PEN){
					mSCanvas.setSettingViewSizeOption(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN, SCanvasConstants.SCANVAS_SETTINGVIEW_SIZE_NORMAL);
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
		}
	};
	
	
    
    private OnClickListener zoomBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mZoomInBtn)) {
                mSCanvas.setCanvasZoomScale(mZoomValue += 0.2, false);
                mSCanvas.setCanvasZoomScale(mZoomValue, true);
            } else if (v.equals(mZoomOutBtn)) {
                mSCanvas.setCanvasZoomScale(mZoomValue -= 0.2, false);
                mSCanvas.setCanvasZoomScale(mZoomValue, true);
            }
        }
    };

    private OnClickListener arrowBtnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(mUpBtn)) {
                mSCanvas.panBySCanvas(0, 30, false);
                mSCanvas.panBySCanvas(0, 0, true);
            }
            if (v.equals(mDownBtn)) {
                mSCanvas.panBySCanvas(0, -30, false);
                mSCanvas.panBySCanvas(0, 0, true);
            }
            if (v.equals(mLeftBtn)) {
                mSCanvas.panBySCanvas(30, 0, false);
                mSCanvas.panBySCanvas(0, 0, true);
            }
            if (v.equals(mRightBtn)) {
                mSCanvas.panBySCanvas(-30, 0, false);
                mSCanvas.panBySCanvas(0, 0, true);
            }
        }
    };

 // Update tool button
	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
		mTextBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_TEXT);
	}
	
	
    private void initBackground() {
        Bitmap bmBG = BitmapFactory.decodeResource(getResources(), R.drawable.smemo_bg);       
        if(bmBG == null)
        	return;       		
        mSCanvas.setBGImage(bmBG);      
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu){
    	if(mSCanvas.isMovingMode())
    		menu.add(MENU_MOVING, MENU_MOVING, Menu.NONE, "Moving Mode Off");
    	else
    		menu.add(MENU_MOVING, MENU_MOVING, Menu.NONE, "Moving Mode On");
		return super.onCreateOptionsMenu(menu);
	}
    
    @Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu == null) 
			return true;
		MenuItem menuItem = menu.findItem(MENU_MOVING);
		if(mSCanvas.isMovingMode())
			menuItem.setTitle("Moving Mode Off");
    	else
    		menuItem.setTitle("Moving Mode On");
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		if(mSCanvas == null)
			return false;

		switch(item.getItemId()) {
		case MENU_MOVING:
			mSCanvas.setMovingMode(!mSCanvas.isMovingMode());
			break;
		}
		return true;
	}	
}
