package com.samsung.spensdk.example.spenhover_general;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_SPenHoverPointerGeneral extends Activity {

	private Context mContext = null;
	private SPenEventLibrary mSPenEventLibrary;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_purpose_hover_pointer);
		TextView view;
		
		mContext = this;
		mSPenEventLibrary = new SPenEventLibrary();
		
		view = (TextView)findViewById(R.id.cursor);
		mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_CURSOR);
        view = (TextView)findViewById(R.id.split01);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_SPLIT_01);
        view = (TextView)findViewById(R.id.split02);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_SPLIT_02);
        view = (TextView)findViewById(R.id.move);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_MOVE);
        view = (TextView)findViewById(R.id.resize01);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_01);
        view = (TextView)findViewById(R.id.resize02);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_02);
        view = (TextView)findViewById(R.id.resize03);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_03);
        view = (TextView)findViewById(R.id.resize04);
        mSPenEventLibrary.setSPenHoverIcon(mContext, view, SPenEventLibrary.HOVERING_SPENICON_RESIZE_04);
        
        Button button = (Button)findViewById(R.id.custom);
        button.setOnClickListener(new OnClickListener() {
            
        	boolean bCustomDrawable = false;
            @Override
            public void onClick(View arg0) {
            	bCustomDrawable = !bCustomDrawable;
            	if(bCustomDrawable){
            		mSPenEventLibrary.setSPenCustomHoverIcon(mContext, arg0, getResources().getDrawable(R.drawable.hover_ic_point) );
            		Toast.makeText(mContext, "Set to custom hover icon", Toast.LENGTH_SHORT).show();
            	}
            	else{
            		mSPenEventLibrary.setSPenCustomHoverIcon(mContext, arg0, null );
            		Toast.makeText(mContext, "Set to default hover icon", Toast.LENGTH_SHORT).show();
            	}
            }
        });
	}
	
	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}
}
