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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sprc.circlelauncher.SPen.Input;
import com.sprc.circlelauncher.SPen.InputHandleListener;
import com.sprc.circlelauncher.view.CircleListView;

/**
 * A {@link BaseAdapter} implementation that provides a list of applications.
 *
 * @author sprc
 *
 */
public class PackageAdapter extends BaseAdapter implements InputHandleListener {

	private final Context mContext;
	private List<ResolveInfo> mApplications;
	private final LayoutInflater mInflater;
	private final PackageManager mPackageManager;

	private OnCircleItemClickListener mOnCircleItemClickListener;
	private OnCircleItemLongClickListener mOnCircleItemLongClickListener;
	private OnCircleItemHoverListener mOnCircleItemHoverListener;
	private OnCircleItemHoverButtonListener mOnCircleItemHoverButtonListener;

	private final SPen mSpen;

	public PackageAdapter(final Context ctx) {
		mContext = ctx;

		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPackageManager = mContext.getPackageManager();

		mSpen = new SPen(ctx);

		mApplications = new ArrayList<ResolveInfo>();
	}

	/**
	 * Reloads the application list and calls {@link #notifyDataSetChanged()} on this {@link Adapter}.
	 */
	public void reloadApplications() {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		mApplications = mPackageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA);

		ArrayList<ResolveInfo> blackListedApplications = new ArrayList<ResolveInfo>();

		final SharedPreferences prefs = mContext.getSharedPreferences(ChooseAppsActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);

		for (ResolveInfo info : mApplications) {
			if (!prefs.getBoolean(info.activityInfo.name, true)) {

				// Remove this app from the list only if it's corresponding key in SharedPreferences is false

				blackListedApplications.add(info);
			}
		}

		mApplications.removeAll(blackListedApplications);

		Collections.sort(mApplications, new ResolveInfo.DisplayNameComparator(mPackageManager));

		notifyDataSetChanged();
	}

	/**
	 * Since we want to loop our list we return {@link Integer#MAX_VALUE}.
	 *
	 * {@inheritDoc}
	 */
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	/**
	 * Retrieves an item in a position from the range (0; {@link Integer#MAX_VALUE}).
	 *
	 * {@inheritDoc}
	 */
	public Object getItem(final int position) {
		if (mApplications.size() > 0) {
			return mApplications.get(position % mApplications.size());
		}

		return null;
	}

	public long getItemId(final int position) {
		final ResolveInfo info = ((ResolveInfo) getItem(position));

		if (info == null) {
			return 0l;
		} else {
			return info.activityInfo.applicationInfo.uid;
		}
	}

	public View getView(final int position, final View convertView, final ViewGroup parent) {

		ViewHolder holder;

		View v = convertView;
		if (v == null) {

			v = mInflater.inflate(R.layout.circle_list_item, null);

			mSpen.registerInputHandler(v, this);

			holder = new ViewHolder();
			holder.mIcon = (ImageView) v.findViewById(R.id.icon);
			holder.mAppName = (TextView) v.findViewById(R.id.label_app_name);

			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		// We use those tags to have the access to parent and position in listeners.

		v.setTag(R.id.key_parent, parent);
		v.setTag(R.id.key_position, position);

		final ResolveInfo resolveInfo = (ResolveInfo) getItem(position);
		if (resolveInfo != null) {
			holder.mIcon.setImageDrawable(resolveInfo.loadIcon(mPackageManager));
			holder.mAppName.setText(resolveInfo.loadLabel(mPackageManager));
		} else {
			holder.mIcon.setImageDrawable(null);
			holder.mAppName.setText(null);
		}

		return v;
	}

	public void setOnCircleItemClickListener(final OnCircleItemClickListener listener) {
		mOnCircleItemClickListener = listener;
	}

	public void setOnCircleItemLongClickListener(final OnCircleItemLongClickListener listener) {
		mOnCircleItemLongClickListener = listener;
	}

	public void setOnCircleItemHoverListener(final OnCircleItemHoverListener listener) {
		mOnCircleItemHoverListener = listener;
	}

	public void setOnCircleItemHoverButtonUpListener(final OnCircleItemHoverButtonListener listener) {
		mOnCircleItemHoverButtonListener = listener;
	}

	public void setOnCircleItemHoverButtonDownListener(final OnCircleItemHoverButtonListener listener) {
		mOnCircleItemHoverButtonListener = listener;
	}

	private class ViewHolder {
		ImageView mIcon;
		TextView mAppName;
	}

	/**
	 * Handles click events coming from {@link CircleListView} elements.
	 *
	 * It is not possible to move the clickable region of each {@link ListView} item. The workaround involved setting
	 * {@link OnClickListener} on each list element in the {@link BaseAdapter} implementation.
	 */
	public interface OnCircleItemClickListener {
		void onClick(View v, BaseAdapter adapter);
	}

	public interface OnCircleItemLongClickListener {
		boolean onLongClick(View v, BaseAdapter adapter);
	}

	public interface OnCircleItemHoverListener {
		boolean onHover(View v, BaseAdapter adapter, long eventTime);
	}

	public interface OnCircleItemHoverButtonListener {
		boolean onHoverButtonUp(View v, BaseAdapter adapter, long eventTime);
		boolean onHoverButtonDown(View v, BaseAdapter adapter, long eventTime);
	}

	public void handleEvent(Input input) {
		switch (input.getInputType()) {
		case CLICK:
			if (SPen.ClickAction.CLICK.equals(input.getClickAction())) {
				if (mOnCircleItemClickListener != null) {
					mOnCircleItemClickListener.onClick(input.getView(), PackageAdapter.this);
				}
			} else if (SPen.ClickAction.LONGCLICK.equals(input.getClickAction())) {
				if (mOnCircleItemLongClickListener != null) {
					mOnCircleItemLongClickListener.onLongClick(input.getView(), PackageAdapter.this);
				}
			}
			break;
		case ERASER:
			break;
		case FINGER:
			break;
		case HOVER:
			if (SPen.InputAction.MOVE.equals(input.getInputAction())) {
				if (mOnCircleItemHoverListener != null) {
					mOnCircleItemHoverListener.onHover(input.getView(), PackageAdapter.this, input.getEventTime());
				}
			} else if (SPen.ButtonAction.UP.equals(input.getButtonAction())) {
				if (mOnCircleItemHoverButtonListener != null) {
					mOnCircleItemHoverButtonListener.onHoverButtonUp(input.getView(), PackageAdapter.this,
							input.getEventTime());
				}
			} else if (SPen.ButtonAction.DOWN.equals(input.getButtonAction())) {
				if (mOnCircleItemHoverButtonListener != null) {
					mOnCircleItemHoverButtonListener.onHoverButtonDown(input.getView(), PackageAdapter.this,
							input.getEventTime());
				}
			}
			break;
		case PEN:
			break;
		case UNDEFINED:
			break;
		default:
			break;
		}
	}
}
