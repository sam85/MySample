package com.samsung.spensdk.example.sammeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.samsung.samm.common.SAMMLibConstants;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;


public class SPen_Example_VoiceRecorder extends Activity {

	private final String TAG = "SPenSDK Sample";

	static public final String EXTRA_VIEW_FILE_PATH = "ExtraViewFilePath";
	static public final String EXTRA_CONTENTS_ORIENTATION = "ExtraContentsOrientation";
	static public final String EXTRA_VIEW_ORIENTATION = "ExtraViewOrientation";

	private final int MENU_START_VOICERECORD = Menu.FIRST + 1;
	private final int MENU_COMPLETE_VOICERECORD = Menu.FIRST + 2;
	private final int MENU_CANCEL_VOICERECORD = Menu.FIRST + 3;

	private String sDataKey = null;
	private SCanvasView		mSCanvas;

	Context mContext = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		boolean bContentsOrientationHorizontal = intent.getBooleanExtra(EXTRA_CONTENTS_ORIENTATION, false);
		boolean bScreenOrientationHorizontal = intent.getBooleanExtra(EXTRA_VIEW_ORIENTATION, false);

		if(!bContentsOrientationHorizontal && !bScreenOrientationHorizontal) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.viewer_vv);        	
		}
		else if(!bContentsOrientationHorizontal && bScreenOrientationHorizontal) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.viewer_vh);        	
		}
		else if(bContentsOrientationHorizontal && !bScreenOrientationHorizontal) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.viewer_hv);        	
		}
		else if(bContentsOrientationHorizontal && bScreenOrientationHorizontal) {
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			setContentView(R.layout.viewer_hh);        	
		}
		mContext = this;

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);      
		mSCanvas.setHistoricalOperationSupport(false);

		// Hide the progress bar at Voice Recorder.
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);


		//====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		//====================================================================================
		SCanvasInitializeListener scanvasInitializeListener = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {

				//--------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				//--------------------------------------------
				// Change to the animation mode  
				// Set as animation mode  
				mSCanvas.setAnimationMode(true);
			}
		};
		// Set the initialization finish listener
		mSCanvas.setSCanvasInitializeListener(scanvasInitializeListener);

		// Get the file path by intent
		sDataKey = intent.getStringExtra(EXTRA_VIEW_FILE_PATH);				
		Toast.makeText(this, "To start record voice, select menu - start" + '\n' + '\n' +
				"After start record voice, " + '\n' +
				"to save current recorded voice, select menu - complete" + '\n' +
				"to cancel current recored voice, select menu - cancel", Toast.LENGTH_LONG).show();    			

		// Do NOT load file or start animation here because we don't know viewer size.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();

		if(!mSCanvas.doAnimationClose())
			Log.e(TAG, "Fail to doAnimationClose");		
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");		
	}

	@Override
	public void onBackPressed() {
		if(!mSCanvas.doAnimationClose())
			Log.e(TAG, "Fail to doAnimationClose");
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		menu.add(MENU_START_VOICERECORD, MENU_START_VOICERECORD, Menu.NONE, "Start");
		menu.add(MENU_COMPLETE_VOICERECORD, MENU_COMPLETE_VOICERECORD, Menu.NONE, "Complete");
		menu.add(MENU_CANCEL_VOICERECORD, MENU_CANCEL_VOICERECORD, Menu.NONE, "Cancel");

		return super.onCreateOptionsMenu(menu);
	} 

	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu != null){	
			if(mSCanvas.isVoiceRecording())	
			{				
				menu.findItem(MENU_COMPLETE_VOICERECORD).setTitle("Complete");				
				menu.findItem(MENU_CANCEL_VOICERECORD).setTitle("Cancel");
				menu.findItem(MENU_COMPLETE_VOICERECORD).setVisible(true);
				menu.findItem(MENU_CANCEL_VOICERECORD).setVisible(true);
				menu.findItem(MENU_START_VOICERECORD).setVisible(false);
			}			
			else 
			{				
				menu.findItem(MENU_START_VOICERECORD).setTitle("Start");
				menu.findItem(MENU_START_VOICERECORD).setVisible(true);
				menu.findItem(MENU_COMPLETE_VOICERECORD).setVisible(false);
				menu.findItem(MENU_CANCEL_VOICERECORD).setVisible(false);

			}
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		case MENU_START_VOICERECORD:
			// if voice recording, return;
			if(mSCanvas.isVoiceRecording())
				return false;

			// Loading temporary save file of SAMMData  
			if(mSCanvas.loadSAMMData(sDataKey)){
				// Start voiceRecord
				mSCanvas.recordVoiceStart();

				// Set play option						
				SOptionSCanvas canvasOption = new SOptionSCanvas();	

				// During recording voice, don't playback Background audio	
				canvasOption.mPlayOption.setPlayBGAudioOption(false);
				// During recording voice, don't playback Sound Effect	
				canvasOption.mPlayOption.setSoundEffectOption(false);

				// Rest Option : Preference
				if(!mSCanvas.setOption(canvasOption))
					return false;

				// Start animation 
				mSCanvas.doAnimationStart();	
			}
			break;
		case MENU_COMPLETE_VOICERECORD:
			if(mSCanvas.isVoiceRecording())
			{
				mSCanvas.recordVoiceComplete();				
				getIntent().putExtra("VOICERECORD", true);			
				setResult(RESULT_OK, getIntent());

				finish();
			}
			break;
		case MENU_CANCEL_VOICERECORD:	
			// Cancel voiceRecord
			mSCanvas.recordVoiceCancel();				
			break;		
		}
		return true;
	}

	// Start or pause animation 
	void animationPlayOrPause(){
		int nAnimationState = mSCanvas.getAnimationState();

		if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_STOP)
		{
			// Start animation			
			mSCanvas.doAnimationStart();	
		}
		else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_PAUSED)
			mSCanvas.doAnimationResume();
		else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_RUNNING)
			mSCanvas.doAnimationPause();
	}	
}
