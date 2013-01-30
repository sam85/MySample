package com.samsung.spensdk.example.eraseranimation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SAMMLibConstants;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.AnimationProcessListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.PreferencesOfAnimationOption;

public class SPen_Example_AnimationEraserViewer extends Activity {

	private final String TAG = "SPenSDK Sample";

	static public final String EXTRA_VIEW_FILE_PATH = "ExtraViewFilePath";
	static public final String EXTRA_CONTENTS_ORIENTATION = "ExtraContentsOrientation";
	static public final String EXTRA_VIEW_ORIENTATION = "ExtraViewOrientation";
	static public final String EXTRA_IMAGE_OPERATION = "ExtraImageOperation";
	static public final String EXTRA_IMAGE_OPERATION_LEVEL = "ExtraImageOperationLevel";
	static public final String EXTRA_PLAY_BUTTON_CLICK = "ExtraPlayButtonClick";


	private final int MENU_PLAYPAUSE = Menu.FIRST + 0;
	private final int MENU_STOP = Menu.FIRST + 1;
	private final int MENU_SPEED = Menu.FIRST + 2;

	private SCanvasView		mSCanvas;

	Context mContext = null;
	
	private String sDataKey = null;
	int nImageOperation;
	int nImageOperationLevel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createAnimationLayoutUI();
	}

	
	private void createAnimationLayoutUI() {
		setContentView(R.layout.example_viewer_eraser);
		mContext = this;

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);

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
				// Set as animation mode  
				mSCanvas.setAnimationMode(true);

				// Set the animation mode as Dimming eraser mode just like erasing frost on the glass by finger. 
				mSCanvas.setEraserAnimationMode(true);

				// Set cursor invisible
				mSCanvas.setEraserCursorVisible(false);

				// Get the file path by intent
				Intent intent = getIntent();
				sDataKey = intent.getStringExtra(EXTRA_VIEW_FILE_PATH);			
				// SAMMData  Load by Key
				if(mSCanvas.loadSAMMData(sDataKey)){

					// Set Clear Image Bitmap
					// It is better to use the bitmap whose size is same as those of canvas.
					// If it is different, setClearImageBitmap() resize the bitmap inside.
					String strBackgroundImagePath = mSCanvas.getBGImagePathDecoded();
					Bitmap bmDim = BitmapFactory.decodeFile(strBackgroundImagePath);
					if(bmDim == null)
						return;

					nImageOperation = intent.getIntExtra(EXTRA_IMAGE_OPERATION, SPenImageFilterConstants.FILTER_DARK);
					// range of level : 0~4	
					nImageOperationLevel = intent.getIntExtra(EXTRA_IMAGE_OPERATION_LEVEL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);

					mSCanvas.setClearImageBitmap(bmDim, nImageOperation, nImageOperationLevel);

					// Set play option					
					SOptionSCanvas canvasOption = new SOptionSCanvas();		
					
					// Whether set transparent background or not					
					// Set false(default value), because transparent background will be displayed as black color. 
					//canvasOption.mPlayOption.setInvisibleBGImageAnimationOption(PreferencesOfAnimationOption.getPreferencePlayAnimationUsingTransparentBackground(mContext));
					
					// Set Background audio play option
					canvasOption.mPlayOption.setPlayBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudio(mContext));
					
					// Set Background audio play repeat option
					canvasOption.mPlayOption.setRepeatBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudioReplay(mContext));
					
					// When stop animation play, whether set Background audio play stop or not
					canvasOption.mPlayOption.setStopBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudioStop(mContext));
					
					// When object drawing, whether set sound effect or not  : Don't play back sound effect
					canvasOption.mPlayOption.setSoundEffectOption(false);

					// Set Background audio software volume
					if(!canvasOption.mPlayOption.setBGAudioVolume(1.0f))
						return;		

					// Set animation play speed
					if(!canvasOption.mPlayOption.setAnimationSpeed(PreferencesOfAnimationOption.getPreferencePlayAnimationSpeed(mContext)))	
						return;	
					// Set Option
					if(!mSCanvas.setOption(canvasOption))
						return;

					// Start animation
					mSCanvas.doAnimationStart();
				}
			}
		};

		// Animation Processing Callback  
		AnimationProcessListener animationProcessListener = new AnimationProcessListener()	{
			@Override
			public void onPlayComplete() {
				Toast.makeText(SPen_Example_AnimationEraserViewer.this, "Play Complete.", Toast.LENGTH_SHORT).show();
				// Close menu after play complete
				closeOptionsMenu();  
			}

			@Override
			public void onChangeProgress(int nProgress) {
				// TODO Auto-generated method stub

			}
		};


		// Set the listener to execute when the animation completed.
		mSCanvas.setAnimationProcessListener(animationProcessListener);
		// Set the initialization finish listener
		mSCanvas.setSCanvasInitializeListener(scanvasInitializeListener);

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener    
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// close previous animation when device is rotated
		if(!mSCanvas.doAnimationClose())
			Log.e(TAG, "Fail to doAnimationClose");		

		// Set as animation mode  
		mSCanvas.setAnimationMode(true);

		// Set the animation mode as Dimming eraser mode just like erasing frost on the glass by finger. 
		mSCanvas.setEraserAnimationMode(true);

		// Set cursor invisible
		mSCanvas.setEraserCursorVisible(false);

		// SAMMData  Load by Key
		if(mSCanvas.loadSAMMData(sDataKey)){
			// Set delay for initializing the canvas before animation start
			mSCanvas.postDelayed(new Runnable() {				
				@Override
				public void run() {
					// Start animation
					mSCanvas.doAnimationStart();
				}
			}, 300);
		}
		
		closeOptionsMenu();

		super.onConfigurationChanged(newConfig);
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

		getIntent().putExtra(EXTRA_PLAY_BUTTON_CLICK, false);			
		setResult(RESULT_OK, getIntent());

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		menu.add(MENU_PLAYPAUSE, MENU_PLAYPAUSE, Menu.NONE, "Play");
		menu.add(MENU_STOP, MENU_STOP, Menu.NONE, "Stop");
		menu.add(MENU_SPEED, MENU_SPEED, Menu.NONE, "Speed");

		return super.onCreateOptionsMenu(menu);
	} 

	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu != null){
			int nAnimationState = mSCanvas.getAnimationState();
			boolean bAnimationMode = mSCanvas.isAnimationMode();
			if(bAnimationMode){
				if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_OFF_ANIMATION)
					menu.findItem(MENU_PLAYPAUSE).setTitle("Play");
				else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_STOP)
					menu.findItem(MENU_PLAYPAUSE).setTitle("Play");
				else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_PAUSED)
					menu.findItem(MENU_PLAYPAUSE).setTitle("Resume");
				else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_RUNNING)
					menu.findItem(MENU_PLAYPAUSE).setTitle("Pause");
			}			
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item)	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		case MENU_PLAYPAUSE:
			if(!mSCanvas.isAnimationMode())
				break;
			animationPlayOrPause();

			break;
		case MENU_STOP:
			if(!mSCanvas.isAnimationMode())
				break;				
			mSCanvas.doAnimationStop(true);
			break;
		case MENU_SPEED:
			animationSpeedDialog();
			break;
		}
		return true;
	}


	void initBackground(){
		RelativeLayout canvasContainer = (RelativeLayout) findViewById(R.id.canvas_container);
		if(canvasContainer==null) return;

		// transparent
		canvasContainer.setBackgroundColor(0);	
	}

	// Load file to animation
	boolean loadAnimationFile(String strFileName){
		// initialize background
		initBackground();

		if(!mSCanvas.loadSAMMFile(strFileName, false)){
			Toast.makeText(this, "Load AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	// Start or Pause Animation 
	void animationPlayOrPause(){
		int nAnimationState = mSCanvas.getAnimationState();
		if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_STOP)
		{
			// Start Animation
			mSCanvas.doAnimationStart();
		}
		else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_PAUSED)
			mSCanvas.doAnimationResume();
		else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_RUNNING)
			mSCanvas.doAnimationPause();
	}

	// Show Animation Speed Dialog
	void animationSpeedDialog(){
		int nAnimationSpeed = mSCanvas.getAnimationSpeed();
		new AlertDialog.Builder(this)
		.setTitle(R.string.animation_speed_title)
		.setSingleChoiceItems(R.array.animation_speed, nAnimationSpeed, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mSCanvas.setAnimationSpeed(which);		
				PreferencesOfAnimationOption.setPreferencePlayAnimationSpeed(mContext, which);
				dialog.dismiss();
			}
		})
		.show();
	}

}
