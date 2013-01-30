package com.samsung.spensdk.example.scratcheffect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;


public class SPen_Example_ScratchEffect extends Activity {

	private final String TAG = "SPenSDK ScratchEffect";
	private final int MENU_IMAGE_OPERATION = Menu.FIRST + 1;
	private final int MENU_IMAGE_OPERATION_LEVEL = Menu.FIRST + 2;
	private final int MENU_CLEAR_ALL = Menu.FIRST + 3;
	private final int MENU_RESET = Menu.FIRST + 4;

	private SCanvasView		mSCanvas1;
	private SCanvasView		mSCanvas2;
	private SCanvasView		mSCanvas3;

	// Conversion array to map menu index to Image Filter Index (only 10 Image filter)
	private int[] imageOperationByIndex = {SPenImageFilterConstants.FILTER_GRAY, 
			SPenImageFilterConstants.FILTER_NEGATIVE, SPenImageFilterConstants.FILTER_DARK, SPenImageFilterConstants.FILTER_COLORIZE, 
			SPenImageFilterConstants.FILTER_BLUR, SPenImageFilterConstants.FILTER_PENCILSKETCH,	SPenImageFilterConstants.FILTER_RETRO,
			SPenImageFilterConstants.FILTER_MOSAIC, SPenImageFilterConstants.FILTER_MAGICPEN, SPenImageFilterConstants.FILTER_CARTOONIZE,};


	private int mImageOperationIndex = 4; // IMAGE_OPERATION_BLUR
	private int mImageOperationLevelIndex = 2; // OPERATION_LEVEL_MEDIUM
	private int mImageOperation = SPenImageFilterConstants.FILTER_BLUR;
	private int mImageOperationLevel = SPenImageFilterConstants.FILTER_LEVEL_LARGE;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewer_scratch);

		mSCanvas1 = (SCanvasView) findViewById(R.id.canvas_view1);
		mSCanvas2 = (SCanvasView) findViewById(R.id.canvas_view2);
		mSCanvas3 = (SCanvasView) findViewById(R.id.canvas_view3);

		//====================================================================================
		//
		// Set Callback Listener(Interface)
		//
		//====================================================================================
		//------------------------------------------------
		// SCanvasLayout Initialization Listener
		//------------------------------------------------
		SCanvasInitializeListener scanvasInitializeListener1 = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				mSCanvas1.setCanvasZoomEnable(false);
				mSCanvas1.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
				mSCanvas1.setEraserStrokeSetting(SObjectStroke.SAMM_DEFAULT_MAX_ERASERSIZE);
				// Set Clear Image Bitmap
				// It is better to use the bitmap whose size is same as those of canvas.
				// If it is different, setClearImageBitmap() resize the bitmap inside.
				Bitmap bmScratch = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_pattern);
				mSCanvas1.setClearImageBitmap(bmScratch, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);
			}
		};
		SCanvasInitializeListener scanvasInitializeListener2 = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				mSCanvas2.setCanvasZoomEnable(false);
				mSCanvas2.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
				mSCanvas2.setEraserStrokeSetting(SObjectStroke.SAMM_DEFAULT_MAX_ERASERSIZE);
				// Set Clear Image Bitmap
				// It is better to use the bitmap whose size is same as those of canvas.
				// If it is different, setClearImageBitmap() resize the bitmap inside.
				Bitmap bmScratch = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_pattern);
				mSCanvas2.setClearImageBitmap(bmScratch, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);			
			}
		};
		SCanvasInitializeListener scanvasInitializeListener3 = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() {
				mSCanvas3.setCanvasZoomEnable(false);
				mSCanvas3.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
				mSCanvas3.setEraserStrokeSetting(SObjectStroke.SAMM_DEFAULT_MAX_ERASERSIZE);
				// Set Clear Image Bitmap
				// It is better to use the bitmap whose size is same as those of canvas.
				// If it is different, setClearImageBitmap() resize the bitmap inside.
				Bitmap bmScratch = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_bg);
				mSCanvas3.setClearImageBitmap(bmScratch, mImageOperation, mImageOperationLevel);
			}
		};
		mSCanvas1.setSCanvasInitializeListener(scanvasInitializeListener1);
		mSCanvas2.setSCanvasInitializeListener(scanvasInitializeListener2);
		mSCanvas3.setSCanvasInitializeListener(scanvasInitializeListener3);

		// Caution:
			// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}

	@Override
	protected void onDestroy() {	
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas1.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");

		if(!mSCanvas2.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");

		if(!mSCanvas3.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");

	}
	
	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		menu.add(MENU_IMAGE_OPERATION, MENU_IMAGE_OPERATION, Menu.NONE, "Operation");
		menu.add(MENU_IMAGE_OPERATION_LEVEL, MENU_IMAGE_OPERATION_LEVEL, Menu.NONE, "Level");
		menu.add(MENU_CLEAR_ALL, MENU_CLEAR_ALL, Menu.NONE, "Clear All");
		menu.add(MENU_RESET, MENU_RESET, Menu.NONE, "Reset");

		return super.onCreateOptionsMenu(menu);
	} 

	void resetClearImage(boolean bClearAll, int nImageOperationOption, int nLevel){
		if(bClearAll){
			mSCanvas1.setClearImageBitmap(null, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);
			mSCanvas2.setClearImageBitmap(null, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);
			mSCanvas3.setClearImageBitmap(null, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);
		}
		else{
			// Set Clear Image Bitmap
			// It is better to use the bitmap whose size is same as those of canvas.
			// If it is different, setClearImageBitmap() resize the bitmap inside.
			Bitmap bmScratch = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_pattern);
			mSCanvas1.setClearImageBitmap(bmScratch, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);
			mSCanvas2.setClearImageBitmap(bmScratch, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);

			Bitmap bmScratchPic = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_bg);
			mSCanvas3.setClearImageBitmap(bmScratchPic, nImageOperationOption, nLevel);
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		case MENU_CLEAR_ALL:
			resetClearImage(true, mImageOperation, mImageOperationLevel);
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
			.setTitle("Image Operation")
			.setSingleChoiceItems(R.array.imageoperation_level, mImageOperationLevel, new DialogInterface.OnClickListener() {			
				@Override
				public void onClick(DialogInterface dialog, int which) {
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
				
				Bitmap bmScratchPic = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_bg);
				mSCanvas3.setClearImageBitmap(bmScratchPic, mImageOperation, mImageOperationLevel);
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
				
				Bitmap bmScratchPic = BitmapFactory.decodeResource(getResources(), R.drawable.scratch_bg);
				mSCanvas3.setClearImageBitmap(bmScratchPic, mImageOperation, mImageOperationLevel);
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		})		
		.show();	
	}
}
