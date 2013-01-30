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

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenDetachmentListener;
import com.samsung.spensdk.applistener.SPenHoverListener;
import com.samsung.spensdk.applistener.SPenTouchListener;

/**
 * A wrapper class for S Pen SDK events functionality.
 */
public class SPen {
	private final Context mContext;
	private final SPenEventLibrary mSPEL;

	public SPen(Context context) {
		mContext = context;
		mSPEL = new SPenEventLibrary();
	}

	/**
	 * Registers {@link InputHandleListener} implementation instance.
	 *
	 * @param view
	 *            the target {@link View} that we register our handler to
	 * @param handler
	 *            the {@link InputHandleListener} implementation instance
	 */
	public void registerInputHandler(View view, final InputHandleListener handler) {
		registerTouchListener(view, handler);
		registerHoverListener(view, handler);
		registerClickListener(view, handler);
	}

	public void registerContextHandler(ContextHandleListener handler) {
		registerSPenDetachmentListener(handler);
	}

	/**
	 * Potential performance issue: For every event we allocate a new InputBuilder object which allocates new Input
	 * objects too. We should utilize an Object Pool design pattern here to make the garbage collector happy.
	 */
	private void registerTouchListener(View view, final InputHandleListener handler) {
		mSPEL.setSPenTouchListener(view, new SPenTouchListener() {
			public void onTouchButtonDown(View view, MotionEvent event) {
				handler.handleEvent(new InputBuilder(view).setInputType(InputType.PEN)
						.setButtonAction(ButtonAction.DOWN).setEventTime(event.getEventTime()).build());
			}

			public void onTouchButtonUp(View view, MotionEvent event) {
				handler.handleEvent(new InputBuilder(view).setInputType(InputType.PEN).setButtonAction(ButtonAction.UP)
						.setEventTime(event.getEventTime()).build());
			}

			public boolean onTouchFinger(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.FINGER)
							.setInputAction(InputAction.DOWN).setEventTime(event.getEventTime()).build());
					break;
				case MotionEvent.ACTION_UP:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.FINGER)
							.setInputAction(InputAction.UP).setEventTime(event.getEventTime()).build());
					break;
				default:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.FINGER)
							.setInputAction(InputAction.MOVE).setEventTime(event.getEventTime()).build());
					break;
				}
				return false;
			}

			public boolean onTouchPen(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.PEN)
							.setInputAction(InputAction.DOWN).setEventTime(event.getEventTime()).build());
					break;
				case MotionEvent.ACTION_UP:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.PEN)
							.setInputAction(InputAction.UP).setEventTime(event.getEventTime()).build());
					break;
				default:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.PEN)
							.setInputAction(InputAction.MOVE).setEventTime(event.getEventTime()).build());
					break;
				}
				return false;
			}

			public boolean onTouchPenEraser(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.ERASER)
							.setInputAction(InputAction.DOWN).setEventTime(event.getEventTime()).build());
					break;
				case MotionEvent.ACTION_UP:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.ERASER)
							.setInputAction(InputAction.UP).setEventTime(event.getEventTime()).build());
					break;
				default:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.ERASER)
							.setInputAction(InputAction.MOVE).setEventTime(event.getEventTime()).build());
					break;
				}
				return false;
			}
		});
	}

	private void registerHoverListener(View view, final InputHandleListener handler) {
		mSPEL.setSPenHoverListener(view, new SPenHoverListener() {
			public boolean onHover(View view, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.HOVER)
							.setInputAction(InputAction.DOWN).setEventTime(event.getEventTime()).build());
					break;
				case MotionEvent.ACTION_UP:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.HOVER)
							.setInputAction(InputAction.UP).setEventTime(event.getEventTime()).build());
					break;
				default:
					handler.handleEvent(new InputBuilder(view).setInputType(InputType.HOVER)
							.setInputAction(InputAction.MOVE).setEventTime(event.getEventTime()).build());
					break;
				}
				return false;
			}

			public void onHoverButtonDown(View view, MotionEvent event) {
				handler.handleEvent(new InputBuilder(view).setInputType(InputType.HOVER)
						.setButtonAction(ButtonAction.DOWN).setEventTime(event.getEventTime()).build());
			}

			public void onHoverButtonUp(View view, MotionEvent event) {
				handler.handleEvent(new InputBuilder(view).setInputType(InputType.HOVER)
						.setButtonAction(ButtonAction.UP).setEventTime(event.getEventTime()).build());
			}
		});
	}

	private void registerClickListener(final View view, final InputHandleListener handler) {
		view.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				handler.handleEvent(new InputBuilder(view).setInputType(InputType.CLICK)
						.setClickAction(ClickAction.CLICK).build());
			}
		});

		view.setOnLongClickListener(new OnLongClickListener() {

			public boolean onLongClick(View v) {
				handler.handleEvent(new InputBuilder(view).setInputType(InputType.CLICK)
						.setClickAction(ClickAction.LONGCLICK).build());

				return true;
			}
		});
	}

	private void registerSPenDetachmentListener(final ContextHandleListener handler) {
		mSPEL.registerSPenDetachmentListener(mContext, new SPenDetachmentListener() {
			public void onSPenDetached(boolean bDetached) {
				if (bDetached) {
					handler.handleEvent(ContextAction.SPEN_DETACHMENT);
				} else {
					handler.handleEvent(ContextAction.SPEN_ATTACHMENT);
				}
			}
		});
	}

	/**
	 * Input's types.
	 */
	public static enum InputType {
		CLICK, PEN, ERASER, FINGER, HOVER, UNDEFINED
	};

	/**
	 * Touch input actions.
	 */
	public static enum InputAction {
		DOWN, MOVE, UP, UNDEFINED
	}

	/**
	 * Actions defining side button status.
	 */
	public static enum ButtonAction {
		DOWN, UP, UNDEFINED
	}

	/**
	 * Actions defining clicks.
	 */
	public static enum ClickAction {
		CLICK, LONGCLICK, UNDEFINED
	}

	/**
	 * Additional non-touch related actions.
	 */
	public static enum ContextAction {
		SPEN_ATTACHMENT, SPEN_DETACHMENT
	}

	public interface InputHandleListener {
		void handleEvent(Input input);
	}

	public interface ContextHandleListener {
		void handleEvent(ContextAction action);
	}

	/**
	 * Contains information about an input event.
	 */
	public static class Input {
		private InputType mInputType;
		private InputAction mInputAction;
		private ButtonAction mButtonAction;
		private ClickAction mClickAction;
		private long mEventTime;
		private View mView;

		public void setInputType(InputType type) {
			mInputType = type;
		}

		public InputType getInputType() {
			return mInputType;
		}

		public void setInputAction(InputAction action) {
			mInputAction = action;
		}

		public InputAction getInputAction() {
			return mInputAction;
		}

		public void setButtonAction(ButtonAction action) {
			mButtonAction = action;
		}

		public ButtonAction getButtonAction() {
			return mButtonAction;
		}

		public void setClickAction(ClickAction action) {
			mClickAction = action;
		}

		public ClickAction getClickAction() {
			return mClickAction;
		}

		public void setEventTime(long time) {
			mEventTime = time;
		}

		public long getEventTime() {
			return mEventTime;
		}

		public void setView(View v) {
			mView = v;
		}

		public View getView() {
			return mView;
		}
	}

	/**
	 * {@link Input} builder helper.
	 */
	public static class InputBuilder {
		private InputType mInputType = InputType.UNDEFINED;
		private InputAction mInputAction = InputAction.UNDEFINED;
		private ButtonAction mButtonAction = ButtonAction.UNDEFINED;
		private ClickAction mClickAction = ClickAction.UNDEFINED;
		private long mEventTime = 0l;
		private View mView;

		public InputBuilder() {
		}

		public InputBuilder(View v) {
			mView = v;
		}

		/**
		 * Creates a new {@link Input} object based on current builder state.
		 *
		 * @return {@link Input} instance
		 */
		public Input build() {
			Input input = new Input();
			input.setInputType(mInputType);
			input.setInputAction(mInputAction);
			input.setButtonAction(mButtonAction);
			input.setClickAction(mClickAction);
			input.setEventTime(mEventTime);
			input.setView(mView);
			return input;
		}

		public InputBuilder setInputType(InputType type) {
			mInputType = type;
			return this;
		}

		public InputBuilder setInputAction(InputAction action) {
			mInputAction = action;
			return this;
		}

		public InputBuilder setButtonAction(ButtonAction action) {
			mButtonAction = action;
			return this;
		}

		public InputBuilder setClickAction(ClickAction action) {
			mClickAction = action;
			return this;
		}

		public InputBuilder setEventTime(long time) {
			mEventTime = time;
			return this;
		}

		public InputBuilder setView(View v) {
			mView = v;
			return this;
		}
	}
}
