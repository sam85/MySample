/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samsung.spensdk.example.tools;

import com.samsung.spensdk.example.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesOfSAMMOption extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//==============================
	// Preference Key Constants 
	//==============================
	public static final String 		PREF_KEY_LOAD_CANVAS_SIZE = "samm_load_canvas_size";
	public static final String 		PREF_KEY_LOAD_CANVAS_HALIGN = "samm_load_canvas_halign";
	public static final String 		PREF_KEY_LOAD_CANVAS_VALIGN = "samm_load_canvas_valign";
	
	public static final String 		PREF_KEY_SAVE_CROP_IMAGE_HORIZONTAL = "samm_save_crop_image_horizontal";
	public static final String 		PREF_KEY_SAVE_CROP_IMAGE_VERTICAL = "samm_save_crop_image_vertical";
	public static final String 		PREF_KEY_SAVE_CROP_CONTENTS = "samm_save_crop_contents";
	
	public static final String 		PREF_KEY_SAVE_IMAGESIZE = "samm_save_image_size";
	public static final String 		PREF_KEY_SAVE_IMAGEQUALITY = "samm_save_image_quality";
	public static final String 		PREF_KEY_SAVE_ONLYFOREGROUNDIMAGE = "samm_save_only_foreground_image";
	public static final String 		PREF_KEY_SAVE_CREATENEWIMAGEFILE = "samm_save_create_new_image_file";
	public static final String 		PREF_KEY_SAVE_ENCODEFOREGROUNDIMAGE = "samm_encode_foreground_image";
	public static final String 		PREF_KEY_SAVE_ENCODETHUMBNAILIMAGE = "samm_encode_thumbnail_image";
	
	private ListPreference mListPreferenceLoadCanvasSize;
	private ListPreference mListPreferenceLoadCanvasHAlign;
	private ListPreference mListPreferenceLoadCanvasVAlign;
	
	private CheckBoxPreference mCheckPreferenceSaveCropImageHorizontal;
	private CheckBoxPreference mCheckPreferenceSaveCropImageVertical;
	private CheckBoxPreference mCheckPreferenceSaveCropContents;
	
	private ListPreference mListPreferenceSaveImageSize;
	private ListPreference mListPreferenceSaveImageQuality;	
	private CheckBoxPreference mCheckPreferenceSaveOnlyForegroundImage;
	private CheckBoxPreference mCheckPreferenceSaveCreateNewImageFile;
	private CheckBoxPreference mCheckPreferenceEncodeForegroundImage;
	private CheckBoxPreference mCheckPreferenceEncodeThumbnailImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.sammoptionpreferences);

        mListPreferenceLoadCanvasSize = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_LOAD_CANVAS_SIZE);
        mListPreferenceLoadCanvasHAlign = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_LOAD_CANVAS_HALIGN);
        mListPreferenceLoadCanvasVAlign = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_LOAD_CANVAS_VALIGN);
        
        mCheckPreferenceSaveCropImageHorizontal = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_CROP_IMAGE_HORIZONTAL);
        mCheckPreferenceSaveCropImageVertical = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_CROP_IMAGE_VERTICAL);
        mCheckPreferenceSaveCropContents = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_CROP_CONTENTS);
		mListPreferenceSaveImageSize = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_IMAGESIZE);
		mListPreferenceSaveImageQuality = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_IMAGEQUALITY);
		mCheckPreferenceSaveOnlyForegroundImage = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_ONLYFOREGROUNDIMAGE);
		mCheckPreferenceSaveCreateNewImageFile = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_CREATENEWIMAGEFILE);
		mCheckPreferenceEncodeForegroundImage = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_ENCODEFOREGROUNDIMAGE);
		mCheckPreferenceEncodeThumbnailImage = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SAVE_ENCODETHUMBNAILIMAGE);

		updatePreferences();
	}

	public void updatePreferences()
	{
		updatePreference(PREF_KEY_LOAD_CANVAS_SIZE);
		updatePreference(PREF_KEY_LOAD_CANVAS_HALIGN);
		updatePreference(PREF_KEY_LOAD_CANVAS_VALIGN);		
		updatePreference(PREF_KEY_SAVE_CROP_IMAGE_HORIZONTAL);
		updatePreference(PREF_KEY_SAVE_CROP_IMAGE_VERTICAL);
		updatePreference(PREF_KEY_SAVE_CROP_CONTENTS);
		updatePreference(PREF_KEY_SAVE_IMAGESIZE);
		updatePreference(PREF_KEY_SAVE_IMAGEQUALITY);
		updatePreference(PREF_KEY_SAVE_ONLYFOREGROUNDIMAGE);
		updatePreference(PREF_KEY_SAVE_CREATENEWIMAGEFILE);
		updatePreference(PREF_KEY_SAVE_ENCODEFOREGROUNDIMAGE);
		updatePreference(PREF_KEY_SAVE_ENCODETHUMBNAILIMAGE);
	}

	public void updatePreference(String key)
	{
		// Preference change
		String str; 
		if (key.equals(PREF_KEY_LOAD_CANVAS_SIZE)) {
			str = mListPreferenceLoadCanvasSize.getValue();
			if(str==null)	
			{
				mListPreferenceLoadCanvasSize.setValueIndex(1);
				str = mListPreferenceLoadCanvasSize.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.load_canvas_size);
			mListPreferenceLoadCanvasSize.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_LOAD_CANVAS_HALIGN)) {
			str = mListPreferenceLoadCanvasHAlign.getValue();
			if(str==null)	
			{
				mListPreferenceLoadCanvasHAlign.setValueIndex(0);
				str = mListPreferenceLoadCanvasHAlign.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.load_canvas_halign);
			mListPreferenceLoadCanvasHAlign.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_LOAD_CANVAS_VALIGN)) {
			str = mListPreferenceLoadCanvasVAlign.getValue();
			if(str==null)	
			{
				mListPreferenceLoadCanvasVAlign.setValueIndex(0);
				str = mListPreferenceLoadCanvasVAlign.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.load_canvas_valign);
			mListPreferenceLoadCanvasVAlign.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_SAVE_CROP_IMAGE_HORIZONTAL)) {
			if(!mCheckPreferenceSaveCropImageHorizontal.isChecked())
				mCheckPreferenceSaveCropImageHorizontal.setSummaryOff(getResources().getString(R.string.save_image_horizontal_crop_off));
			else
				mCheckPreferenceSaveCropImageHorizontal.setSummaryOn(getResources().getString(R.string.save_image_horizontal_crop_on));
		}
		else if (key.equals(PREF_KEY_SAVE_CROP_IMAGE_VERTICAL)) {
			if(!mCheckPreferenceSaveCropImageVertical.isChecked())
				mCheckPreferenceSaveCropImageVertical.setSummaryOff(getResources().getString(R.string.save_image_vertical_crop_off));
			else
				mCheckPreferenceSaveCropImageVertical.setSummaryOn(getResources().getString(R.string.save_image_vertical_crop_on));
		}
		else if (key.equals(PREF_KEY_SAVE_CROP_CONTENTS)) {
			if(!mCheckPreferenceSaveCropContents.isChecked())
				mCheckPreferenceSaveCropContents.setSummaryOff(getResources().getString(R.string.adjust_saving_contents_by_cropping_off));
			else
				mCheckPreferenceSaveCropContents.setSummaryOn(getResources().getString(R.string.adjust_saving_contents_by_cropping_on));
		}
		else if (key.equals(PREF_KEY_SAVE_IMAGESIZE)) {
			str = mListPreferenceSaveImageSize.getValue();
			if(str==null)	
			{
				mListPreferenceSaveImageSize.setValueIndex(0);
				str = mListPreferenceSaveImageSize.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.save_image_size);
			mListPreferenceSaveImageSize.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_SAVE_IMAGEQUALITY)) {
			str = mListPreferenceSaveImageQuality.getValue();
			if(str==null)	
			{
				mListPreferenceSaveImageQuality.setValueIndex(0);
				str = mListPreferenceSaveImageQuality.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.save_image_quality);
			mListPreferenceSaveImageQuality.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_SAVE_ONLYFOREGROUNDIMAGE)) {
			if(!mCheckPreferenceSaveOnlyForegroundImage.isChecked())
				mCheckPreferenceSaveOnlyForegroundImage.setSummaryOff(getResources().getString(R.string.save_only_foreground_image_off));
			else
				mCheckPreferenceSaveOnlyForegroundImage.setSummaryOn(getResources().getString(R.string.save_only_foreground_image_on));
		}
		else if (key.equals(PREF_KEY_SAVE_CREATENEWIMAGEFILE)) {
			if(!mCheckPreferenceSaveCreateNewImageFile.isChecked())
				mCheckPreferenceSaveCreateNewImageFile.setSummaryOff(getResources().getString(R.string.create_new_image_off));
			else
				mCheckPreferenceSaveCreateNewImageFile.setSummaryOn(getResources().getString(R.string.create_new_image_on));
		}
		else if (key.equals(PREF_KEY_SAVE_ENCODEFOREGROUNDIMAGE)) {
			if(!mCheckPreferenceEncodeForegroundImage.isChecked())
				mCheckPreferenceEncodeForegroundImage.setSummaryOff(getResources().getString(R.string.encode_foreground_image_off));
			else
				mCheckPreferenceEncodeForegroundImage.setSummaryOn(getResources().getString(R.string.encode_foreground_image_on));
		}
		else if (key.equals(PREF_KEY_SAVE_ENCODETHUMBNAILIMAGE)) {
			if(!mCheckPreferenceEncodeThumbnailImage.isChecked())
				mCheckPreferenceEncodeThumbnailImage.setSummaryOff(getResources().getString(R.string.encode_thumbnail_image_off));
			else
				mCheckPreferenceEncodeThumbnailImage.setSummaryOn(getResources().getString(R.string.encode_thumbnail_image_on));
		}
	}

	// Return Load Canvas Size converting option
	public static final int getPreferenceLoadCanvasSize(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strLoadCanvasSizePref = prefs.getString(PREF_KEY_LOAD_CANVAS_SIZE, "0");	// default
		return Integer.parseInt(strLoadCanvasSizePref);
	}
	// Return Load Canvas Align converting option
	public static final int getPreferenceLoadCanvasHAlign(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strLoadCanvasHAlignPref = prefs.getString(PREF_KEY_LOAD_CANVAS_HALIGN, "1");	// default
		return Integer.parseInt(strLoadCanvasHAlignPref);
	}
	// Return Load Canvas Align converting option
	public static final int getPreferenceLoadCanvasVAlign(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strLoadCanvasVAlignPref = prefs.getString(PREF_KEY_LOAD_CANVAS_VALIGN, "0");	// default
		return Integer.parseInt(strLoadCanvasVAlignPref);
	}
	
	public static final boolean getPreferenceSaveImageHorizontalCrop(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_CROP_IMAGE_HORIZONTAL, false);	// default
	}
	
	public static final boolean getPreferenceSaveImageVerticalCrop(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_CROP_IMAGE_VERTICAL, false);	// default
	}
	
	public static final boolean getPreferenceSaveContentsCrop(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_CROP_CONTENTS, true);	// default
	}
	
	// Return saved image size.
	public static final int getPreferenceSaveImageSize(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strSaveImageSizePref = prefs.getString(PREF_KEY_SAVE_IMAGESIZE, "1");	// default
		return Integer.parseInt(strSaveImageSizePref);
		//		int nIndex = Integer.parseInt(strSaveImageSizePref);		
		//		int nSaveImageSize[] = {0, 1, 2, 3};
		//		return nSaveImageSize[nIndex];
	}

	// Return saved image quality.
	public static final int getPreferenceSaveImageQuality(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strSaveImageQualityPref = prefs.getString(PREF_KEY_SAVE_IMAGEQUALITY, "1");	// default
		return Integer.parseInt(strSaveImageQualityPref);
		//		int nIndex = Integer.parseInt(strSaveImageQualityPref);		
		//		int nSaveImageQuality[] = {0, 1, 2};
		//		return nSaveImageQuality[nIndex];
	}

	// When image create, including background or not.
	public static final boolean getPreferenceSaveOnlyForegroundImage(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_ONLYFOREGROUNDIMAGE, false);	// default
	}
	
	// When save SAMM data, create new image file.
	public static final boolean getPreferenceSaveCreateNewImageFile(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_CREATENEWIMAGEFILE, true);	// default
	}
	
	// Encode foreground image
	public static final boolean getPreferenceEncodeForegroundImageFile(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_ENCODEFOREGROUNDIMAGE, true);	// default
	}
	
	// Encode thumbnail image
	public static final boolean getPreferenceEncodeThumbnailImageFile(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SAVE_ENCODETHUMBNAILIMAGE, true);	// default
	}

	@Override
	protected void onResume() {
		super.onResume();		
		//  Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);        
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}    

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		updatePreference(key);		
	}

}
