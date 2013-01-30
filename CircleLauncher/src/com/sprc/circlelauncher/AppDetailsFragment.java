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

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A {@link Fragment} showing detailed application info.
 *
 * @author sprc
 *
 */
public class AppDetailsFragment extends Fragment {

	private ResolveInfo mResolveInfo;
	private PackageInfo mPackageInfo;
	private PackageManager mPackageManager;

	public static AppDetailsFragment newInstance(ResolveInfo resolveInfo) {

		AppDetailsFragment f = new AppDetailsFragment();
		f.setResolveInfo(resolveInfo);

		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mPackageManager = activity.getPackageManager();

		if (mPackageInfo == null) {
			try {
				mPackageInfo = mPackageManager.getPackageInfo(mResolveInfo.activityInfo.packageName, 0);
			} catch (NameNotFoundException e) {
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_appdetails, container, false);

		TextView title = (TextView) v.findViewById(R.id.text_title);
		title.setText(mResolveInfo.loadLabel(mPackageManager));

		// Set package name

		ViewGroup item = (ViewGroup) v.findViewById(R.id.item_package_name);
		TextView label = ((TextView) item.findViewById(R.id.label));
		label.setText(R.string.label_package_name);
		TextView text = ((TextView) item.findViewById(R.id.text));
		text.setText(mResolveInfo.activityInfo.packageName);

		// Set version name

		item = (ViewGroup) v.findViewById(R.id.item_version_name);

		if (mPackageInfo != null && isVersionNameValid(mPackageInfo.versionName)) {
			label = ((TextView) item.findViewById(R.id.label));
			label.setText(R.string.label_version_name);
			text = ((TextView) item.findViewById(R.id.text));
			text.setText(mPackageInfo.versionName);
		} else {
			item.setVisibility(View.GONE);
		}

		// Set version code

		item = (ViewGroup) v.findViewById(R.id.item_version_code);

		if (mPackageInfo != null && isVersionCodeValid(mPackageInfo.versionCode)) {
			label = ((TextView) item.findViewById(R.id.label));
			label.setText(R.string.label_version_code);
			text = ((TextView) item.findViewById(R.id.text));
			text.setText(Integer.toString(mPackageInfo.versionCode));
		} else {
			item.setVisibility(View.GONE);
		}

		// Set SDK version

		item = (ViewGroup) v.findViewById(R.id.item_target_sdk_version);

		if (isSdkVersionValid(mResolveInfo.activityInfo.applicationInfo.targetSdkVersion)) {
			label = ((TextView) item.findViewById(R.id.label));
			label.setText(R.string.label_target_sdk_version);
			text = ((TextView) item.findViewById(R.id.text));
			text.setText(Integer.toString(mResolveInfo.activityInfo.applicationInfo.targetSdkVersion));
		} else {
			item.setVisibility(View.GONE);
		}

		// Set UID

		item = (ViewGroup) v.findViewById(R.id.item_uid);
		label = ((TextView) item.findViewById(R.id.label));
		label.setText(R.string.label_uid);
		text = ((TextView) item.findViewById(R.id.text));
		text.setText(Integer.toString(mResolveInfo.activityInfo.applicationInfo.uid));

		return v;
	}

	public void setResolveInfo(ResolveInfo resolveInfo) {
		mResolveInfo = resolveInfo;
	}

	public ResolveInfo getResolveInfo() {
		return mResolveInfo;
	}

	private static boolean isSdkVersionValid(int targetSdkVersion) {
		return targetSdkVersion > 0;
	}

	private static boolean isVersionNameValid(String name) {
		return name != null;
	}

	private static boolean isVersionCodeValid(int code) {
		return code > 0;
	}
}
