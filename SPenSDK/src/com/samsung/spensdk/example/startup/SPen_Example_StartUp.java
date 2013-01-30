package com.samsung.spensdk.example.startup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_StartUp extends Activity {
	
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
	
	private RelativeLayout	mCanvasContainer;
	private SCanvasView		mSCanvas;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.editor_startup);
	
		mContext = this;
		
		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mCanvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);		
		mSCanvas = new SCanvasView(mContext);        
		mCanvasContainer.addView(mSCanvas);
		
		//------------------------------------------------
		// Set SCanvas Initialize Listener
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
				
				// Set Background as white
				if(!mSCanvas.setBGColor(0xFFFFFFFF))
					Toast.makeText(mContext, "Fail to set Background color.", Toast.LENGTH_LONG).show();
			}
		});
		
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
}
