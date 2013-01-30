/*
 ********************************************************************************
 * Copyright (c) 2012 Samsung Electronics, Inc.
 * All rights reserved.
 *
 * This software is a confidential and proprietary information of Samsung
 * Electronics, Inc. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Samsung Electronics.
 ********************************************************************************
 */
package com.sprc.circlelauncher;

import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

/**
 * List where are selected application which will be shown on launcher.
 * 
 * @author sprc.
 * 
 */
public class ChooseAppsActivity extends ListActivity {

	private static final int ICON_SIZE = 72;
	private static final int ICON_PADDING = 10;

	private List<ResolveInfo> mApplications;

	private PackageManager mPackageManager;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		mPackageManager = getPackageManager();
		mApplications = mPackageManager.queryIntentActivities(intent,
				PackageManager.GET_META_DATA);
		Collections.sort(mApplications, new ResolveInfo.DisplayNameComparator(
				mPackageManager));
		setListAdapter(new ChoseAppListAdapter(this));

	}

	@Override
	protected void onListItemClick(final ListView l, final View v,
			final int position, final long id) {
		final ResolveInfo resolveInfo = (ResolveInfo) l.getAdapter().getItem(
				position);

		final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		final SharedPreferences.Editor editor = sharedPreferences.edit();
		final String key = resolveInfo.activityInfo.name;
		final CheckBox checkBox = (CheckBox) v
				.findViewById(R.id.add_app_to_launch);
		final boolean isChecked = checkBox.isChecked();
		editor.putBoolean(key, !isChecked);
		checkBox.setChecked(!isChecked);
		editor.commit();

	}

	/**
	 * Class which represents adapter.
	 * 
	 * @author sprc
	 * 
	 */
	class ChoseAppListAdapter extends BaseAdapter {

		private final LayoutInflater mLayoutInflater;

		public ChoseAppListAdapter(final Context pContext) {
			mLayoutInflater = LayoutInflater.from(pContext);
		}

		/**
		 * 
		 */
		public int getCount() {
			return mApplications.size();
		}

		public Object getItem(final int position) {
			return mApplications.get(position);
		}

		public long getItemId(final int position) {
			return position;
		}

		public View getView(final int position, final View convertView,
				final ViewGroup parent) {

			final ResolveInfo resolveInfo = (ResolveInfo) getItem(position);

			View v = convertView;
			if (v == null) {
				v = mLayoutInflater
						.inflate(R.layout.select_app_list_item, null);
			}

			final TextView appName = (TextView) v.findViewById(R.id.app_name);
			final CheckBox checkBox = (CheckBox) v
					.findViewById(R.id.add_app_to_launch);

			final Drawable icon = resolveInfo.loadIcon(mPackageManager);
			final Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();

			final Drawable iconCorect = new BitmapDrawable(getResources(),
					Bitmap.createScaledBitmap(bitmap, ICON_SIZE, ICON_SIZE,
							true));

			appName.setCompoundDrawablesWithIntrinsicBounds(iconCorect, null,
					null, null);
			appName.setText(resolveInfo.loadLabel(mPackageManager));

			final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			final String key = resolveInfo.activityInfo.name;
			final boolean isChecked = sharedPreferences.getBoolean(key, true);
			checkBox.setChecked(isChecked);

			appName.setCompoundDrawablePadding(ICON_PADDING);

			return v;
		}
	}
}
