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

public class PreferencesOfAnimationOption extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//==============================
	// Preference Key Constants 
	//==============================

	public static final String 		PREF_KEY_PLAY_ANIMATIONSPEED = "samm_animation_speed";
	public static final String 		PREF_KEY_PLAY_ANIMATIONSUSINGTRANSPARENTBACKGROUND = "play_animation_using_transparent_background";
	public static final String 		PREF_KEY_PLAY_BACKGROUNDAUDIO = "background_audio_play";
	public static final String 		PREF_KEY_PLAY_BACKGROUNDAUDIOREPLAY = "background_audio_auto_replay";
	public static final String 		PREF_KEY_PLAY_BACKGROUNDAUDIOSTOP = "background_audio_play_stop";

	public static final String 		PREF_KEY_PLAY_ANIMATIONSUSINGSOUNDEFFECT = "play_animation_using_sound_effect";

	private ListPreference mListPreferenceAnimationSpeed;
	private CheckBoxPreference mCheckPreferencePlayAnimationUsingTransparentBackground;
	private CheckBoxPreference mCheckPreferencePlayBackgroundAudio;
	private CheckBoxPreference mCheckPreferencePlayBackgroundAudioReplay;
	private CheckBoxPreference mCheckPreferencePlayBackgroundAudioStop;

	private CheckBoxPreference mCheckPreferencePlayAnimationUsingSoundEffect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.animationoptionpreferences);

		mListPreferenceAnimationSpeed = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_PLAY_ANIMATIONSPEED);
		mCheckPreferencePlayAnimationUsingTransparentBackground = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PLAY_ANIMATIONSUSINGTRANSPARENTBACKGROUND);
		mCheckPreferencePlayBackgroundAudio = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PLAY_BACKGROUNDAUDIO);
		mCheckPreferencePlayBackgroundAudioReplay = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PLAY_BACKGROUNDAUDIOREPLAY);
		mCheckPreferencePlayBackgroundAudioStop = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PLAY_BACKGROUNDAUDIOSTOP);

		mCheckPreferencePlayAnimationUsingSoundEffect = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PLAY_ANIMATIONSUSINGSOUNDEFFECT);

		updatePreferences();
	}

	public void updatePreferences()
	{
		updatePreference(PREF_KEY_PLAY_ANIMATIONSPEED);
		updatePreference(PREF_KEY_PLAY_ANIMATIONSUSINGTRANSPARENTBACKGROUND);
		updatePreference(PREF_KEY_PLAY_BACKGROUNDAUDIO);
		updatePreference(PREF_KEY_PLAY_BACKGROUNDAUDIOREPLAY);
		updatePreference(PREF_KEY_PLAY_BACKGROUNDAUDIOSTOP);
		updatePreference(PREF_KEY_PLAY_ANIMATIONSUSINGSOUNDEFFECT);
	}

	public void updatePreference(String key)
	{
		// Preference change
		String str; 
		if (key.equals(PREF_KEY_PLAY_ANIMATIONSPEED)) {
			str = mListPreferenceAnimationSpeed.getValue();
			if(str==null)	
			{
				mListPreferenceAnimationSpeed.setValueIndex(4);
				str = mListPreferenceAnimationSpeed.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.animation_speed);
			mListPreferenceAnimationSpeed.setSummary(strings[nSelectIndex]);
		}
		else if (key.equals(PREF_KEY_PLAY_ANIMATIONSUSINGTRANSPARENTBACKGROUND)) {
			if(!mCheckPreferencePlayAnimationUsingTransparentBackground.isChecked())
				mCheckPreferencePlayAnimationUsingTransparentBackground.setSummaryOff(getResources().getString(R.string.play_animation_using_trasparent_background_off));
			else
				mCheckPreferencePlayAnimationUsingTransparentBackground.setSummaryOn(getResources().getString(R.string.play_animation_using_trasparent_background_on));
		}
		else if (key.equals(PREF_KEY_PLAY_BACKGROUNDAUDIO)) {
			if(!mCheckPreferencePlayBackgroundAudio.isChecked())
				mCheckPreferencePlayBackgroundAudio.setSummaryOff(getResources().getString(R.string.background_audio_play_off));
			else
				mCheckPreferencePlayBackgroundAudio.setSummaryOn(getResources().getString(R.string.background_audio_play_on));
		}
		else if (key.equals(PREF_KEY_PLAY_BACKGROUNDAUDIOREPLAY)) {
			if(!mCheckPreferencePlayBackgroundAudioReplay.isChecked())
				mCheckPreferencePlayBackgroundAudioReplay.setSummaryOff(getResources().getString(R.string.background_audio_auto_replay_off));
			else
				mCheckPreferencePlayBackgroundAudioReplay.setSummaryOn(getResources().getString(R.string.background_audio_auto_replay_on));
		}
		else if (key.equals(PREF_KEY_PLAY_BACKGROUNDAUDIOSTOP)) {
			if(!mCheckPreferencePlayBackgroundAudioStop.isChecked())
				mCheckPreferencePlayBackgroundAudioStop.setSummaryOff(getResources().getString(R.string.background_audio_stop_off));
			else
				mCheckPreferencePlayBackgroundAudioStop.setSummaryOn(getResources().getString(R.string.background_audio_stop_on));
		}
		else if (key.equals(PREF_KEY_PLAY_ANIMATIONSUSINGSOUNDEFFECT)) {
			if(!mCheckPreferencePlayAnimationUsingSoundEffect.isChecked())
				mCheckPreferencePlayAnimationUsingSoundEffect.setSummaryOff(getResources().getString(R.string.play_animation_using_sound_effect_off));
			else
				mCheckPreferencePlayAnimationUsingSoundEffect.setSummaryOn(getResources().getString(R.string.play_animation_using_sound_effect_on));
		}
	}

	// Return Play Animation Speed 
	public static final int getPreferencePlayAnimationSpeed(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strPlayAnimationSpeedPref = prefs.getString(PREF_KEY_PLAY_ANIMATIONSPEED, "4");	// default
		return Integer.parseInt(strPlayAnimationSpeedPref);
		//		int nIndex = Integer.parseInt(strPlayAnimationSpeedPref);		
		//		int nPlayAnimationSpeed[] = {0, 1, 2, 3, 4};
		//		return nPlayAnimationSpeed[nIndex];
	}

	public static void setPreferencePlayAnimationSpeed(Context context, int nSpeed)
	{			
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(PREF_KEY_PLAY_ANIMATIONSPEED, Integer.toString(nSpeed));
		prefsEditor.commit();		
	}

	// Play animation into Transparency background or not(Not yet set Background)
	public static final boolean getPreferencePlayAnimationUsingTransparentBackground(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PLAY_ANIMATIONSUSINGTRANSPARENTBACKGROUND, false);	// default
	}

	// background audio play or not(including file, voice recording)
	public static final boolean getPreferencePlayBackgroundAudio(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PLAY_BACKGROUNDAUDIO, true);	// default
	}

	// Background Audio play the repeat or not(including file, voice recording)
	public static final boolean getPreferencePlayBackgroundAudioReplay(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PLAY_BACKGROUNDAUDIOREPLAY, false);	// default
	}

	// When Animation Play is stop, Background Audio play stop or not
	public static final boolean getPreferencePlayBackgroundAudioStop(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PLAY_BACKGROUNDAUDIOSTOP, true);	// default
	}

	// When Animation Play, Setting of voice effect or not 
	public static final boolean getPreferencePlayAnimationUsingSoundEffect(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PLAY_ANIMATIONSUSINGSOUNDEFFECT, true);	// default
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
