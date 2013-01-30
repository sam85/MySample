package com.samsung.spensdk.example.spenevent_general;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenDetachmentListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_SPenEventGeneral extends Activity {

	private SPenEventLibrary mSPenEventLibrary;
	private ImageView mImageView;	
	private Context mContext = null;
	private TextView mX = null;
	private TextView mY = null;
	private TextView mPressure = null;
	private TextView mTool = null;	    
	private TextView mTouchAction = null;
	private TextView mHoverAction = null;

	private final int TOOL_UNKNOWN = 0;
	private final int TOOL_FINGER = 1;
	private final int TOOL_PEN = 2;
	private final int TOOL_PEN_ERASER = 3;
	private int mCurrentTool = TOOL_UNKNOWN;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	 
		mContext = this;
		setContentView(R.layout.general_purpose_spen_event);
		mImageView = (ImageView) findViewById(R.id.image_view);
		mX = (TextView)findViewById(R.id.x_point);
		mY = (TextView)findViewById(R.id.y_point);
		mTool = (TextView)findViewById(R.id.tool_type);
		mPressure = (TextView)findViewById(R.id.pressure);
		mTouchAction = (TextView)findViewById(R.id.tool_touch_action);
		mHoverAction = (TextView)findViewById(R.id.tool_hover_action);

		resetUI();

		mSPenEventLibrary = new SPenEventLibrary();
		//--------------------------------------------
		// Set S pen Touch Listener
		//--------------------------------------------
		mSPenEventLibrary.setSPenTouchListener(mImageView, new SPenTouchListener(){

			@Override
			public boolean onTouchFinger(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(), event.getAction(), "Finger");

				// Update Current Color
				if(mCurrentTool!= TOOL_FINGER){
					mCurrentTool = TOOL_FINGER;				
				}
				return true;	// keep event in this view 
			}

			@Override
			public boolean onTouchPen(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(), event.getAction(), "Pen");

				// Update Current Color
				if(mCurrentTool!=TOOL_PEN){
					mCurrentTool = TOOL_PEN;
				}
				return true;	// keep event in this view
			}

			@Override
			public boolean onTouchPenEraser(View view, MotionEvent event) {
				updateTouchUI(event.getX(), event.getY(), event.getPressure(),event.getAction(),  "Pen-Eraser");

				if(mCurrentTool!=TOOL_PEN_ERASER){
					mCurrentTool = TOOL_PEN_ERASER;
				}
				return true;	// keep event in this view
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
		mSPenEventLibrary.setSPenCustomHoverListener(mContext, mImageView, new SPenHoverListener(){
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
				Toast.makeText(mContext, "S Pen Button Up on Hover", Toast.LENGTH_SHORT).show();
			}
		}, getResources().getDrawable(R.drawable.tool_ic_pen));		
		
		
		//--------------------------------------------
		// Set S pen Detachment Listener
		//--------------------------------------------
		mSPenEventLibrary.registerSPenDetachmentListener(mContext, new SPenDetachmentListener(){
			@Override
			public void onSPenDetached(boolean bDetached) {
				if(bDetached) Toast.makeText(mContext, " SPen Detached", Toast.LENGTH_SHORT).show();
				else Toast.makeText(mContext, "S Pen Inserted", Toast.LENGTH_SHORT).show();
			}
		});
		
		
	}

	@Override
	protected void onDestroy() {
		// unregister SPenDetachment Listener
		mSPenEventLibrary.unregisterSPenDetachmentListener(mContext);
		super.onDestroy();
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
}
