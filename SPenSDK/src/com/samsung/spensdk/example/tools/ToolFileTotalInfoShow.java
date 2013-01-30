package com.samsung.spensdk.example.tools;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

import com.samsung.samm.common.SObject;
import com.samsung.samm.common.SObjectFilling;
import com.samsung.samm.common.SObjectImage;
import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SObjectText;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.FileProcessListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;
import com.samsung.spensdk.example.R;

public class ToolFileTotalInfoShow extends Activity {
	/** Called when the activity is first created. */

	static public final String EXTRA_SAMM_FILE_INFO = "ExtraSAMMFilePath";
	static public final String EXTRA_SCANVAS_WIDTH = "ExtraScanvasWidth";
	static public final String EXTRA_SCANVAS_HEIGHT = "ExtraScanvasHeight";
	static public final String EXTRA_SCANVAS_PUTEXTRA = "ExtraScanvasPutExtra";

	//==============================
	// Constants 
	//==============================
	private static final int 	TOTAL_INFO_NUM = 8;			// The number of basic data
	private static final int 	INFO_APPLICATION_ID = 0;	// basic data
	private static final int 	INFO_TITLE_INDEX = 1;		// basic data
	private static final int 	INFO_BACKGROUNDAUDIO_INDEX = 2;		// basic data
	private static final int 	INFO_BACKGROUNDCOLOR_INDEX = 3;		// basic data
	private static final int 	INFO_BACKGROUNDIMAGE_INDEX = 4;		// basic data
	private static final int 	INFO_EXTRA_DATA = 5;		// basic data
	private static final int 	INFO_TAG_DATA = 6;			// basic data
	private static final int 	INFO_TAG_PREFERENCE = 7;			// basic data

	private String APPID_TAG = "[Application ID]";
	private String TITLE_TAG = "[Title]";
	private String BACKGROUNDAUDIO_TAG = "[BACKGROUND Audio]";
	private String BACKGROUNDCOLOR_TAG = "[BACKGROUND Color]";
	private String BACKGROUNDIMAGE_TAG = "[BACKGROUND IMAGE]";
	private String IMAGE_TAG = "[Image]";
	private String TEXT_TAG = "[Text]";
	private String STROKE_TAG = "[Stroke]";
	private String EXTRA_TAG = "[Extra Data]";
	private String TAG_TAG = "[TAG Data]";
	private String PREFERENCE_TAG = "[Preference]";

