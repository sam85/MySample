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

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.Window;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.sprc.circlelauncher.PackageAdapter.OnCircleItemClickListener;
import com.sprc.circlelauncher.PackageAdapter.OnCircleItemHoverButtonListener;
import com.sprc.circlelauncher.PackageAdapter.OnCircleItemHoverListener;
import com.sprc.circlelauncher.PackageAdapter.OnCircleItemLongClickListener;
import com.sprc.circlelauncher.popup.AppPopupWindow;
import com.sprc.circlelauncher.view.CircleListView;
import com.sprc.circlelauncher.view.CircleListView.OnItemCenteredListener;
import com.sprc.circlelauncher.view.CircularViewModifier;

/**
 * Provides a drawer of applications in a wheel form.
 *
 * @author sprc
 *
 */

public class LauncherActivity extends Activity implements OnCircleItemClickListener, OnCircleItemLongClickListener,
		OnCircleItemHoverListener, OnCircleItemHoverButtonListener, OnDragListener, OnClickListener,
		OnItemCenteredListener {

	private static final boolean DEBUG = BuildConfig.DEBUG && false;
	private static final String TAG = LauncherActivity.class.getSimpleName();

	private static final int MENU_ITEM_CHOSE_APP = 1001;
	private static final int MENU_ITEM_HELP = 1002;

	public static final int REQUEST_CODE_UNINSTALL = 1;
	private static final String DRAG_LABEL_SHORTCUT = "Dragging shortcut";
	private static final long HOVER_TIME_TRIGGER = 500;

	private static final String SP_KEY_IS_FIRST_RUN = "isFirstRun";

	private CircleListView mCircleListView;
	private PackageAdapter mPackageAdapter;
	private AppDetailsFragment mAppDetailsFragment;
	private int mLastPosition;
	private long mHoverStartTime;

	private AppPopupWindow mCircleAppContextMenu;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getWindow() != null) {
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		}

		setContentView(R.layout.activity_launcher);

		if (DEBUG) {
			Debug.waitForDebugger();
		}

		mCircleListView = (CircleListView) findViewById(R.id.circle_list);
		mPackageAdapter = new PackageAdapter(this);

		// Set CircleListView's item listeners

		mPackageAdapter.setOnCircleItemClickListener(this);
		mPackageAdapter.setOnCircleItemLongClickListener(this);
		mPackageAdapter.setOnCircleItemHoverListener(this);
		mPackageAdapter.setOnCircleItemHoverButtonDownListener(this);

		((View) mCircleListView.getParent()).setOnClickListener(this);
		mCircleListView.setAdapter(mPackageAdapter);
		mCircleListView.setViewModifier(new CircularViewModifier());

		CircleListView v = (CircleListView) findViewById(R.id.circle_list);
		v.setOnDragListener(this);
		v.setOnItemCenteredListener(this);

		mCircleAppContextMenu = new AppPopupWindow(this);

		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setBackgroundDrawable(null);
		}

		if (isFirstLaunch()) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean(SP_KEY_IS_FIRST_RUN, false);
			editor.commit();

			showHelpDialog();
		}
	}

	/**
	 * Checks if this is the first launch of this application.
	 *
	 * @return true if the application was not run before, false otherwise
	 */
	private boolean isFirstLaunch() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		return prefs.getBoolean(SP_KEY_IS_FIRST_RUN, true);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		menu.add(0, MENU_ITEM_CHOSE_APP, 1, R.string.menu_title_chooseapps);
		menu.add(0, MENU_ITEM_HELP, 2, R.string.menu_title_help);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mPackageAdapter.reloadApplications();
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_CHOSE_APP: {
			final Intent myIntent = new Intent(this, ChooseAppsActivity.class);
			startActivity(myIntent);
			break;
		}
		case MENU_ITEM_HELP: {
			showHelpDialog();
			break;
		}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

		switch (requestCode) {
		case REQUEST_CODE_UNINSTALL:
			mPackageAdapter.reloadApplications();
			break;
		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	public boolean onDrag(final View v, final DragEvent event) {
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
			if (DEBUG) {
				Log.d(TAG, "ACTION_DRAG_STARTED");
			}

			return true;
		case DragEvent.ACTION_DRAG_EXITED:
			if (DEBUG) {
				Log.d(TAG, "ACTION_DRAG_EXITED");
			}

			// Exited the CircleListView, close the Launcher and show Home.

			finish();
			break;
		default:
			break;
		}

		return false;
	}

	/**
	 * {@link OnCircleItemClickListener#onClick(View, BaseAdapter)} implementation for the circle list.
	 */

	public void onClick(final View v, final BaseAdapter adapter) {

		final int position = (Integer) v.getTag(R.id.key_position);
		final ResolveInfo info = (ResolveInfo) mPackageAdapter.getItem(position);

		if (v.equals(mCircleListView.findViewAtCenter())) {

			if (info != null) {
				final Intent intent = getPackageManager().getLaunchIntentForPackage(info.activityInfo.packageName);
				startActivity(intent);
			}
		} else {
			mCircleListView.smoothScrollToView(v);

			showAppDetails(info);
		}
	}

	/**
	 * Handles the long click on the middle {@link View} in the {@link #mCircleListView}.
	 */
	public boolean onLongClick(final View v, final BaseAdapter adapter) {

		// Start dragging the application icon.

		final int position = (Integer) v.getTag(R.id.key_position);
		final ResolveInfo info = (ResolveInfo) adapter.getItem(position);
		if (info != null) {
			final View iconView = v.findViewById(R.id.icon);
			final ClipData data = ClipData.newPlainText(DRAG_LABEL_SHORTCUT, "Dragging: " + info.resolvePackageName);
			iconView.startDrag(data, new DragShadowBuilder(iconView), v, 0);

			return true;
		}

		return false;
	}

	/**
	 * Handles the hover event on the {@link View} in the {@link #mCircleListView}.
	 */
	public boolean onHover(final View v, final BaseAdapter adapter, long eventTime) {
		if (DEBUG) {
			Log.d(TAG, "onHover()");
		}

		if (isFinishing()) {
			return true;
		}

		final int position = (Integer) v.getTag(R.id.key_position);
		final ResolveInfo info = getResolveInfo(v, adapter);

		if (position == mLastPosition) {

			// Still hovering over the same item

			if (eventTime - mHoverStartTime > HOVER_TIME_TRIGGER) {
				showAppDetails(info);
			}

		} else {

			// Hovering over a new item

			mHoverStartTime = eventTime;
			mLastPosition = position;
		}

		return true;
	}

	public boolean onHoverButtonDown(final View v, final BaseAdapter adapter, long eventTime) {

		if (DEBUG) {
			Log.d(TAG, "onHoverButtonDown");
		}

		final ResolveInfo info = getResolveInfo(v, adapter);

		mCircleAppContextMenu.setResolveInfo(info);
		mCircleAppContextMenu.setAnchor(v);
		mCircleAppContextMenu.showMenu();

		return true;
	}

	public boolean onHoverButtonUp(final View v, final BaseAdapter adapter, long eventTime) {

		return false;
	}

	public void onClick(final View v) {
		showAppDetails(null);
	}

	public void onItemCentered(View v) {
		if (v != null) {
			final int position = (Integer) v.getTag(R.id.key_position);
			final ResolveInfo info = (ResolveInfo) mPackageAdapter.getItem(position);

			showAppDetails(info);
		}
	}

	/**
	 * Shows an {@link AppDetailsFragment} with application info.
	 *
	 * @param info
	 *            the {@link ResolveInfo} object with information of an application suitable to launch
	 */
	private void showAppDetails(final ResolveInfo info) {
		if (info != null) {

			if (mAppDetailsFragment != null && info.equals(mAppDetailsFragment.getResolveInfo())) {
				return;
			}

			mAppDetailsFragment = AppDetailsFragment.newInstance(info);

			final FragmentManager fm = getFragmentManager();
			final FragmentTransaction ft = fm.beginTransaction();
			ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			ft.replace(R.id.placeholder_appdetails, mAppDetailsFragment);
			ft.commit();
		} else if (mAppDetailsFragment != null) {

			final FragmentManager fm = getFragmentManager();
			final FragmentTransaction ft = fm.beginTransaction();
			ft.remove(mAppDetailsFragment);
			ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
			ft.commit();

			mAppDetailsFragment = null;
		} else {

			finish();
		}
	}

	private void showHelpDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		HelpDialogFragment frag = HelpDialogFragment.newInstance();

		frag.show(ft, null);
	}

	/**
	 * A helper method to retrieve a {@link ResolveInfo} object from the {@link Adapter}.
	 *
	 * @param v
	 *            a {@link View} that contains a key with the position in the {@link Adapter}
	 * @param adapter
	 *            actual {@link Adapter}
	 * @return the {@link ResolveInfo} object
	 */
	private static ResolveInfo getResolveInfo(final View v, final BaseAdapter adapter) {
		final int position = (Integer) v.getTag(R.id.key_position);
		return (ResolveInfo) adapter.getItem(position);
	}
}
