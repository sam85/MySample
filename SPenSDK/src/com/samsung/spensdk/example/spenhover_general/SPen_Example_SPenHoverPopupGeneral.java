package com.samsung.spensdk.example.spenhover_general;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;
import com.samsung.spensdk.example.tools.ToolHoverPopup;

public class SPen_Example_SPenHoverPopupGeneral extends Activity {

   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general_purpose_hover_popupwindow);
		
		if(SPenEventLibrary.isHoverIconSupport(this)){
			View tooltip1 = findViewById(R.id.view_tooltip1);
			ToolHoverPopup hover1 = new ToolHoverPopup(tooltip1, ToolHoverPopup.TYPE_TOOLTIP);

			View tooltip2 = findViewById(R.id.view_tooltip2);
			tooltip2.setContentDescription("Time 500ms");
			ToolHoverPopup hover2 = new ToolHoverPopup(tooltip2, ToolHoverPopup.TYPE_TOOLTIP);
			hover2.setHoverDetectTime(500);
			hover2.setPopupGravity(ToolHoverPopup.Gravity.TOP_ABOVE | ToolHoverPopup.Gravity.RIGHT_CENTER_AXIS);

			View userView1 = findViewById(R.id.view_user1);
			View custromView1 = LayoutInflater.from(this).inflate(R.layout.general_purpose_hover_customview, null);
			ToolHoverPopup hover3 = new ToolHoverPopup(userView1, ToolHoverPopup.TYPE_USER_CUSTOM);
			hover3.setContent(custromView1);
			hover3.setGuideLineEnabled(true);
			hover3.setGuideLineFadeOffset(2);
			hover3.setPopupPosOffset(0, -50);
			hover3.setPopupGravity(ToolHoverPopup.Gravity.TOP_ABOVE | ToolHoverPopup.Gravity.RIGHT_CENTER_AXIS);

			View userView2 = findViewById(R.id.view_user2);
			View customView2 = LayoutInflater.from(this).inflate(R.layout.general_purpose_hover_customview, null);
			ToolHoverPopup hover4 = new ToolHoverPopup(userView2, ToolHoverPopup.TYPE_USER_CUSTOM);
			hover4.setContent(customView2);
			hover4.setPopupPosOffset(0, -10);
			hover4.setAnimationStyle(android.R.style.Animation_Toast);
			hover4.setPopupGravity(ToolHoverPopup.Gravity.BOTTOM_UNDER | ToolHoverPopup.Gravity.CENTER_HORIZONTAL);

			TextView tv1 = (TextView)findViewById(R.id.textview_abbr1);		
			View customView3 = LayoutInflater.from(this).inflate(R.layout.general_purpose_hover_textview, null);		
			ToolHoverPopup textHover1 = new ToolHoverPopup(tv1, ToolHoverPopup.TYPE_USER_CUSTOM);
			textHover1.setContent(customView3);
			textHover1.setPopupGravity(ToolHoverPopup.Gravity.TOP_ABOVE | ToolHoverPopup.Gravity.RIGHT_CENTER_AXIS);

			TextView tv2 = (TextView)findViewById(R.id.textview_abbr2);
			View customView4 = LayoutInflater.from(this).inflate(R.layout.general_purpose_hover_textview, null);		
			ToolHoverPopup textHover2 = new ToolHoverPopup(tv2, ToolHoverPopup.TYPE_USER_CUSTOM);
			textHover2.setContent(customView4);
			textHover2.setPopupGravity(ToolHoverPopup.Gravity.TOP_ABOVE | ToolHoverPopup.Gravity.RIGHT_CENTER_AXIS);
		}
	}
	
	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}
}