	//==============================
	// Local Variables 
	//==============================
	Context mContext = null;
	private ListAdapter mListAdapter = null;
	private SCanvasView	mSCanvas = null;
	private ListView mlistView = null;		
	private boolean mIncludeOnlyVisible = false; // get all objects


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.libtest);

		mContext = this;

		// create list and button
		createUI();		

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}	




	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		super.dispatchKeyEvent(event);

		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			Intent intent = getIntent();
			if(intent == null)
				return false;
			String tmpStr = intent.getStringExtra(EXTRA_SAMM_FILE_INFO);
			if(tmpStr == null)
				return false;
			intent.putExtra(EXTRA_SAMM_FILE_INFO, tmpStr);		
			setResult(RESULT_OK, intent);
			finish();
		}			
		return true;			
	}


	private boolean loadSSAMObject(String strPath) {
		if(mSCanvas==null)
			return false;

		File f = new File (strPath);		
		if (!f.exists()){
			Toast.makeText(ToolFileTotalInfoShow.this, "Saved Test Image is not exist", Toast.LENGTH_LONG).show();
			return false;
		}

		if(!mSCanvas.loadSAMMFile(strPath, false, false, true)){
			// if(!mCanvasView.loadSAMMFile(strPath, true)){
			Toast.makeText(this, "Load SAMMFile Fail!", Toast.LENGTH_SHORT).show();
			return false;
		}
		mSCanvas.setVisibility(View.GONE);
		return true;
	}

	private void createUI() {
		mListAdapter = new ListAdapter(this);
		mlistView = (ListView)findViewById(R.id.objectList);
		mlistView.setAdapter(mListAdapter);

		mlistView.setItemsCanFocus(false);
		mlistView.setTextFilterEnabled(true);
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showObjectInfo(position);
			}
		});

		//Set real drawing view size, Set screen size for test
		//Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		//int nCanvasWidth = display.getWidth(); 
		//int nCanvasHeight = display.getHeight();

		Intent intent = getIntent();
		if(intent == null)
			return;
		int nTmpWidth = intent.getIntExtra(EXTRA_SCANVAS_WIDTH, 0);
		int nTmpHeight = intent.getIntExtra(EXTRA_SCANVAS_HEIGHT, 0);
		if(nTmpWidth <= 0 || nTmpHeight <= 0)
			return;		 		

		//====================================
		// Create SCanvasView dynamically
		//====================================
		RelativeLayout layout = (RelativeLayout)findViewById(R.id.canvas_container);		
		LayoutParams params = new LayoutParams(nTmpWidth, nTmpHeight);
		mSCanvas = new SCanvasView(mContext);
		mSCanvas.setLayoutParams(params);		
		layout.addView(mSCanvas);

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
				Intent intent = getIntent();
				if(intent == null)
					return;		
				String tmpStr = intent.getStringExtra(EXTRA_SAMM_FILE_INFO);			
				if(tmpStr == null)
					return;		

				if(loadSSAMObject(tmpStr))
					mListAdapter.updateDisplay();
				else
					return;			
			}
		};
		// Set the initialization finish listener
		mSCanvas.setSCanvasInitializeListener(scanvasInitializeListener);

		//------------------------------------------------
		// File Processing 
		//------------------------------------------------
		FileProcessListener mFileProgressChange = new FileProcessListener() {
			@Override
			public void onChangeProgress(int nProgress) {
				//Log.i(TAG, "Progress = " + nProgress);
			}

			@Override
			public void onLoadComplete(boolean bLoadResult) {			 
				if(bLoadResult){
					// Show Application Identifier
					mListAdapter.updateDisplay();
					String appID = mSCanvas.getAppID();
					Toast.makeText(ToolFileTotalInfoShow.this, "Load AMS File("+ appID + ") Success!", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(ToolFileTotalInfoShow.this, "Load AMS File Fail!", Toast.LENGTH_LONG).show();				
				}
			}
		};
		mSCanvas.setFileProcessListener(mFileProgressChange);

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialize() of SCanvasInitializeListener 
	}

	//=================================================================
	// List adapter : The kind of Object (Title, BackGroundColor, Text, Image, BackGroundAudio)  
	//=================================================================
	public class ListAdapter extends BaseAdapter {

		public ListAdapter(Context context) {
		}    	

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				final LayoutInflater inflater = getLayoutInflater();
				convertView = inflater.inflate(R.layout.libtest_list_item, parent, false);
			}
			// UI Item
			ImageView im =  (ImageView)convertView.findViewById(R.id.itemImg);
			TextView tvObjectattribute=  (TextView)convertView.findViewById(R.id.objectattibute);
			TextView tvObjectContent=  (TextView)convertView.findViewById(R.id.objectcontent);
			// TextView itemText=  (TextView)convertView.findViewById(R.id.itemText);

			//==================================
			// Show basic data
			//==================================
			if(position<TOTAL_INFO_NUM){

				if(position == INFO_APPLICATION_ID)
				{
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_appid));
					// The kind of basic data
					tvObjectattribute.setText(APPID_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					tvObjectContent.setText(mSCanvas.getAppID());
				}
				else if(position == INFO_TITLE_INDEX) {
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_title));
					// The kind of basic data
					tvObjectattribute.setText(TITLE_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					tvObjectContent.setText(mSCanvas.getTitle());
				}
				else if(position == INFO_BACKGROUNDAUDIO_INDEX)	{
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_bgaudio));
					// The kind of basic data   
					tvObjectattribute.setText(BACKGROUNDAUDIO_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					tvObjectContent.setText("Path : " + mSCanvas.getBGAudioFile());
				}
				else if(position == INFO_BACKGROUNDCOLOR_INDEX)	{
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_bgcolor));
					// The kind of basic data   
					tvObjectattribute.setText(BACKGROUNDCOLOR_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					if(mSCanvas.getBGImagePath() == null)
						tvObjectContent.setText("Color:" + mSCanvas.getBGColor());
					else
						tvObjectContent.setText("Color:" + "0");
				}
				else if(position == INFO_BACKGROUNDIMAGE_INDEX)	{
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_bgcolor));
					// The kind of basic data 
					tvObjectattribute.setText(BACKGROUNDIMAGE_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					// tvObjectContent.setText("Path:" + mSCanvas.getBGImagePath());
					tvObjectContent.setText("Path:" + mSCanvas.getBGImagePathDecoded());
				}
				else if(position == INFO_EXTRA_DATA)	{
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_extra));
					// The kind of basic data    
					tvObjectattribute.setText(EXTRA_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information 
					tvObjectContent.setText("Extra data:" + 
							mSCanvas.getStringExtra(EXTRA_SCANVAS_PUTEXTRA, null));
				}			
				else if(position == INFO_TAG_DATA)	{
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_tag));
					// The kind of basic data 
					tvObjectattribute.setText(TAG_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					String[] tagArray = mSCanvas.getTags();					
					String strTags = null;
					if(tagArray != null) {
						StringBuffer res = new StringBuffer();
						for(String tag : tagArray){
							if(res.length()==0) 
								res.append(tag);
							else{
								res.append("; " + tag);							
							}
						}
						strTags = res.toString();
						res.delete(0, res.length());
					}
					tvObjectContent.setText("TAG data:" + strTags);
				}				
				else if(position == INFO_TAG_PREFERENCE) {
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.list_icon_preference));
					// The kind of basic data    
					tvObjectattribute.setText(PREFERENCE_TAG);
					tvObjectattribute.setTextColor(0xFF00B8FF);
					// Basic data information
					String strTemp;
					if(mSCanvas.getCheckPreference() == 0)
						strTemp = "Normal";
					else if(mSCanvas.getCheckPreference() == 1)
						strTemp = "Favorite";
					else
						strTemp = "Custom";
					tvObjectContent.setText("Preference:" + strTemp);
				}
			}
			//==================================
			// Show Object data 
			//==================================
			else {

				// get each Object
				int nMaxObjectNum = mSCanvas.getSAMMObjectNum(mIncludeOnlyVisible);
				int nCurObjectIndex = position - TOTAL_INFO_NUM;
				int nCurObjectNum = nCurObjectIndex + 1;
				if(nCurObjectIndex<0 || nCurObjectIndex>=nMaxObjectNum)
					return convertView; 

				SObject sammObject = mSCanvas.getSAMMObject(nCurObjectIndex, mIncludeOnlyVisible);
				if(sammObject == null) {					
					return convertView;
				}					

				String tempString = getSObjectAttributeInfo(sammObject);

				if(sammObject instanceof SObjectStroke) {
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.tool_ic_pen_press));
					// Show object attribute
					tvObjectattribute.setTextColor(0xFF00B8FF);					
					tvObjectattribute.setText("[" + nCurObjectNum + "]  " + tempString);
					// Object Information
					tvObjectContent.setText(sammObject.getObjectInfo());									

				}
				else if(sammObject instanceof SObjectImage) {
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.tool_ic_attach_press));
					// Show object attribute
					tvObjectattribute.setTextColor(0xFF00B8FF);
					tvObjectattribute.setText("[" + nCurObjectNum + "]  " + tempString);
					// Object Information
					tvObjectContent.setText(sammObject.getObjectInfo());				
				}    
				else if(sammObject instanceof SObjectText) {
					// Icon image
					im.setImageDrawable(getResources().getDrawable(R.drawable.tool_ic_text_press));
					// Show object attribute
					tvObjectattribute.setTextColor(0xFF00B8FF);
					tvObjectattribute.setText("[" + nCurObjectNum + "]  " + tempString);
					// Object Information
					tvObjectContent.setText(sammObject.getObjectInfo());				
				}
				else if(sammObject instanceof SObjectFilling) {
					// Icon Image
					im.setImageDrawable(getResources().getDrawable(R.drawable.tool_ic_filling_press));
					// object property
					tvObjectattribute.setTextColor(0xFF00B8FF);
					tvObjectattribute.setText("[" + nCurObjectNum + "]  " + tempString);
					// Object info
					tvObjectContent.setText(sammObject.getObjectInfo());				
				}
				else {
					// Icon image
					im.setImageDrawable(null);
					// Object Information
					tvObjectContent.setText("NO INFORMATION");				
					// Show the kind of object
					//itemText.setText("Unknown Object");
				}	
			}

			return convertView; 
		}

		public void updateDisplay() {
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if(mSCanvas!=null){
				int nSAMMObjectNum = mSCanvas.getSAMMObjectNum(mIncludeOnlyVisible);
				if(nSAMMObjectNum<0)	return 0;
				else 					return nSAMMObjectNum + TOTAL_INFO_NUM;
			}
			else{
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	private void showObjectInfo(int pos) {
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		//		View layout = inflater.inflate(R.layout.libtest_textview, (ViewGroup)findViewById(R.id.objectdetails));
		// Second parameter of LayoutInflater.inflate must be root view
		View layout = inflater.inflate(R.layout.libtest_textview, (ViewGroup)findViewById(R.id.textdialoglayout));
		AlertDialog.Builder textInputDialog = new AlertDialog.Builder(mContext);
		String msg;
		textInputDialog.setTitle("Object Information");
		if(pos ==INFO_APPLICATION_ID)
		{
			msg = APPID_TAG;
			textInputDialog.setMessage(msg + "\n\n" + "AppID:" + mSCanvas.getAppID());
		}
		else if(pos == INFO_TITLE_INDEX) {
			msg = TITLE_TAG;			
			textInputDialog.setMessage(msg + "\n\n" + "Title:" + mSCanvas.getTitle());
		}
		else if(pos == INFO_BACKGROUNDAUDIO_INDEX) {
			msg = BACKGROUNDAUDIO_TAG;			
			textInputDialog.setMessage(msg + "\n\n" + "Path:" + mSCanvas.getBGAudioFile());
		}
		else if(pos == INFO_BACKGROUNDCOLOR_INDEX) {
			msg = BACKGROUNDCOLOR_TAG;			
			textInputDialog.setMessage(msg + "\n\n" + "Color:" + mSCanvas.getBGColor());
		}
		else if(pos == INFO_BACKGROUNDIMAGE_INDEX) {
			msg = BACKGROUNDIMAGE_TAG;			
			textInputDialog.setMessage(msg + "\n\n" + "Path:" + mSCanvas.getBGImagePath());
		}
		else if(pos == INFO_EXTRA_DATA) {
			msg = EXTRA_TAG;			
			textInputDialog.setMessage(msg + "\n\n" + "Extra data:" + 
					mSCanvas.getStringExtra(EXTRA_SCANVAS_PUTEXTRA, null));
		}
		else if(pos == INFO_TAG_DATA) {
			msg = TAG_TAG;			
			String[] tagArray = mSCanvas.getTags();
			String strTags = null; 

			if(tagArray != null) {
				StringBuffer res = new StringBuffer();

				for(String tag : tagArray){
					if(res.length()==0) 
						res.append(tag);
					else{
						res.append("; " + tag);							
					}
				}
				strTags = res.toString();
				res.delete(0, res.length());
			}

			textInputDialog.setMessage(msg + "\n\n" + "Tag data:" +  strTags);
		}		
		else if(pos == INFO_TAG_PREFERENCE) {
			msg = PREFERENCE_TAG;
			String strTemp;
			if(mSCanvas.getCheckPreference() == 0)
				strTemp = "Normal";
			else if(mSCanvas.getCheckPreference() == 1)
				strTemp = "Favorite";
			else
				strTemp = "Custom";
			textInputDialog.setMessage(msg + "\n\n" + "Preference:" + strTemp);
		}
		else {
			int nCurObjectIndex = pos - TOTAL_INFO_NUM;
			SObject sammObject = mSCanvas.getSAMMObject(nCurObjectIndex, mIncludeOnlyVisible);
			if(sammObject == null)
				return;
			String tempString = getSObjectAttributeInfo(sammObject);
			if(sammObject instanceof SObjectStroke){
				msg = STROKE_TAG;				
				textInputDialog.setMessage(msg + "\n\n" + tempString + "\n\n" + sammObject.getObjectInfo());
			}
			else if(sammObject instanceof SObjectImage){
				msg = IMAGE_TAG;				
				textInputDialog.setMessage(msg + "\n\n" + tempString + "\n\n" + sammObject.getObjectInfo());
			}
			else if(sammObject instanceof SObjectText){
				msg = TEXT_TAG;				
				textInputDialog.setMessage(msg + "\n\n" + tempString + "\n\n" + sammObject.getObjectInfo());
			}	
		}
		textInputDialog.setView(layout);		
		AlertDialog ad = textInputDialog.create();
		ad.show();		
	}


	private String getSObjectAttributeInfo(SObject sammObject) {
		String tempString = "Style = " + sammObject.getStyle() + ", " + "Color = " + 
		sammObject.getColor() + ", " + "Size = " + sammObject.getSize() + ", " + "Rect.left = " + 
		sammObject.getRect().left + ", " + "Rect.right = " + sammObject.getRect().right +
		", " + "Rect.top = " +	sammObject.getRect().top + ", " + "Rect.bottom = " + 
		sammObject.getRect().bottom/* + ", " + "PageIndex = " + sammObject.getPageIndex() +
		", " + "RotateAngle = " + sammObject.getRotateAngle() + 
		", " + "Hypertext = " + sammObject.getHyperText() +
		", " + "Description = " + sammObject.getDescription() + 
		", " + "Latitude = " + sammObject.getLatitude() +
		", " + "Longitude = " + sammObject.getLongitude()*/;
		return tempString;
	}

}

