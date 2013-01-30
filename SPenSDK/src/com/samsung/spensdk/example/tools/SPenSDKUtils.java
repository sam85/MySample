package com.samsung.spensdk.example.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.example.R;

public class SPenSDKUtils {

	/**
	 * Get the real file path from URI registered in media store 
	 * @param contentUri URI registered in media store 
	 */		
	public static   String getRealPathFromURI(Activity activity, Uri contentUri) { 		

		String releaseNumber = Build.VERSION.RELEASE;

		if(releaseNumber!=null){
			/* ICS Version */
			if(releaseNumber.length()>0 && releaseNumber.charAt(0)=='4'){
				String[] proj = { MediaStore.Images.Media.DATA };
				String strFileName="";
				CursorLoader cursorLoader = new CursorLoader(activity, contentUri, proj, null, null,null); 
				Cursor cursor = cursorLoader.loadInBackground();			
				if(cursor!=null)
				{
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);			
					cursor.moveToFirst();
					if(cursor.getCount()>0)
						strFileName=cursor.getString(column_index);

					cursor.close();
				}
				return strFileName; 	
			}			
			/* GB Version */
			else if(releaseNumber.startsWith("2.3")){
				String[] proj = { MediaStore.Images.Media.DATA };
				String strFileName="";
				Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null); 
				if(cursor!=null)
				{
					int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

					cursor.moveToFirst();
					if(cursor.getCount()>0)
						strFileName=cursor.getString(column_index);

					cursor.close();				
				}
				return strFileName; 	
			}	
		}

		//---------------------
		// Undefined Version
		//---------------------
		/* GB, ICS Common */
		String[] proj = { MediaStore.Images.Media.DATA }; 
		String strFileName="";
		Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null); 
		if(cursor!=null)
		{
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);			

			// Use the Cursor manager in ICS         
			activity.startManagingCursor(cursor);

			cursor.moveToFirst();
			if(cursor.getCount()>0)
				strFileName=cursor.getString(column_index);

			//cursor.close(); // If the cursor close use , This application is terminated .(In ICS Version)
			activity.stopManagingCursor(cursor);
		}
		return strFileName; 		
	}


	/**
	 * Save jpeg image with background color
	 * @param strFileName Save file path
	 * @param bitmap Input bitmap
	 * @param nQuality Jpeg quality for saving
	 * @param nBackgroundColor background color
	 * @return whether success or not
	 */	
	public static boolean saveBitmapJPEGWithBackgroundColor(String strFileName, Bitmap bitmap, int nQuality, int nBackgroundColor)
	{		
		boolean bSuccess1 = false;
		boolean bSuccess2 = false;	
		boolean bSuccess3;		
		File saveFile = new File(strFileName);			

		if(saveFile.exists()) {
			if(!saveFile.delete())
				return false;
		}

		int nA = (nBackgroundColor>>24)&0xff;

		// If Background color alpha is 0, Background color substitutes as white
		if(nA==0)
			nBackgroundColor = 0xFFFFFFFF;

		Rect rect = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(nBackgroundColor);		
		canvas.drawBitmap(bitmap, rect, rect, new Paint());

		// Quality limitation min/max
		if(nQuality<10) nQuality = 10;
		else if(nQuality>100) nQuality = 100;

		OutputStream out = null;

		try {
			bSuccess1 = saveFile.createNewFile();			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			out = new FileOutputStream(saveFile);
			bSuccess2 = newBitmap.compress(CompressFormat.JPEG, nQuality, out);				
		} catch (Exception e) {
			e.printStackTrace();			
		}


		try {
			if(out!=null)
			{
				out.flush();
				out.close();
				bSuccess3 = true;
			}
			else
				bSuccess3 = false;

		} catch (IOException e) {
			e.printStackTrace();
			bSuccess3 = false;
		}finally
		{
			if(out != null)
			{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
		}		

		return (bSuccess1 && bSuccess2 && bSuccess3);		
	}



	/**
	 * Save PNG image with background color
	 * @param strFileName Save file path
	 * @param bitmap Input bitmap
	 * @param nQuality Jpeg quality for saving
	 * @param nBackgroundColor background color
	 * @return whether success or not
	 */	
	public static boolean saveBitmapPNGWithBackgroundColor(String strFileName, Bitmap bitmap, int nBackgroundColor)
	{		
		boolean bSuccess1 = false;
		boolean bSuccess2 = false;	
		boolean bSuccess3;		
		File saveFile = new File(strFileName);			

		if(saveFile.exists()) {
			if(!saveFile.delete())
				return false;
		}

		int nA = (nBackgroundColor>>24)&0xff;

		// If Background color alpha is 0, Background color substitutes as white
		if(nA==0)
			nBackgroundColor = 0xFFFFFFFF;

		Rect rect = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(nBackgroundColor);		
		canvas.drawBitmap(bitmap, rect, rect, new Paint());	

		OutputStream out = null;

		try {
			bSuccess1 = saveFile.createNewFile();			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			out = new FileOutputStream(saveFile);
			bSuccess2 = newBitmap.compress(CompressFormat.PNG, 100, out);				
		} catch (Exception e) {
			e.printStackTrace();			
		}


		try {
			if(out!=null)
			{
				out.flush();
				out.close();
				bSuccess3 = true;
			}
			else
				bSuccess3 = false;

		} catch (IOException e) {
			e.printStackTrace();
			bSuccess3 = false;
		}finally
		{
			if(out != null)
			{
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}			
		}		

		return (bSuccess1 && bSuccess2 && bSuccess3);		
	}

	// Check whether valid image file or not
	public static boolean isValidImagePath(String strImagePath){		
		if(strImagePath==null){			
			return false;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(strImagePath, options);

		return (options.outMimeType != null);	
	}

	// Check whether valid file name or not
	public static  boolean isValidSaveName(String fileName) {

		int len = fileName.length();
		for (int i = 0; i < len; i++) {
			char c = fileName.charAt(i);

			if(c== '\\'|| c== ':' || c== '/' || c== '*' || c== '?' || c== '"'  
				|| c== '<' || c== '>' || c== '|' || c== '\t'|| c== '\n') {
				return false;
			}
		}
		return true;
	}

	/****************************************************************************************************************
	 * Get the image bitmap that resizing to maximum size of limit.
	 * Parameter :
	 *  - context : Context 
	 *  - uri : Image URI
	 *  - bContentStreamImage : Gallery contents stream file(true)/file path(false)
	 *  - nMaxResizedWidth : The maximum allowable width of resizing image.
	 *  - nMaxResizedHeight : The maximum allowable height of resizing image.
	 * Return :
	 *  - Resizing bitmap
	 */
	public static Bitmap getSafeResizingBitmap(	
			String strImagePath, 
			int nMaxResizedWidth,	
			int nMaxResizedHeight, 
			boolean checkOrientation)
	{
		//==========================================
		// Bitmap Option
		//==========================================
		BitmapFactory.Options options = getBitmapSize(strImagePath);
		if(options == null)		
			return null;		

		//==========================================
		// Bitmap Scaling
		//==========================================
		int nSampleSize;
		int degree = 0;
		if(checkOrientation){
			degree = getExifDegree(strImagePath);
		}
		
		if(degree==90 || degree==270){
			nSampleSize = getSafeResizingSampleSize(options.outHeight, options.outWidth, nMaxResizedWidth, nMaxResizedHeight);
		}
		else{
			nSampleSize = getSafeResizingSampleSize(options.outWidth, options.outHeight, nMaxResizedWidth, nMaxResizedHeight);
		}
		
		
		//==========================================
		// Load the bitmap including actually data.
		//==========================================
		options.inJustDecodeBounds = false;
		options.inSampleSize = nSampleSize;
		options.inDither = false;		
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;	
		options.inPurgeable = true;

		Bitmap photo = BitmapFactory.decodeFile(strImagePath, options);
		if(checkOrientation 
		&& (degree==90 || degree==270) ){
			return getRotatedBitmap(photo, degree);
		}
		return photo;		
	}
	
	
	
	public static Bitmap decodeImageFile(String strImagePath, BitmapFactory.Options options, boolean checkOrientation){
		if(checkOrientation==false){
			return BitmapFactory.decodeFile(strImagePath, options);
		}
		else{
			Bitmap bm = BitmapFactory.decodeFile(strImagePath, options);
			int degree = getExifDegree(strImagePath);
			return getRotatedBitmap(bm, degree);
		}
	}
	
	public static BitmapFactory.Options getBitmapSize(String strImagePath)
	{
		//==========================================
		// Loaded the temporary bitmap for getting size.
		//==========================================
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//Bitmap photo = BitmapFactory.decodeFile(strPath, options);
		BitmapFactory.decodeFile(strImagePath, options);
		
		return options;
	}
	
	public static BitmapFactory.Options getBitmapSize(String strImagePath, boolean checkOrientation)
	{
		//==========================================
		// Loaded the temporary bitmap for getting size.
		//==========================================
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//Bitmap photo = BitmapFactory.decodeFile(strPath, options);
		BitmapFactory.decodeFile(strImagePath, options);
		
		if(checkOrientation){
			int degree = getExifDegree(strImagePath);
			if(degree==90 || degree==270){
				int temp = options.outWidth;
				options.outWidth = options.outHeight; 
				options.outHeight = temp;
			}
		}
		
		return options;		
	}

	/****************************************************************************************************************
	 * Get the sampling size for load the bitmap. (If you load the bitmap file of big size, This application is terminated.)
	 * Parameter :
	 *  - nOrgWidth	: The width of the original image (Value of outWidth of BitmapFactory.Options)
	 *  - nOrgHeight : The height of the original image  (Value of outHeight of BitmapFactory.Options)
	 *  - nMaxWidth : The width of the image of maximum size.  (width under 3M. ex.3000)
	 *  - nMaxHeight : The height of the image of maximum size.   (height under 3M. ex.1000)	 *  
	 * Return :
	 *  - Sampling size (If no need to resize, return 1). Throttled much larger.
	 *  - If more than x.5 times , divide x+1 times.
	 */
	public static int getSafeResizingSampleSize(
			int nOrgWidth, 
			int nOrgHeight,
			int nMaxWidth, 
			int nMaxHeight)
	{
		int size = 1;
		float fsize;
		float fWidthScale = 0;
		float fHeightScale = 0;

		if(nOrgWidth > nMaxWidth  || nOrgHeight > nMaxHeight ) 
		{
			if(nOrgWidth > nMaxWidth) 
				fWidthScale = (float)nOrgWidth / (float)nMaxWidth;					
			if(nOrgHeight > nMaxHeight) 
				fHeightScale = (float)nOrgHeight / (float)nMaxHeight;			

			if(fWidthScale >= fHeightScale) fsize = fWidthScale;
			else fsize= fHeightScale;

			size = (int)fsize;			
		}

		return size;
	}
	
	public static int getExifDegree(String filepath){
	    int degree = 0;
	    ExifInterface exif;
		try {
			exif = new ExifInterface(filepath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	    
	    
	    if (exif != null){
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	        
	        if (orientation != -1){
	            switch(orientation)	            {
	                case ExifInterface.ORIENTATION_ROTATE_90:
	                    degree = 90;
	                    break;
	                case ExifInterface.ORIENTATION_ROTATE_180:
	                    degree = 180;
	                    break;
	                case ExifInterface.ORIENTATION_ROTATE_270:
	                    degree = 270;
	                    break;
	            }
	        }
	    }	    
	    return degree;
	}
	
	public static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees)	{
	    if ( degrees != 0 && bitmap != null ) {
	        Matrix m = new Matrix();
	        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
	        try {
	            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
	            if (bitmap.equals(b2)){
	            	bitmap.recycle();
	            	bitmap = b2;
	            }
	        } 
	        catch (OutOfMemoryError ex){
	        	// TODO Auto-generated catch block
	        	ex.printStackTrace();
	        }
	    }
	    return bitmap;
	}
	
	
	
	public static HashMap<String, Integer> getSettingLayoutLocaleResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting, boolean bUseFillingSetting){
		//----------------------------------------
        // Resource Map for Layout & Locale
		//----------------------------------------
		HashMap<String,Integer> settingResourceMapInt = new HashMap<String, Integer>();
		// Layout 
		if(bUsePenSetting){
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_PEN_SPINNER, R.layout.mspinner);
		}
//		if(bUseEraserSetting){
//			
//		}
		if(bUseTextSetting){
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER, R.layout.mspinnertext);
			settingResourceMapInt.put(SCanvasConstants.LAYOUT_TEXT_SPINNER_TABLET, R.layout.mspinnertext_tablet);
		}
//		if(bUseFillingSetting){
//			
//		}
		
		//----------------------------------------
		// Locale(Multi-Language Support)	
		//----------------------------------------
		if(bUsePenSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_TITLE, R.string.pen_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EMPTY_MESSAGE, R.string.pen_settings_preset_empty);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_TITLE, R.string.pen_settings_preset_delete_title);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_DELETE_MESSAGE, R.string.pen_settings_preset_delete_msg);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_EXIST_MESSAGE, R.string.pen_settings_preset_exist);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_PEN_SETTING_PRESET_MAXIMUM_MESSAGE, R.string.pen_settings_preset_maximum_msg);
		}
		if(bUseEraserSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_TITLE, R.string.eraser_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_ERASER_SETTING_CLEARALL, R.string.clear_all);
		}
		if(bUseTextSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TITLE, R.string.text_settings);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_FONT, R.string.text_settings_tab_font);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH, R.string.text_settings_tab_paragraph);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXT_SETTING_TAB_PARAGRAPH_ALIGN, R.string.text_settings_tab_paragraph_align);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_TEXTBOX_HINT, R.string.textbox_hint);
			
			settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME1, R.string.user_font_name1);
			settingResourceMapInt.put(SCanvasConstants.LOCALE_USER_FONT_NAME2, R.string.user_font_name2);
		}
		if(bUseFillingSetting){
			settingResourceMapInt.put(SCanvasConstants.LOCALE_FILLING_SETTING_TITLE, R.string.filling_settings);
		}
		// common
		settingResourceMapInt.put(SCanvasConstants.LOCALE_SETTINGVIEW_CLOSE_DESCRIPTION, R.string.settingview_close_btn_desc);
	
		return settingResourceMapInt;
	}
	
	public static HashMap<String, String> getSettingLayoutStringResourceMap(boolean bUsePenSetting, boolean bUseEraserSetting, boolean bUseTextSetting, boolean bUseFillingSetting){
		HashMap<String,String> settingResourceMapString = new HashMap<String, String>();
		if(bUseTextSetting){
			// Resource Map for Custom font path
			settingResourceMapString = new HashMap<String, String>();
			settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH1, "fonts/chococooky.ttf");
			settingResourceMapString.put(SCanvasConstants.USER_FONT_PATH2, "fonts/rosemary.ttf");
		}
		
		// Set S Pen SDK Resource from Asset
		//settingResourceMapString.put(SCanvasConstants.CUSTOM_RESOURCE_ASSETS_PATH, "spen_sdk_resource");	// set folder of asstes/spen_sdk_resource
		
		return settingResourceMapString;
	}
	
	public static void alertActivityFinish(final Activity activity, String msg){
		AlertDialog.Builder ad = new AlertDialog.Builder(activity);		
		ad.setIcon(activity.getResources().getDrawable(android.R.drawable.ic_dialog_alert));	// Android Resource
		ad.setTitle(activity.getResources().getString(R.string.app_name))		
		.setMessage(msg)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {				
				// finish dialog
				dialog.dismiss();
				activity.finish();
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		})
		.show();
		ad = null;		
	}
}
