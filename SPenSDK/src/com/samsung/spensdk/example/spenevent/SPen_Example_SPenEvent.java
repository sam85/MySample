package com.samsung.spensdk.example.spenevent;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.samm.common.SObjectStroke;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.SCanvasLongPressListener;
import com.samsung.spensdk.applistener.SPenDetachmentListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;
import com.samsung.spensdk.applistener.SettingStrokeChangeListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_SPenEvent extends Activity {

	private final String TAG = "SPenSDK Sample";
	
	private SCanvasView mSCanvas;	
	private Context mContext = null;
	private TextView mX = null;
	private TextView mY = null;
	private TextView mPressure = null;
	private TextView mTool = null;	    
	private TextView mTouchAction = null;
	private TextView mHoverAction = null;

	private SettingStrokeInfo mStrokeInfoPen;
	private SettingStrokeInfo mStrokeInfoFinger;
    
	private final int MENU_RESET = 1000;

	private final int TOOL_UNKNOWN = 0;
	private final int TOOL_FINGER = 1;
	private final int TOOL_PEN = 2;
	private final int TOOL_PEN_ERASER = 3;
	private int mCurrentTool = TOOL_UNKNOWN;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

    	
		mContext = this;
		setContentView(R.layout.example_spen_event);
		mSCanvas = (SCanvasView) findViewById(R.id.canvas_view);
		mX = (TextView)findViewById(R.id.x_point);
		mY = (TextView)findViewById(R.id.y_point);
		mTool = (TextView)findViewById(R.id.tool_type);
		mPressure = (TextView)findViewById(R.id.pressure);
		mTouchAction = (TextView)findViewById(R.id.tool_touch_action);
		mHoverAction = (TextView)findViewById(R.id.tool_hover_action);

		
		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, false, false);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, false, false);
		
		RelativeLayout settingViewContainer = (RelativeLayout) findViewById(R.id.canvas_container);		
		mSCanvas.createSettingView(settingViewContainer, settingResourceMapInt, settingResourceMapString);    	  
    	mSCanvas.setSCanvasHoverPointerStyle(SCanvasConstants.SCANVAS_HOVERPOINTER_STYLE_NONE);
    	
    	
		// Initialize Stroke Setting
		mStrokeInfoFinger = new SettingStrokeInfo();
		mStrokeInfoFinger.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_CRAYON);
		mStrokeInfoFinger.setStrokeColor(Color.RED);
		mStrokeInfoFinger.setStrokeWidth(50);
		
		mStrokeInfoPen = new SettingStrokeInfo();
		mStrokeInfoPen.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_PENCIL);
		mStrokeInfoPen.setStrokeColor(Color.BLUE);
		mStrokeInfoPen.setStrokeWidth(10);
		
		resetUI();

		//--------------------------------------------
		// Set S pen Touch Listener
		//--------------------------------------------
		mSCanvas.setSPenTouchListener(new SPenTouchListener(){

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(), event.getAction(), "Finger");

				// Update Current Color
				if(mCurrentTool!= TOOL_FINGER){
					mCurrentTool = TOOL_FINGER;

				if(event.getAction()==MotionEvent.ACTION_DOWN)
					mSCanvas.setSettingViewStrokeInfo(mStrokeInfoFinger);
				}
				return false;	// dispatch event to SCanvasView for drawing
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(), event.getAction(), "Pen");

				// Update Current Color
				if(mCurrentTool!=TOOL_PEN){
					mCurrentTool = TOOL_PEN;
				
				if(event.getAction()==MotionEvent.ACTION_DOWN)
					mSCanvas.setSettingViewStrokeInfo(mStrokeInfoPen);
				}
				
				return false;	// dispatch event to SCanvasView for drawing
			}

			@Override
			public boolean onTouchPenEraser(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(),event.getAction(),  "Pen-Eraser");

				if(mCurrentTool!=TOOL_PEN_ERASER){
					mCurrentTool = TOOL_PEN_ERASER;
				}
				
				if(event.getAction()==MotionEvent.ACTION_DOWN)
					mSCanvas.setEraserStrokeSetting(SObjectStroke.SAMM_DEFAULT_MAX_ERASERSIZE);

				return false;	// dispatch event to SCanvasView for drawing
			}		

			@Override
			public void onTouchButtonDown(View view, MotionEvent event) {
				//Toast.makeText(mContext, "S Pen Button Down on Touch", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onTouchButtonUp(View view, MotionEvent event) {
				Toast.makeText(mContext, "S Pen Button Up on Touch", Toast.LENGTH_SHORT).show();
			}			

		});


		//--------------------------------------------
		// [Custom Hover Icon Only]
		// Set Custom Hover Icon
		//--------------------------------------------
		//mSPenEventLibrary.setCustomHoveringIcon(mContext, mImageView, getResources().getDrawable(R.drawable.custom_hover_icon));
		
		//--------------------------------------------
		// [Hover Listener Only]
		// Set SPenHoverListener
		//--------------------------------------------
		//mSPenEventLibrary.setSPenHoverListener(mImageView, new SPenHoverListener(){...}

		//--------------------------------------------
		// [Hover Listener & Custom Hover Icon]
		// Set S pen HoverListener & Custom Hover Icon
		//--------------------------------------------
		mSCanvas.setSPenHoverListener(new SPenHoverListener(){
			@Override
			public boolean onHover(View view, MotionEvent event) {
				updateHoverUI(event.getX(), event.getY(), event.getPressure(), event.getAction(), "Hover");
				return false;
			}

			@Override
			public void onHoverButtonDown(View view, MotionEvent event) {
			}

			@Override
			public void onHoverButtonUp(View view, MotionEvent event) {
				mSCanvas.toggleShowSettingView(SCanvasConstants.SCANVAS_SETTINGVIEW_PEN);
			}
		});
		
		//--------------------------------------------
		// Set S pen Detachment Listener
		//--------------------------------------------
		mSCanvas.setSPenDetachmentListener(new SPenDetachmentListener(){
			@Override
			public void onSPenDetached(boolean bDetached) {
				if(bDetached) Toast.makeText(mContext, "S Pen Detached", Toast.LENGTH_SHORT).show();
				else Toast.makeText(mContext, "S Pen Inserted", Toast.LENGTH_SHORT).show();
			}
		});
		
		//--------------------------------------------
		// Set S pen Long Press Listener
		//--------------------------------------------
		mSCanvas.setSCanvasLongPressListener(new SCanvasLongPressListener(){
			@Override
			public void onLongPressed(float fPosX, float fPosY) {
				if(mCurrentTool== TOOL_FINGER)
					Toast.makeText(mContext, "Long Pressed by Finger", Toast.LENGTH_SHORT).show();
				else if(mCurrentTool== TOOL_PEN)
					Toast.makeText(mContext, "Long Pressed by Pen", Toast.LENGTH_SHORT).show();
				else if(mCurrentTool== TOOL_PEN_ERASER)
					Toast.makeText(mContext, "Long Pressed by Pen-Eraser", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(mContext, "Long Pressed by other tools", Toast.LENGTH_SHORT).show();				
			}
			@Override
			public void onLongPressed() {				
			}
		});

		//------------------------------------------------
		// OnSettingStrokeChangeListener Listener 
		//------------------------------------------------				
		mSCanvas.setSettingStrokeChangeListener( new SettingStrokeChangeListener() {
			@Override
			public void onClearAll(boolean bClearAllCompleted) {
			}
			@Override
			public void onEraserWidthChanged(int eraserWidth) {				
			}

			@Override
			public void onStrokeColorChanged(int strokeColor) {
				if(mCurrentTool == TOOL_PEN)
					mStrokeInfoPen.setStrokeColor(strokeColor);
				else if(mCurrentTool == TOOL_FINGER)
					mStrokeInfoFinger.setStrokeColor(strokeColor);
				Log.e("TEST", "color " + strokeColor);

			}

			@Override
			public void onStrokeStyleChanged(int strokeStyle) {
				if(mCurrentTool == TOOL_PEN)
					mStrokeInfoPen.setStrokeStyle(strokeStyle);
				else if(mCurrentTool == TOOL_FINGER)
					mStrokeInfoFinger.setStrokeStyle(strokeStyle);
			}

			@Override
			public void onStrokeWidthChanged(int strokeWidth) {
				if(mCurrentTool == TOOL_PEN)
					mStrokeInfoPen.setStrokeWidth(strokeWidth);
				else if(mCurrentTool == TOOL_FINGER)
					mStrokeInfoFinger.setStrokeWidth(strokeWidth);
			}

			@Override
			public void onStrokeAlphaChanged(int strokeAlpha) {				
				if(mCurrentTool == TOOL_PEN)
					mStrokeInfoPen.setStrokeAlpha(strokeAlpha);
				else if(mCurrentTool == TOOL_FINGER)
					mStrokeInfoFinger.setStrokeAlpha(strokeAlpha);
			}
		});
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

	// Reset Touch UI
	private void resetUI(){
		mX.setText("X : " + String.format("%.2f", 0.f));
		mY.setText("Y : " + String.format("%.2f", 0.f));
		mPressure.setText("Pressure : " + String.format("%.3f", 0.f));

		mTool.setText("Tool");
		mTouchAction.setText("Touch");
		mHoverAction.setText("Hover");
	}

	// Update Touch UI
	private void updateTouchUI(float x, float y, float pressure, int action, String tool){
		mX.setText("X : " + String.format("%.2f", x));
		mY.setText("Y : " + String.format("%.2f", y));
		mPressure.setText("Pressure : " + String.format("%.3f", pressure));

		if(action==MotionEvent.ACTION_DOWN)			mTouchAction.setText("DOWN");
		else if(action==MotionEvent.ACTION_MOVE)	mTouchAction.setText("MOVE");
		else if(action==MotionEvent.ACTION_UP)		mTouchAction.setText("UP");
		else if(action==MotionEvent.ACTION_CANCEL)	mTouchAction.setText("CANCEL");
		else 										mTouchAction.setText("Unknow");
		mTool.setText(tool);		
	}

	// Update Hover UI
	private void updateHoverUI(float x, float y, float pressure, int action, String tool){

		// For noise point On Hover, so filter it 
		if(x>=0 && y>=0){ 
			mX.setText("X : " + String.format("%.2f", x));
			mY.setText("Y : " + String.format("%.2f", y));
			mPressure.setText("Pressure : " + String.format("%.3f", pressure));
		}

		if(action==MotionEvent.ACTION_HOVER_ENTER)		mHoverAction.setText("HOVER ENTER");
		else if(action==MotionEvent.ACTION_HOVER_MOVE)	mHoverAction.setText("HOVER MOVE");
		else if(action==MotionEvent.ACTION_HOVER_EXIT)	mHoverAction.setText("HOVER EXIT");
		else 											mHoverAction.setText("Unknow");
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu){			
		menu.add(MENU_RESET, MENU_RESET, Menu.NONE, "Reset");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);

		if(mSCanvas == null)
			return false;

		switch(item.getItemId()) {
		case MENU_RESET:
			mSCanvas.clearSCanvasView();
			resetUI();
			break;
		}
		return true;
	}	
}
