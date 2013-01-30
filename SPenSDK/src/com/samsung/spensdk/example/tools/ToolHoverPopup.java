/*
 * Copyright (C) 2006 The Android Open Source Project
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Cap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.samsung.spensdk.example.R;

public class ToolHoverPopup implements View.OnHoverListener{

    static final boolean DEBUG = true;
	
    static final String TAG = "HoverPopup";

    private static final int MSG_SHOW_POPUP = 1;

    private static final int MSG_DISMISS_POPUP = 2;

    private static final int HOVER_DETECT_TIME_MS = 300;

    private static final int POPUP_TIMEOUT_MS = 10 * 1000;

    public static final int TYPE_NONE = 0; // turn off HoverPopup

    public static final int TYPE_TOOLTIP = 1; // toolTip type
    
    public static final int TYPE_USER_CUSTOM = 2;

    private static final int ID_TOOLTIP_VIEW = 0x07010001;

    protected final int MARGIN_FOR_HOVER_RING = 8; // 8dp

    private HoverPopupHandler mHandler;

    protected final Context mContext;

    protected final View mParentView;

    protected PopupWindow mPopup;

    protected int mPopupType = TYPE_NONE;

    protected boolean mEnabled;

    protected int mHoverDetectTimeMS;

    protected int mPopupGravity;

    protected int mPopupPosX;

    protected int mPopupPosY;

    protected int mHoveringPointX;

    protected int mHoveringPointY;

    protected int mContentResId;

    protected int mAnimationStyle;

    protected View mContentView;
    
    protected ViewGroup.LayoutParams mContentLP;

    protected HoverPopupContainer mContentContainer;

    protected CharSequence mContentText;

    protected boolean mIsGuideLineEnabled;

    protected int mGuideLineFadeOffset;

    protected int mPopupOffsetX;

    protected int mPopupOffsetY;
    
    protected int mGuideRingDrawableId;
    
    protected int mGuideLineColor;
    
    protected int mWindowGapX;
    
    protected int mWindowGapY;

    /**
     * Basic constructor to instantiate
     * 
     * @param parentView The view that created/made this class
     */
    @Deprecated
    public ToolHoverPopup(View parentView) {
        this(parentView, TYPE_TOOLTIP);
    }

    /**
     * Basic constructor to instantiate
     * 
	 * @param parentView The view that created/made this class
     * @param type The type of HoverPopup Window, it can be one of the "TYPE_NONE",
     */
    public ToolHoverPopup(View parentView, int type) {
        mParentView = parentView;
        mContext = parentView.getContext();
        mPopupType = type;

        initInstance();        
        registerHover();
    }

    private void registerHover(){
        mParentView.setOnHoverListener(this);
    }
    
    public boolean onHover(View v, MotionEvent event){
        final int action = event.getAction();        
        if (action == MotionEvent.ACTION_HOVER_ENTER){            
            setHoveringPoint((int)event.getRawX(), (int)event.getRawY());
            show();                    
        } else if (action == MotionEvent.ACTION_HOVER_MOVE){
            setHoveringPoint((int) event.getRawX(), (int) event.getRawY());
            // update popup for moving guide line
            if (mIsGuideLineEnabled && isShowing()) {
                View popupView = mPopup.getContentView();
                if (popupView instanceof HoverPopupContainer) {
                    ((HoverPopupContainer) mPopup.getContentView()).setGuideLineEndPoint(
                            (int) event.getRawX() - mPopupPosX - mWindowGapX, (int) event.getRawY() - mPopupPosY - mWindowGapY);

                    ((HoverPopupContainer) popupView).updateDecoration();
                }
            }
        } else if (action == MotionEvent.ACTION_HOVER_EXIT){            
            dismiss();
        }           
                
        return true;
    }
    
    /**
     * Initialize instances
     */
    protected void initInstance() {
        mPopup = null;
        mEnabled = true;
        mHoverDetectTimeMS = HOVER_DETECT_TIME_MS;
        
        mPopupGravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP_ABOVE;
        mPopupPosX = 0;
        mPopupPosY = 0;
        mHoveringPointX = 0;
        mHoveringPointY = 0;
        mPopupOffsetX = 0;
        mPopupOffsetY = 0;
        mWindowGapX = 0;
        mWindowGapY = 0;
        
        mContentText = null;
        mHandler = null;
        mAnimationStyle = 0;
        mIsGuideLineEnabled = false;
        mGuideLineFadeOffset = 0;
        mContentView = null;
        mContentContainer = null;
        
        mGuideRingDrawableId = R.drawable.hover_ic_point_normal;
        mGuideLineColor = 0xFF9ca2a9;
    }
    
    /**
     * Check the condition that needs for showing.
     * 
     * @return true if HoverPopup can be shown, false if showing condition is not satisfied.
     */
    public boolean isHoverPopupPossible() {
        boolean ret = true;

        if (mPopupType == TYPE_NONE) {
            ret = false;
        } else if (mPopupType == TYPE_TOOLTIP) {
            if (mParentView == null || TextUtils.isEmpty(getTooltipText())) {
                ret = false;
            }
        } 
        return ret;
    }
    

    @Deprecated
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Deprecated
    public boolean getEnabled() {
        return mEnabled;
    }

    /**
     * Returns the parent View that called/made this HoverPopup.
     *
     * @return a ViewParent or null if this ViewParent does not have a parent
     */
    public View getParentView() {
        return mParentView;
    }

    /**
     * Set a popup's content. If want to use custom view, Popup Type should be "TYPE_USER_CUSTOM"
     * 
     *  @param resId layout resource ID 
     */
    @Deprecated
    public void setContent(int resId) {
        mContentResId = resId;
    }

    /**
     * Set a popup's content. If want to use custom view, Popup Type should be "TYPE_USER_CUSTOM"
     * 
     *  @param view the new content view for Popup 
     */
    public void setContent(View view) {
        setContent(view, (view != null)?view.getLayoutParams():null);
    }
    
    /**
     * Set a popup's content. If want to use custom view, Popup Type should be "TYPE_USER_CUSTOM"
     * 
     * @param view the new content view with specific LayoutParam. This supports only FrameLayout.LayoutParam.
     *        Note that you can use "WRAP_CONTENT" or absolute width/height for LayoutParam attribute.
     *        "FILL_PARENT" operates as "WRAP_CONTENT"
     */
    public void setContent(View view, ViewGroup.LayoutParams lp) {
        mContentView = view;
        mContentLP = lp;
    }

    /**
     * Sets the text for HoverPopup. this is used in some specific TYPE.
     * if uses TYPE_TOOLTIP, this text will be shown instead of "contentDescription"
     * 
     * @param text Text
     */
    public void setContent(CharSequence text) {
        mContentText = text;
    }

    /**
     * Return the view used as the content of the Popup window.
     *
     * @return View The popup's content
     */
    public View getContent() {
        return mContentView;
    }

    /**
     * Indicate whether this hover Popup is showing on screen.
     *
     * @return true if the popup is showing, false otherwise
     */
    protected boolean isShowing() {
        if (mPopup != null) {
            return mPopup.isShowing();
        }
        return false;
    }

    /**
     * Sets a gravity of hover popup. basically, Popup is based on parent View that called popup.
     * 
     * @param gravity The gravity which controls the placement of the popup window. HoverPopup.Gravity's types can be used. 
     */
    public void setPopupGravity(int gravity) {
        mPopupGravity = gravity;
    }

    /**
     * Sets the time that detecting hovering. 
     * 
     * @param ms The time, milliseconds
     */
    public void setHoverDetectTime(int ms) {
        mHoverDetectTimeMS = ms;
    }

    /**
     * Sets a Popup offset.  
     * 
     * @param x The popup's x location offset
     * @param y The popup's y location offset
     */
    public void setPopupPosOffset(int x, int y) {
        mPopupOffsetX = x;
        mPopupOffsetY = y;
    }

    /**
     * Sets a point that now hovering. This is used for drawing guide line.
     * 
     * @param x The absolute X point on Window
     * @param y The absolute Y point on Window
     */
    public void setHoveringPoint(int x, int y) {
        mHoveringPointX = x;
        mHoveringPointY = y;
    }

    @Deprecated
    protected CharSequence getPriorityContentText() {
        if (!TextUtils.isEmpty(mContentText)) {
            return mContentText;
        } else if (!TextUtils.isEmpty(mParentView.getContentDescription())) {
            return mParentView.getContentDescription();
        }
        return null;
    }
    
    /**
     * Returns the text for ToolTip.
     * 
     * @return The Tooltip's text. NULL, if text is empty.
     */
    private CharSequence getTooltipText(){
        if (!TextUtils.isEmpty(mContentText)) {
            return mContentText;
        } else if (!TextUtils.isEmpty(mParentView.getContentDescription())) {
            return mParentView.getContentDescription();
        }
        return null;
    }

    @Deprecated
    public void show() {
        show(mPopupType);
    }

    /**
     * Display the hover popup to window. This checks the condition and sends the Message to show after N sec.   
     * Note that do not call this directly.
     * 
     * @param type The type of the Hover Popup
     */
    public void show(int type) {

        if (type != mPopupType) {
            mPopupType = type;            
        }

        // return if TYPE is none.
        if (!mEnabled || type == TYPE_NONE || !isHoverPopupPossible() 
		        || isShowing())
            return;

        // send message to show.
        if (getHandler().hasMessages(MSG_SHOW_POPUP)) {
            return;
            // getHandler().removeMessages(MSG_SHOW_POPUP);
        }
        getHandler().sendEmptyMessageDelayed(MSG_SHOW_POPUP, mHoverDetectTimeMS);
    }

    /**
     * Internal method for making/showing popup. calls some methods sequentially. 
     */
    private void showPopup() {

        if (mPopup != null) {
            mPopup.dismiss();
        }

        createPopupWindow();
        setPopupContent();
        updateHoverPopup();
    }

    /**
     * Create a PopupWindow and set a options if want to use custom popup window
     * with style, override this.
     * 
     * @return The PopupWindow
     */
    protected PopupWindow createPopupWindow() {
        if (mPopup == null) {
            mPopup = new PopupWindow(mParentView.getContext());
            mPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopup.setTouchable(false);
            mPopup.setClippingEnabled(false);
            mPopup.setBackgroundDrawable(null);
           // mPopup.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        }
        return mPopup;
    }

    /**
     * Create a content view according to Popup Type 
     */
    private void setPopupContent() {

        /**
         * 1. get a view as per each mPopupType. 2. call a listener that
         * override by USER.
         */

        switch (mPopupType) {
            case TYPE_NONE:
                mContentView = null;
                break;
            case TYPE_TOOLTIP:
                makeToolTipContentView();
                break;
                
            case TYPE_USER_CUSTOM:
                if (mContentView == null) {
                    if (mContentResId != 0) {
                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        try {
                            View v = inflater.inflate(mContentResId, null);
                            mContentView = v;
                        } catch (InflateException ie) {
                            mContentView = null;
                        }
                    }
                }
                break;
            default:
                mContentView = null;
                break;
        }

    }


    /**
     * Create a content view for TYPE_TOOLTIP 
     */
    private void makeToolTipContentView() {
        CharSequence text = getTooltipText();

        if (TextUtils.isEmpty(text)) {
            // if description is not set, do not show tooltip.
            mContentView = null;
        } else {
            // otherwise, inflate Tooltip view and set Text.
            if (mContentView == null || mContentView.getId() != ID_TOOLTIP_VIEW) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                mContentView = inflater.inflate(R.layout.hover_tooltip_popup, null);                
                mContentView.setId(ID_TOOLTIP_VIEW);
            }
            ((TextView) mContentView).setText(text);
        }
    }

    /**
     * Compute popup location with a gravity and offset that received.
     * 
     * @param gravity the gravity of object. HoverGravity's attributes can be used.
     * @param offX the popup's x location offset
     * @param offY the popup's y location offset
     * 
     */
    protected void computePopupPosition(View anchor, int gravity, int offX, int offY) {
        // can not compute position.
        if (mContentView == null) {
            return;
        }
        
        View anchorView = (anchor != null)?anchor:mParentView;
        final DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();        
                
        int[] anchorLocOnScr = new int[2];
        int[] anchorLocInWindow = new int[2];
        anchorView.getLocationOnScreen(anchorLocOnScr);
        anchorView.getLocationInWindow(anchorLocInWindow);
        
        // window rect that can be drawn.
        Rect displayFrame = new Rect();
        anchorView.getWindowVisibleDisplayFrame(displayFrame);
        
        if (DEBUG){
            Log.e(TAG, "anchor location on screen x : " + anchorLocOnScr[0] + "  y: " + anchorLocOnScr[1]);
            Log.e(TAG, "anchor location in window x : " + anchorLocInWindow[0] + "  y: " + anchorLocInWindow[1]);
        }
        
        // get a anchor rect.
        Rect anchorRect;
        if (!anchorView.getApplicationWindowToken().equals(anchorView.getWindowToken())){        
            // use a location that on screen. because popup is inflated with App window token. 
            mWindowGapX = 0;
            mWindowGapY = 0;
            
            anchorRect = new Rect(anchorLocOnScr[0], anchorLocOnScr[1],
                    (anchorLocOnScr[0] + anchorView.getWidth()),
                    (anchorLocOnScr[1] + anchorView.getHeight()));
            
            /* if anchorView is on sub-panel window, fix displayFrame rect.
             * because Rect just has child's(sub-panel) displayFrame */
            displayFrame.left = 0;
            displayFrame.right = displayMetrics.widthPixels;
            displayFrame.top = 0;
            displayFrame.bottom = displayMetrics.heightPixels;
        } else {
            // use a location that in window.
            mWindowGapX = anchorLocOnScr[0] - anchorLocInWindow[0];
            mWindowGapY = anchorLocOnScr[1] - anchorLocInWindow[1];
            
            anchorRect = new Rect(anchorLocInWindow[0], anchorLocInWindow[1],
                    (anchorLocInWindow[0] + anchorView.getWidth()),
                    (anchorLocInWindow[1] + anchorView.getHeight()));
        }
        
        displayFrame.left = Math.max(0, displayFrame.left);
        displayFrame.right = Math.min(displayMetrics.widthPixels, displayFrame.right);
        displayFrame.top = Math.max(0, displayFrame.top);
        displayFrame.bottom = Math.min(displayMetrics.heightPixels, displayFrame.bottom);

        /** 
         * measure contentView size.    
         */
        int contentWidth, contentHeight;
        int widthMeasureSpec, heightMeasureSpec;
        
        if (mContentLP == null) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    displayMetrics.widthPixels, View.MeasureSpec.AT_MOST);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                    displayMetrics.heightPixels, View.MeasureSpec.AT_MOST);
        } else {
            if (mContentLP.width >= 0) {
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        mContentLP.width, View.MeasureSpec.EXACTLY);
            } else {    // wrap_content
                widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        displayMetrics.widthPixels,
                        View.MeasureSpec.AT_MOST);
            }

            if (mContentLP.height >= 0) {      
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        mContentLP.height, View.MeasureSpec.EXACTLY);
            } else {    // wrap_content
                heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                        displayMetrics.heightPixels,
                        View.MeasureSpec.AT_MOST);
            }

        }
        
        mContentView.measure(widthMeasureSpec, heightMeasureSpec);
        contentWidth = mContentView.getMeasuredWidth();
        contentHeight = mContentView.getMeasuredHeight();
        
        mPopup.setWidth(contentWidth);
        mPopup.setHeight(contentHeight);
        

        /** 
         * get a location according to the gravity    
         */
        // find X,Y position according to mPopupGravity.
        int hGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
        int vGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        int posX;
        int posY;
        if (gravity == Gravity.NO_GRAVITY){
            //if no gravity, just use offset value. 
            posX = offX;
            posY = offY;
            
        } else {
            // horizontal
            switch (hGravity) {
                case Gravity.LEFT_OUTSIDE:
                    posX = anchorRect.left - contentWidth;
                    break;
    
                case Gravity.LEFT:
                    posX = anchorRect.left;
                    break;
    
                case Gravity.LEFT_CENTER_AXIS:
                    posX = anchorRect.centerX() - contentWidth;
                    break;
    
                case Gravity.CENTER_HORIZONTAL:
                    posX = anchorRect.centerX() - (contentWidth/2);
                    break;
    
                case Gravity.RIGHT_CENTER_AXIS:
                    posX = anchorRect.centerX();
                    break;
    
                case Gravity.RIGHT:
                    posX = anchorRect.right - contentWidth;
                    break;
    
                case Gravity.RIGHT_OUTSIDE:
                    posX = anchorRect.right;
                    break;
    
                case Gravity.CENTER_HORIZONTAL_ON_WINDOW:
                    posX = displayFrame.centerX() - (contentWidth / 2);
                    break;

                case Gravity.CENTER_HORIZONTAL_ON_POINT:
                    posX = mHoveringPointX - (contentWidth / 2) - mWindowGapX;
                    break;

                default:
                    posX = offX;
                    break;
            }
            
            // add X offset
            posX += mPopupOffsetX;
            

            // vertical
            switch (vGravity) {
                case Gravity.TOP_ABOVE:
                    posY = anchorRect.top - contentHeight;
                    break;
                case Gravity.TOP:
                    posY = anchorRect.top;
                    break;
                case Gravity.CENTER_VERTICAL:
                    posY = anchorRect.centerY() - (contentHeight / 2);
                    break;
                case Gravity.BOTTOM:
                    posY = anchorRect.bottom - contentHeight;
                    break;
                case Gravity.BOTTOM_UNDER:
                    posY = anchorRect.bottom;
                    break;
                default:
                    posY = offY;
                    break;
            }
            
            // add Y offset
            posY += mPopupOffsetY;
        }


        /** 
         * check window boundary    
         */
        posX = Math.min(posX, (displayFrame.right - contentWidth));
        posX = Math.max(0, posX);
        
        if (posY < displayFrame.top){   // if view is over the top boundary
            // if gravity is TOP_ABOVE, move to BOTTOM_UNDER. otherwise, just pull into window boundary.
            if (vGravity == Gravity.TOP_ABOVE){
                posY = anchorRect.bottom;
                posY -= mPopupOffsetY; // adjust offset
            } else {
                posY = Math.max(displayFrame.top, posY); 
            }
        } else if ((posY+contentHeight) > displayFrame.bottom) {    // if view is over the bottom boundary
         // if gravity is BOTTOM_UNDER, move to TOP_ABOVE. otherwise, just pull into window boundary.
            if (vGravity == Gravity.BOTTOM_UNDER){ 
                posY = anchorRect.top - contentHeight;
                posY -= mPopupOffsetY; // adjust offset
            } else {
                posY = Math.min((displayFrame.bottom-contentHeight) , posY);
            }            
        }
        

        /** 
         * Draw guide line    
         */
        boolean canDraw = false;
        if ((posY <= (anchorRect.top - contentHeight)) || (posY >= anchorRect.bottom)) {
            canDraw = true;
        }

        if (mIsGuideLineEnabled && canDraw) {
            // get margin for hover ring.
            int marginForHoverRing = convertDPtoPX(MARGIN_FOR_HOVER_RING,
                    displayMetrics);

            // get left/right of the container
            int containerLeftOnWindow = Math.max(
                    Math.min(posX, (anchorRect.left - marginForHoverRing)), displayFrame.left);
            int containerRightOnWindow = Math.min(
                    Math.max((posX + contentWidth),(anchorRect.right + marginForHoverRing)),
                    displayFrame.right);

            // check where popup present.
            boolean isPopupAboveHorizontal;
            if (posY > anchorRect.centerY()) {
                isPopupAboveHorizontal = false;
            } else {
                isPopupAboveHorizontal = true;
            }

            // set width/height of the container
            if (mContentContainer == null) {
                mContentContainer = new HoverPopupContainer(mContext);
                mContentContainer.setBackgroundColor(Color.TRANSPARENT);
                mContentContainer.setGuideLine(mGuideRingDrawableId, mGuideLineColor);
            }

            // add a contentView to Container.
            ViewGroup.LayoutParams contentLP = mContentView.getLayoutParams();
            if (contentLP == null) {
                mContentView.setLayoutParams(new FrameLayout.LayoutParams(contentWidth, contentHeight));
            } else {
                contentLP.width = contentWidth;
                contentLP.height = contentHeight;
            }
            if (mContentContainer.getChildCount() == 0) {
                mContentContainer.addView(mContentView);
            }
            
            mPopup.setWidth(-2);   // Wrap_content
            mPopup.setHeight(-2);  // Wrap_content

            // set padding
            int containerPaddingLeft = Math.abs(containerLeftOnWindow - posX);
            int containerPaddingRight = Math.abs(containerRightOnWindow - (posX + contentWidth));
            int containerPaddingTop;
            if (isPopupAboveHorizontal) {
            	int containerPaddingBottom = (anchorRect.bottom + marginForHoverRing) - (posY + contentHeight);
            	mContentContainer.setPadding(containerPaddingLeft, 0, containerPaddingRight, containerPaddingBottom);

            	// set a container position on window
            	posX = containerLeftOnWindow;
            	//posY = posY;

            	// set Guide line, position is based on container. **Start : center
            	// of the contentView, End : hover point.
            	int hoverPointXonContainer = mHoveringPointX - posX - mWindowGapX;
            	int hoverPointYonContainer = mHoveringPointY - posY - mWindowGapY;
            	mContentContainer.setGuideLine((containerPaddingLeft + contentWidth / 2),
            			contentHeight - mGuideLineFadeOffset, hoverPointXonContainer,
            			hoverPointYonContainer, true);
            } else {
            	containerPaddingTop = (posY - (anchorRect.top - marginForHoverRing));
            	mContentContainer.setPadding(containerPaddingLeft, containerPaddingTop, containerPaddingRight, 0);

            	// set a container position on window
            	posX = containerLeftOnWindow;
            	posY = posY - containerPaddingTop;

            	// set Guide line, position is based on container. **Start : center
            	// of the contentView, End : hover point.
            	int hoverPointXonContainer = mHoveringPointX - posX - mWindowGapX;
            	int hoverPointYonContainer = mHoveringPointY - posY - mWindowGapY;
            	mContentContainer.setGuideLine((containerPaddingLeft + contentWidth / 2),
            			containerPaddingTop + mGuideLineFadeOffset, hoverPointXonContainer,
            			hoverPointYonContainer, true);
            }
        } else {
            mContentContainer = null;
        }
        
        mPopupPosX = posX;
        mPopupPosY = posY;
    }
    
    /**
     * Updates the hover popup window
     */
    public void updateHoverPopup() {
        updateHoverPopup(mParentView, mPopupGravity, mPopupOffsetX, mPopupOffsetY);
    }

    /**
     * Updates the hover popup window
     */
    public void updateHoverPopup(View anchor, int gravity, int offsetX, int offsetY) {
        if (mPopup == null) {
            if (DEBUG) Log.e(TAG, "updateHoverPopup(), returned dueto mPopup==null ");
            return;
        }

        // compute position.
        computePopupPosition(anchor, gravity, offsetX, offsetY);

        // set contents to Popup
        if (mIsGuideLineEnabled && mContentContainer != null) {
            mPopup.setContentView(mContentContainer);
        } else {
            mPopup.setContentView(mContentView);
        }

        if (mPopup.getContentView() == null) {
            if (DEBUG) Log.e(TAG, "updateHoverPopup(), returns dueto mPopup.getContentView()==null");
            return;
        }

        // set animation.
        mPopup.setAnimationStyle(mAnimationStyle);

        if (mPopup.isShowing()) {
            mPopup.update(mPopupPosX, mPopupPosY, -1, -1);
        } else {
            // If window type of current view is sub-panel, attach to window with ApplicationWindowToken. 

            mPopup.showAtLocation(anchor, Gravity.NO_GRAVITY, mPopupPosX, mPopupPosY);            
        }
    }

    /**
     * Change the animation style resource for this popup.
     *
     * @param aniStyle Animation style to use when the popup appears
     *      and disappears.
     */
    public void setAnimationStyle(int aniStyle) {
        mAnimationStyle = aniStyle;
    }
    

    /**
     * Sets a guide line on/off. the line is drawn between content view and hovering point.
     * 
     * @param enabled True to draw line.
     */
    public void setGuideLineEnabled(boolean enabled) {
        mIsGuideLineEnabled = enabled;
    }

    /**
     * Sets a length of fading edge of the content background image. 
     * This is for fitting a guide line and Content more closely.  
     * 
     * @param offset The length of fading edge
     */
    public void setGuideLineFadeOffset(int offset) {
        mGuideLineFadeOffset = convertDPtoPX(offset, null);
    }
    
    /**
     * Sets a guide line style   
     */
    public void setGuideLineStyle(int ringDrawable, int lineColor) {
        mGuideRingDrawableId = ringDrawable;
        mGuideLineColor = lineColor;
    }

    /**
     * This is called from onHoverEvent of the View.java.
     * Note that Do not call this method directly. 
     */
    /*
    public boolean onHoverEvent(MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_HOVER_MOVE) {
            setHoveringPoint((int) event.getRawX(), (int) event.getRawY());
            // update popup for moving guide line
            if (mIsGuideLineEnabled && isShowing()) {
                View popupView = mPopup.getContentView();
                if (popupView instanceof HoverPopupContainer) {
                    ((HoverPopupContainer) mPopup.getContentView()).setGuideLineEndPoint(
                            (int) event.getRawX() - mPopupPosX - mWindowGapX, (int) event.getRawY() - mPopupPosY - mWindowGapY);

                    ((HoverPopupContainer) popupView).updateDecoration();
                }
            }
        }
        return false;
    }*/

    /**
     * Dismiss the hover popup and clears handler messages 
     * This is for fitting a guide line and Content more closely.  
     */
    public void dismiss() {
        // if (getHandler().hasMessages(MSG_DISMISS_POPUP)) {
        // getHandler().removeMessages(MSG_DISMISS_POPUP);
        // }
        // getHandler().sendEmptyMessage(MSG_DISMISS_POPUP);
        dismissPopup();
    }

    /**
     * Dismiss the hover popup and clears handler messages 
     * This is for fitting a guide line and Content more closely.  
     */
    private void dismissPopup() {
        // remove pending message and dismiss popup
        getHandler().removeMessages(MSG_SHOW_POPUP);
        getHandler().removeMessages(MSG_DISMISS_POPUP);
        if (mPopup != null) {
            mPopup.dismiss();
        }
    }
    
    /**
     * Utility method to convert DP to Pixel. 
     */
    protected int convertDPtoPX(int dp, DisplayMetrics displayMetrics) {
        if (displayMetrics == null) {
            displayMetrics = mContext.getResources().getDisplayMetrics();
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics);
    }

    private Handler getHandler() {
        if (mHandler == null)
            mHandler = new HoverPopupHandler();
        return mHandler;
    }

    private class HoverPopupHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            if (DEBUG)
                Log.e(TAG, "handleMessage : " + ((msg.what == MSG_SHOW_POPUP) ? "SHOW" : "DISMISS"));

            switch (msg.what) {
                case MSG_SHOW_POPUP:
                    showPopup();
                    sendEmptyMessageDelayed(MSG_DISMISS_POPUP, POPUP_TIMEOUT_MS);
                    break;
                case MSG_DISMISS_POPUP:
                    dismissPopup();
                    break;
            }
        }
    };

    protected class HoverPopupContainer extends FrameLayout {

        static final String TAG = "HoverPopupContainer";

        static final boolean DEBUG = true;

        private int mLineStartX, mLineStartY;

        private int mLineEndX, mLineEndY;

//        private int mOldLineEndX = -1;

//        private int mOldLineEndY = -1;

//        private int mChildImageFadeHeight;

        private boolean mIsRingEnabled = false;

        private Paint mLinePaint;
        
        private Drawable mRingDrawable;
        
        private int mRingWidth, mRingHeight;

        public HoverPopupContainer(Context context) {
            super(context);
        }
        
        public void setGuideLine(int drawableId, int lineColor){
            mRingDrawable = getResources().getDrawable(drawableId);
            mRingWidth = mRingDrawable.getIntrinsicWidth();
            mRingHeight = mRingDrawable.getIntrinsicHeight();
            mRingDrawable.setBounds(0,0,mRingWidth,mRingHeight);
            
            mLinePaint = new Paint();
            mLinePaint.setStrokeWidth(3);
            mLinePaint.setStrokeCap(Cap.ROUND);
            mLinePaint.setColor(lineColor);
            mLinePaint.setAntiAlias(true);
        }

        public void updateDecoration() {
            if (DEBUG) Log.d(TAG, "HoverPopupContainer.updateContainer()");

            invalidate();
//            if (getPaddingTop() > getPaddingBottom()){
//                // invalidate upper area.
//                postInvalidate(0, 0, getWidth(), Math.max(mLineStartY, mLineEndY));
//            } else {
//                postInvalidate(0, Math.min(mLineStartY, mLineEndY), getWidth(), getHeight());
//            }

        }

        // this received position of the hover point.
        public void setGuideLine(int startX, int startY, int endX, int endY, boolean ringEnabled) {
            if (DEBUG)
                Log.d(TAG, "HoverPopupContainer.updateContainer()");

            mLineStartX = startX;
            mLineStartY = startY;
            mLineEndX = endX;
            mLineEndY = endY;
            mIsRingEnabled = ringEnabled;
        }

        public void setGuideLineEndPoint(int pointX, int pointY) {
            mLineEndX = pointX;
            mLineEndY = pointY;
        }

//        public void setChildImageFadeHeight(int height) {
//            mChildImageFadeHeight = height;
//        }
        
        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            
            // check precondition
            if (DEBUG)
                Log.d(TAG, "HoverPopupContainer.onDraw() RingEnabled : " + mIsRingEnabled
                        + " s.x : " + mLineStartX + " s.y : " + mLineStartY + " e.x : " + mLineEndX
                        + " e.y : " + mLineEndY + " Drawable : " + mRingDrawable);

            if (getChildCount() == 0 || getChildAt(0) == null) {
                return;
            }
            
            if (mRingDrawable == null){
                setGuideLine(R.drawable.hover_ic_point, 0xFF9ca2a9);
            }

            // draw ring
            if (mIsRingEnabled) {
                
                canvas.save();   
                canvas.translate((mLineEndX - mRingWidth/2), (mLineEndY - mRingHeight/2));
                mRingDrawable.draw(canvas);
                canvas.restore();
                
                if (mLineStartY < mLineEndY) {
                    canvas.drawLine(mLineStartX, mLineStartY, mLineEndX, (mLineEndY - mRingHeight/2 + 2), mLinePaint);
                } else if (mLineStartY > mLineEndY) {
                    canvas.drawLine(mLineStartX, mLineStartY, mLineEndX, (mLineEndY + mRingHeight/2 - 2), mLinePaint);
                }
            } else {
                // draw line
                canvas.drawLine(mLineStartX, mLineStartY, mLineEndX, mLineEndY, mLinePaint);
            }
        }

        protected boolean pointInValidPaddingArea(int localX, int localY) {
            boolean ret = false;

            if (getPaddingTop() > getPaddingBottom()) {
                if ((localX < getWidth()) && (localY <= getPaddingTop())) {
                    ret = true;
                }
            } else if (getPaddingTop() < getPaddingBottom()) {
                if ((localX < getWidth()) && (localY >= (getHeight() - getPaddingBottom()))) {
                    ret = true;
                }
            } else {
                ret = false;
            }

            return ret;
        }
    }
    
    final static public class Gravity {

        /** Constant indicating that no gravity has been set **/
        public static final int NO_GRAVITY = 0x00000000;
        /** Place the object in the center of its container in both the vertical and the horizontal axis**/
        public static final int CENTER = 0x00000011;

        
        /** Binary mask to get the absolute vertical gravity of a gravity. **/
        public static final int VERTICAL_GRAVITY_MASK = 0x0000F0F0;
        /** Place object in the vertical center of its container. **/
        public static final int CENTER_VERTICAL = 0x00000010;
        /** Place object to meet the top of its container. **/
        public static final int TOP = 0x00000030;
        /** Place object above its container. **/
        public static final int TOP_ABOVE = 0x00003030;
        /** Place object to meet the bottom of it's container. **/
        public static final int BOTTOM = 0x00000050;
        /** Place object under its container. **/
        public static final int BOTTOM_UNDER = 0x00005050;

        
        /** Binary mask to get the absolute horizontal gravity of a gravity. **/
        public static final int HORIZONTAL_GRAVITY_MASK = 0x00000F0F;
        /** Place object in the horizontal center of its container. **/
        public static final int CENTER_HORIZONTAL = 0x00000001;
        /** Place object to meet the left of its container **/
        public static final int LEFT = 0x00000003;
        /** Place object to meet its right side and container's center_horizontal **/
        public static final int LEFT_CENTER_AXIS = 0x00000103;
        /** Place object in the left of its container **/
        public static final int LEFT_OUTSIDE = 0x00000303;
        /** Place object to meet its left side and container's center_horizontal **/
        public static final int RIGHT_CENTER_AXIS = 0x00000105;
        /** Place object to meet the right of its container **/
        public static final int RIGHT = 0x00000005;
        /** Place object in the right of its container **/
        public static final int RIGHT_OUTSIDE = 0x00000505;
        
        /** Place object in the horizontal center of its container. **/
        public static final int CENTER_HORIZONTAL_ON_WINDOW = 0x00000101;
        /** Place object in the horizontal center of its container. **/
        public static final int CENTER_HORIZONTAL_ON_POINT = 0x00000201;
    }
}
