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

package com.sprc.circlelauncher.popup;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.sprc.circlelauncher.Constants;
import com.sprc.circlelauncher.LauncherActivity;
import com.sprc.circlelauncher.R;

/**
 * This class extends PopupWindow and it represents context menu.
 *
 * @author Konrad Krakowiak <k.krakowiak@samsung.com>
 *
 */
public class AppPopupWindow extends PopupWindow {

	private static final String SCHEMA_PACKAGE = "package";

	private final Context mContext;
	private View mAnchor;
	private ResolveInfo mResolveInfo;

	public AppPopupWindow(final Context ctx) {
		super();

		mContext = ctx;

		final LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final View layout = inflater.inflate(R.layout.popupwindow_app,
				null, false);

		// A dirty hack to make the popup disappear when pressed outside of it.

		setBackgroundDrawable(new BitmapDrawable(mContext.getResources()));

		setFocusable(false);
		prepareMenuItem(layout);
		setContentView(layout);
		setOutsideTouchable(true);

		setHeight(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		setWidth(android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	public void setResolveInfo(final ResolveInfo info) {
		mResolveInfo = info;
	}

	public void setAnchor(final View v) {
		mAnchor = v;
	}

	/**
	 * Shows a popup menu.
	 */
	public void showMenu() {
		if (mAnchor != null) {

			if (isShowing()) {
				dismiss();
			}

			showAsDropDown(mAnchor, 0, -mAnchor.getHeight());
		}
	}

	/**
	 * Method prepares menu item. Sets onClicklistener for them.
	 *
	 * @param pLayoutView
	 *            - view from CircleAppContextMenu,
	 */
	private void prepareMenuItem(final View pView) {
		final ImageView details = (ImageView) pView
				.findViewById(R.id.details_item);
		final ImageView delete = (ImageView) pView
				.findViewById(R.id.delete_item);

		final OnClickListener onClickListener = new OnClickListener() {

			public void onClick(final View v) {
				switch (v.getId()) {
				case R.id.details_item:
					showInstalledAppDetails(mResolveInfo.activityInfo.packageName);

					break;

				case R.id.delete_item:
					final ApplicationInfo info = mResolveInfo.activityInfo.applicationInfo;
					final Intent intent = new Intent(Intent.ACTION_DELETE,
							Uri.fromParts(SCHEMA_PACKAGE, info.packageName,
									null));

					((Activity) mContext).startActivityForResult(intent,
							LauncherActivity.REQUEST_CODE_UNINSTALL);

					break;
				default:
					throw new IllegalArgumentException("No known view.");
				}

				dismiss();
			}
		};

		details.setOnClickListener(onClickListener);
		delete.setOnClickListener(onClickListener);
	}

	/**
	 * Sends an {@link Intent} to show application details {@link Activity} in
	 * {@link Settings}.
	 *
	 * @param packageName
	 *            the name of the package
	 */
	public void showInstalledAppDetails(final String packageName) {
		Intent intent;

		if (Constants.SUPPORTS_GINGERBREAD) {
			final Uri packageUri = Uri.fromParts(SCHEMA_PACKAGE, packageName,
					null);
			intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
					packageUri);
		} else {
			intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(Constants.SETTINGS_PACKAGENAME,
					Constants.SETTINGS_APPDETAILS_CLASS);
			intent.putExtra(Constants.SETTINGS_EXTRA_KEY, packageName);
		}
		try {
			mContext.startActivity(intent);
		} catch (final ActivityNotFoundException e) {
			Toast.makeText(mContext, R.string.toast_unable_to_run_settings,
					Toast.LENGTH_LONG).show();
		}
	}

}
