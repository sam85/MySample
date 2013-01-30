package com.samsung.spensdk.example.sammeditor;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.samsung.samm.common.SAMMLibConstants;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.AnimationProcessListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.PreferencesOfAnimationOption;
import com.samsung.spensdk.example.tools.ToolListActivity;


public class SPen_Example_AnimationViewer extends Activity {
	
	private final String TAG = "SPenSDK Sample";
	
	static public final String EXTRA_VIEW_FILE_PATH = "ExtraViewFilePath";
	static public final String EXTRA_CONTENTS_ORIENTATION = "ExtraContentsOrientation";
	static public final String EXTRA_VIEW_ORIENTATION = "ExtraViewOrientation";
	static public final String EXTRA_PLAY_BUTTON_CLICK = "ExtraPlayButtonClick";	
	
	private final int MENU_PLAYPAUSE = Menu.FIRST + 1;
	private final int MENU_STOP = Menu.FIRST + 2;
	private final int MENU_LOAD_SELECT = Menu.FIRST + 3;
	private final int MENU_ANIMATION = Menu.FIRST + 4;
	
	private final int REQUEST_CODE_FILE_SELECT = 100;
	private final int REQUEST_CODE_PLAY_OPTION = 101;
	
	private String  mTempAMSFolderPath = null;
	private final String DEFAULT_SAVE_PATH = "SPenSDK2.2";	
	
	private SCanvasView		mSCanvas;
	private ProgressBar 	mProgress;

	Context mContext = null;

	private boolean mIsRotation = false;	
	private boolean mIsLoadInViewer = false;
	private String mStrFileName = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createAnimationLayoutUI(mIsRotation);

		// Make basic folder and file name to load or save 
		File sdcard_path = Environment.getExternalStorageDirectory();
		File spen_path =  new File(sdcard_path, DEFAULT_SAVE_PATH);
		if(!spen_path.exists()){
			if(!spen_path.mkdirs()){
				Log.e(TAG, "Default Save Path Creation Error");
				return ;
			}
		}
		mTempAMSFolderPath = spen_path.getAbsolutePath();

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}
	
	private void createAnimationLayoutUI(boolean bRotation) {

		Intent intent = getIntent();
		boolean bContentsOrientationHorizontal = intent.getBooleanExtra(EXTRA_CONTENTS_ORIENTATION, false);
		boolean bScreenOrientationHorizontal = intent.getBooleanExtra(EXTRA_VIEW_ORIENTATION, false);        

		if(!bContentsOrientationHorizontal && !bScreenOrientationHorizontal) {
			if(!bRotation)
				setContentView(R.layout.viewer_vv);
			else
				setContentView(R.layout.viewer_vh);
		}
		else if(!bContentsOrientationHorizontal && bScreenOrientationHorizontal) {
			if(!bRotation)
				setContentView(R.layout.viewer_vh);
			else
				setContentView(R.layout.viewer_vv);
		}
		else if(bContentsOrientationHorizontal && !bScreenOrientationHorizontal) {
			if(!bRotation)
				setContentView(R.layout.viewer_hv);
			else
				setContentView(R.layout.viewer_hh);
		}
		else if(bContentsOrientationHorizontal && bScreenOrientationHorizontal) {
			if(!bRotation)
				setContentView(R.layout.viewer_hh);
			else
				setContentView(R.layout.viewer_hv);
		}
		mContext = this;       

		// Set progress
		mProgress = (ProgressBar) findViewById(R.id.progressBar);
		mProgress.setMax(100);
		mProgress.setVisibility(View.VISIBLE);

		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);
	        mSCanvas.setHistoricalOperationSupport(false);

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
				
				if(mIsLoadInViewer) {
					if(loadAnimationFile(mStrFileName))
					{
						//Set Animation Option
						SOptionSCanvas canvasOption = new SOptionSCanvas();		
						setPlayOption(canvasOption);						

						// Start animation
						mSCanvas.doAnimationStart();	
					}
					
				}
				else {

					// Get the file path by intent
					Intent intent = getIntent();
					String sDataKey = intent.getStringExtra(EXTRA_VIEW_FILE_PATH);			

					// Loading temporary save file of SAMMData				
					if(mSCanvas.loadSAMMData(sDataKey)){

						//Set Animation Option
						SOptionSCanvas canvasOption = new SOptionSCanvas();		
						setPlayOption(canvasOption);

						//					// Set play option					
						//					SOptionSCanvas canvasOption = new SOptionSCanvas();		
						//					// Whether set transparent background or not
						//					canvasOption.mPlayOption.setInvisibleBGImageAnimationOption(PreferencesOfAnimationOption.getPreferencePlayAnimationUsingTransparentBackground(mContext));
						//					// Set Background audio Play option
						//					canvasOption.mPlayOption.setPlayBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudio(mContext));
						//					// Set Background audio play repeat option
						//					canvasOption.mPlayOption.setRepeatBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudioReplay(mContext));
						//					// When stop animation play, whether set Background audio play stop or not
						//					canvasOption.mPlayOption.setStopBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudioStop(mContext));
						//					// when object drawing, whether set sound effect or not 
						//					canvasOption.mPlayOption.setSoundEffectOption(PreferencesOfAnimationOption.getPreferencePlayAnimationUsingSoundEffect(mContext));
						//
						//					// Set Background audio software volume
						//					if(!canvasOption.mPlayOption.setBGAudioVolume(1.0f))
						//						return;				
						//					// Set Sound effect software volume
						//					if(!canvasOption.mPlayOption.setSoundEffectVolume(1.0f))
						//						return;			
						//					// Set animation play speed
						//					if(!canvasOption.mPlayOption.setAnimationSpeed(PreferencesOfAnimationOption.getPreferencePlayAnimationSpeed(mContext)))
						//						return;				
						//					// Set Option
						//					if(!mSCanvas.setOption(canvasOption))
						//						return;

						// Start animation
						mSCanvas.doAnimationStart();	
					}
				}
			}
		};

		//--------------------------------------------
		// Set Callback Listener
		//--------------------------------------------
		// Play complete callback listener 
		AnimationProcessListener animationProcessListener = new AnimationProcessListener()	{
			@Override
			public void onPlayComplete() {
				Toast.makeText(SPen_Example_AnimationViewer.this, "Play Complete.", Toast.LENGTH_SHORT).show();
				mProgress.setVisibility(View.GONE);
				// Close menu after play complete
				closeOptionsMenu();  
			}

			@Override
			public void onChangeProgress(int nProgress) {
				if(nProgress==0) mProgress.setVisibility(View.VISIBLE);
				else if(nProgress==100) mProgress.setVisibility(View.GONE);
				mProgress.setProgress(nProgress);
			}
		};

		// Set the initialization finish listener
		mSCanvas.setSCanvasInitializeListener(scanvasInitializeListener);

		// Set the listener to execute when the animation completed.
		mSCanvas.setAnimationProcessListener(animationProcessListener);		

        //--------------------------------------------
		// Custom Sound Effect
		//--------------------------------------------
