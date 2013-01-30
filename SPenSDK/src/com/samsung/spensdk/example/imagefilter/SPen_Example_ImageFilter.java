package com.samsung.spensdk.example.imagefilter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.samsung.spen.lib.image.SPenImageFilter;
import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spensdk.example.R;
import com.samsung.spensdk.example.tools.SPenSDKUtils;

public class SPen_Example_ImageFilter extends Activity {

	Context mContext = null;

	private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 106;	

	private Button			mBackgroundImageBtn;
	private Button			mImageFilterBtn;	
	private Button			mFilterLevelBtn;	
	private ImageView		mBackgroudnImageView;
	private Bitmap	bmBackgroundBitmap = null;
	private Bitmap	bmFilteredBitmap = null;	
	private int mImageOperation = SPenImageFilterConstants.FILTER_ORIGINAL;
	private int mImageOperationIndex = 0;
	private int mImageOperationLevel = SPenImageFilterConstants.FILTER_LEVEL_MEDIUM;	
	private int mImageOperationLevelIndex = 2;

	private ArrayAdapter<CharSequence> arrayAdapter ;
	private ListView filterlistView;
	boolean bShowListView = false;


	// Conversion array to map menu index to Image Filter Index	
	private int[] imageOperationByIndex = {SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_GRAY, SPenImageFilterConstants.FILTER_SEPIA, SPenImageFilterConstants.FILTER_NEGATIVE, 
			SPenImageFilterConstants.FILTER_BRIGHT, SPenImageFilterConstants.FILTER_DARK, SPenImageFilterConstants.FILTER_VINTAGE, SPenImageFilterConstants.FILTER_OLDPHOTO, 
			SPenImageFilterConstants.FILTER_FADEDCOLOR, SPenImageFilterConstants.FILTER_VIGNETTE, SPenImageFilterConstants.FILTER_VIVID, SPenImageFilterConstants.FILTER_COLORIZE, 
			SPenImageFilterConstants.FILTER_BLUR, SPenImageFilterConstants.FILTER_PENCILSKETCH, SPenImageFilterConstants.FILTER_FUSAIN, SPenImageFilterConstants.FILTER_PENSKETCH, 
			SPenImageFilterConstants.FILTER_PASTELSKETCH, SPenImageFilterConstants.FILTER_COLORSKETCH, SPenImageFilterConstants.FILTER_PENCILPASTELSKETCH, SPenImageFilterConstants.FILTER_PENCILCOLORSKETCH, 
			SPenImageFilterConstants.FILTER_RETRO, SPenImageFilterConstants.FILTER_SUNSHINE, SPenImageFilterConstants.FILTER_DOWNLIGHT, SPenImageFilterConstants.FILTER_BLUEWASH,
			SPenImageFilterConstants.FILTER_NOSTALGIA, SPenImageFilterConstants.FILTER_YELLOWGLOW, SPenImageFilterConstants.FILTER_SOFTGLOW, SPenImageFilterConstants.FILTER_MOSAIC, 
			SPenImageFilterConstants.FILTER_POPART, SPenImageFilterConstants.FILTER_MAGICPEN, SPenImageFilterConstants.FILTER_OILPAINT, SPenImageFilterConstants.FILTER_POSTERIZE, 
			SPenImageFilterConstants.FILTER_CARTOONIZE,SPenImageFilterConstants.FILTER_CLASSIC};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.general_purpose_image_filter);

		mContext = this;

		mImageFilterBtn = (Button) findViewById(R.id.imagefilter);
		mImageFilterBtn.setOnClickListener(imagefilterBtnClickListener);
		mBackgroundImageBtn = (Button) findViewById(R.id.backgroundimage);
		mBackgroundImageBtn.setOnClickListener(backgroundimageBtnClickListener);
		mFilterLevelBtn = (Button) findViewById(R.id.filterlevel);
		mFilterLevelBtn.setOnClickListener(filterlevelBtnClickListener);

		mBackgroudnImageView = (ImageView) findViewById(R.id.imageview_background);


		arrayAdapter = ArrayAdapter.createFromResource(this, R.array.imageoperation, R.layout.general_purpose_filter_list); 

		filterlistView = (ListView)findViewById(R.id.filter_list);
		filterlistView.setAdapter(arrayAdapter);        
		filterlistView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// Don't show list view
		bShowListView = false;
		filterlistView.setVisibility(View.GONE);

		// initial background
		bmBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
		mBackgroudnImageView.setImageBitmap(bmBackgroundBitmap);

		filterlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id) {

				if(bmBackgroundBitmap==null)
				{
					Toast.makeText(mContext, "Input image does not selected!, select Image firtsly", Toast.LENGTH_SHORT).show();  
					return;
				}	

				mImageOperationIndex = position;				
				mImageOperation = imageOperationByIndex[mImageOperationIndex];	

				if(mImageOperation==SPenImageFilterConstants.FILTER_ORIGINAL)
				{						
					mBackgroudnImageView.setImageBitmap(bmBackgroundBitmap);  	    					
				}
				else
				{						
					bmFilteredBitmap = SPenImageFilter.filterImageCopy(bmBackgroundBitmap, mImageOperation, mImageOperationLevel);
					if(bmFilteredBitmap!=null){    			
						mBackgroudnImageView.setImageBitmap(bmFilteredBitmap);    				
					}			
					else
					{
						Toast.makeText(mContext, "Fail to apply image filter", Toast.LENGTH_LONG).show();  
					}			  				  	
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	}

	//set image filter
	private OnClickListener imagefilterBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mImageFilterBtn)) {
				//make image filter dialog
				bShowListView = !bShowListView;
				if(bShowListView) {
					filterlistView.setVisibility(View.VISIBLE);
					mImageFilterBtn.setText("Select Filter");					
				}					
				else 
				{
					filterlistView.setVisibility(View.GONE);	
					mImageFilterBtn.setText("Show Filter List");					
				}
			}
		}
	};


	// set background image
	private OnClickListener backgroundimageBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mBackgroundImageBtn)) {	
				callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);
			}
		}
	};


	//set image filter
	private OnClickListener filterlevelBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mFilterLevelBtn)) {
				//make image filter dialog
				showSetFilterLevelMenu();
			}
		}
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode==RESULT_OK) {
			if(data == null)
				return;

			if(requestCode == REQUEST_CODE_SELECT_IMAGE_BACKGROUND) {    			
				Uri imageFileUri = data.getData();
				String strBackgroundImagePath = SPenSDKUtils.getRealPathFromURI(this, imageFileUri);

				// Check Valid Image File
				if(!SPenSDKUtils.isValidImagePath(strBackgroundImagePath))
				{
					Toast.makeText(this, "Invalid image path or web image", Toast.LENGTH_LONG).show();	
					return;
				}    			

				// Set background image on the image view
				bmBackgroundBitmap = SPenSDKUtils.getSafeResizingBitmap(strBackgroundImagePath, mBackgroudnImageView.getWidth()/2, mBackgroudnImageView.getHeight()/2, true);
				if(bmBackgroundBitmap!=null){    			
					mBackgroudnImageView.setImageBitmap(bmBackgroundBitmap);    				
				}
				else
					Toast.makeText(mContext, "Fail to set Background Image Path.", Toast.LENGTH_LONG).show();    			
			}			
		}
	}	


	//==================================================
	// Call Gallery
	//==================================================
	private void callGalleryForInputImage(int nRequestCode) {
		try {
			Intent galleryIntent;
			galleryIntent = new Intent(); 
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);				
			galleryIntent.setType("image/*");
			galleryIntent.setClassName("com.cooliris.media", "com.cooliris.media.Gallery");
			startActivityForResult(galleryIntent, nRequestCode);
		} catch(ActivityNotFoundException e) {
			Intent galleryIntent;
			galleryIntent = new Intent();
			galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
			galleryIntent.setType("image/*");
			startActivityForResult(galleryIntent, nRequestCode);
			e.printStackTrace();
		}		
	}


	private void showSetFilterLevelMenu(){	   

		if(bmBackgroundBitmap==null)
		{
			Toast.makeText(mContext, "Input image does not selected!, select Image firtsly", Toast.LENGTH_SHORT).show();  
			return;
		}	
		AlertDialog dlg = new AlertDialog.Builder(this)		
		.setIcon(getResources().getDrawable(android.R.drawable.ic_dialog_alert))
		.setTitle("Set Filter Level")	    	
		.setSingleChoiceItems(R.array.imageoperation_level, mImageOperationLevelIndex, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {		    			
				mImageOperationLevelIndex = whichButton;
				mImageOperationLevel = mImageOperationLevelIndex; 

				if(mImageOperation!=SPenImageFilterConstants.FILTER_ORIGINAL)					
				{
					bmFilteredBitmap = SPenImageFilter.filterImageCopy(bmBackgroundBitmap, mImageOperation, mImageOperationLevel);
					if(bmFilteredBitmap!=null){    			
						mBackgroudnImageView.setImageBitmap(bmFilteredBitmap);    				
					}			
					else
					{
						Toast.makeText(mContext, "Fail to apply image filter", Toast.LENGTH_LONG).show();  
					}		
				}
				dialog.dismiss();
			}
		})
		.create();
		dlg.show();
	}
}
