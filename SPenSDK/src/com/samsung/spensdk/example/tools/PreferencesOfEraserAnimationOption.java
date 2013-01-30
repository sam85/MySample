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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.samsung.spensdk.example.R;

public class PreferencesOfEraserAnimationOption extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//==============================
	// Preference Key Constants 
	//==============================

	public static final String 		PREF_KEY_IMAGE_OPERATION_LEVEL = "samm_image_operation_level";

	private ListPreference mListPreferenceImageOperationLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
//		addPreferencesFromResource(R.xml.eraseranimationoptionpreferences);

		mListPreferenceImageOperationLevel = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_IMAGE_OPERATION_LEVEL);

		updatePreferences();
	}

	public void updatePreferences()
	{
		updatePreference(PREF_KEY_IMAGE_OPERATION_LEVEL);
	}

	public void updatePreference(String key)
	{
		// Preference change
		String str; 
		if (key.equals(PREF_KEY_IMAGE_OPERATION_LEVEL)) {
			str = mListPreferenceImageOperationLevel.getValue();
			if(str==null)	
			{
				mListPreferenceImageOperationLevel.setValueIndex(2);
				str = mListPreferenceImageOperationLevel.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.imageoperation_level);
			mListPreferenceImageOperationLevel.setSummary(strings[nSelectIndex]);			
		}
	}

	// Return the Image Operation Level 
	public static final int getPreferenceImageOperationLevel(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strImageOperationLevel = prefs.getString(PREF_KEY_IMAGE_OPERATION_LEVEL, "2");	// default
		return Integer.parseInt(strImageOperationLevel);
		//		int nIndex = Integer.parseInt(strImageOperationLevel);		
		//		int nImageOperationLevel[] = {0, 1, 2, 3, 4};
		//		return nImageOperationLevel[nIndex];
	}

	public static void setPreferenceImageOperationLevel(Context context, int nImageOperationLevel)
	{			
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(PREF_KEY_IMAGE_OPERATION_LEVEL, Integer.toString(nImageOperationLevel));
		prefsEditor.commit();		
	}

	// When Animation Play, Setting of voice effect or not 

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