//		mSCanvas.setCustomSoundEffectSettingListener(new CustomSoundEffectSettingListener(){
//
//			String strSoundEffectPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
//			
//			@Override
//			public String onLoadCustomSoundEffect(int nSoundEffectType) {
//				switch(nSoundEffectType){
//					case SCanvasConstants.SOUND_EFFECT_TYPE_PEN_CLICK:
//						return strSoundEffectPath + "pen_click.ogg";
//					case SCanvasConstants.SOUND_EFFECT_TYPE_CRAYON_CLICK:
//						return strSoundEffectPath + "crayon_click.ogg";
//					case SCanvasConstants.SOUND_EFFECT_TYPE_PEN_DRAW:
//						return strSoundEffectPath + "pen_draw.ogg";
//					case SCanvasConstants.SOUND_EFFECT_TYPE_CRAYON_DRAW:
//						return strSoundEffectPath + "crayon_draw.ogg";
//					case SCanvasConstants.SOUND_EFFECT_TYPE_MARKER_DRAW:
//						return strSoundEffectPath + "shiny_draw.ogg"; 
//					case SCanvasConstants.SOUND_EFFECT_TYPE_BRUSH_DRAW:
//						return strSoundEffectPath + "brush_draw.ogg"; 
//					case SCanvasConstants.SOUND_EFFECT_TYPE_CHINESE_BRUSH_DRAW:
//						return strSoundEffectPath + "chinese_brush_draw.ogg"; 
//					case SCanvasConstants.SOUND_EFFECT_TYPE_ERASER_DRAW:
//						return strSoundEffectPath + "eraser_draw.ogg"; 
//					case SCanvasConstants.SOUND_EFFECT_TYPE_IMAGE_INSERT:
//						return strSoundEffectPath + "image_insert.ogg"; 
//					case SCanvasConstants.SOUND_EFFECT_TYPE_TEXT_INSERT:
//						return strSoundEffectPath + "text_type.ogg"; 
//					case SCanvasConstants.SOUND_EFFECT_TYPE_FILLING_INSERT:
//						return strSoundEffectPath + "color_filling.ogg"; 
//				}
//				return null;
//			}	
//		});
				
		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// close previous animation when device is rotated
		if(!mSCanvas.doAnimationClose())
			Log.e(TAG, "Fail to doAnimationClose");
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");

		mProgress.setVisibility(View.GONE);

		mIsRotation = !mIsRotation;
		createAnimationLayoutUI(mIsRotation);
		
		closeOptionsMenu();

		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(mSCanvas.isAnimationMode()) {
			int nAnimationState = mSCanvas.getAnimationState();
			if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_STOP) {
				mSCanvas.doAnimationStop(false);
			}else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_RUNNING) {
				mSCanvas.doAnimationPause();
			}
		}
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		// Result of selection file to start animation 
		if(requestCode==REQUEST_CODE_FILE_SELECT){
			if(resultCode==RESULT_OK){
				if(data == null)
					return;
				Bundle bundle = data.getExtras();
				if(bundle == null)
					return;
				mStrFileName = bundle.getString(ToolListActivity.EXTRA_SELECTED_FILE);

				if(loadAnimationFile(mStrFileName))
				{
					mIsLoadInViewer = true;
					// Start animation
					mSCanvas.doAnimationStart();	
				}					
			}
		}else if(requestCode==REQUEST_CODE_PLAY_OPTION){
			SOptionSCanvas canvasOption = new SOptionSCanvas();
			setPlayOption(canvasOption);
			canvasOption = mSCanvas.getOption();
			if(canvasOption!=null)
				mSCanvas.setAnimationSpeed(canvasOption.mPlayOption.getAnimationSpeed());
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){	
		menu.add(MENU_PLAYPAUSE, MENU_PLAYPAUSE, Menu.NONE, "Play");
		menu.add(MENU_STOP, MENU_STOP, Menu.NONE, "Stop");
		menu.add(MENU_LOAD_SELECT, MENU_LOAD_SELECT, Menu.NONE, "Load");
		menu.add(MENU_ANIMATION, MENU_ANIMATION, Menu.NONE, "Play Option");
		
		return super.onCreateOptionsMenu(menu);
	} 
	
	@Override
	public boolean onMenuOpened(int featureId, Menu menu){
		super.onMenuOpened(featureId, menu);

		if (menu != null){

			boolean bAnimationMode = mSCanvas.isAnimationMode();
			if(bAnimationMode){
				int nAnimationState = mSCanvas.getAnimationState();
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
		case MENU_LOAD_SELECT:
		{
			Intent intent = new Intent(getApplicationContext(), ToolListActivity.class);
			String [] exts = new String [] { "jpg", "png" }; // file extension 			
			intent.putExtra(ToolListActivity.EXTRA_LIST_PATH, mTempAMSFolderPath);
			intent.putExtra(ToolListActivity.EXTRA_FILE_EXT_ARRAY, exts);				
			intent.putExtra(ToolListActivity.EXTRA_SEARCH_ONLY_SAMM_FILE, true);
			startActivityForResult(intent, REQUEST_CODE_FILE_SELECT);
		}
		break;
		case MENU_ANIMATION:
		{
			Intent intent = new Intent(this, PreferencesOfAnimationOption.class);				
			startActivityForResult(intent, REQUEST_CODE_PLAY_OPTION);			
		}
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
		// Initialize background
		initBackground();

		if(!mSCanvas.loadSAMMFile(strFileName, false)){
			Toast.makeText(this, "Load AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	// Start or pause animation 
	void animationPlayOrPause(){
		int nAnimationState = mSCanvas.getAnimationState();
		if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_STOP)
		{
			// Start animation
//			mSCanvas.setAnimationSpeed(PreferencesOfAnimationOption.getPreferencePlayAnimationSpeed(mContext));
			SOptionSCanvas canvasOption = new SOptionSCanvas();
			setPlayOption(canvasOption);
			mSCanvas.doAnimationStart();	
		}
		else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_PAUSED)
			mSCanvas.doAnimationResume();
		else if(nAnimationState==SAMMLibConstants.ANIMATION_STATE_ON_RUNNING)
			mSCanvas.doAnimationPause();
	}	
	
	
	// Set Play Option
	void setPlayOption(SOptionSCanvas canvasOption) {
		// Set play option
		if(canvasOption == null) {
			canvasOption = new SOptionSCanvas();
		}
		// Whether set transparent background or not
		canvasOption.mPlayOption.setInvisibleBGImageAnimationOption(PreferencesOfAnimationOption.getPreferencePlayAnimationUsingTransparentBackground(mContext));
		// Set Background audio Play option
		canvasOption.mPlayOption.setPlayBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudio(mContext));
		// Set Background audio play repeat option
		canvasOption.mPlayOption.setRepeatBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudioReplay(mContext));
		// When stop animation play, whether set Background audio play stop or not
		canvasOption.mPlayOption.setStopBGAudioOption(PreferencesOfAnimationOption.getPreferencePlayBackgroundAudioStop(mContext));
		// when object drawing, whether set sound effect or not 
		canvasOption.mPlayOption.setSoundEffectOption(PreferencesOfAnimationOption.getPreferencePlayAnimationUsingSoundEffect(mContext));

		// Set Background audio software volume
		if(!canvasOption.mPlayOption.setBGAudioVolume(1.0f))
			return;				
		// Set Sound effect software volume
		if(!canvasOption.mPlayOption.setSoundEffectVolume(1.0f))
			return;			
		// Set animation play speed
		if(!canvasOption.mPlayOption.setAnimationSpeed(PreferencesOfAnimationOption.getPreferencePlayAnimationSpeed(mContext)))
			return;				
		// Set Option
		if(!mSCanvas.setOption(canvasOption))
			return;
	}
}
