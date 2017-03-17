package com.fanyu.boundless.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.List;

public class PopupList {
    private static final int DEFAULT_BACKGROUND_RADIUS_DP = 8;
    private static final int DEFAULT_DIVIDER_COLOR = -7829368;
    private static final int DEFAULT_DIVIDER_HEIGHT_PIXEL = 30;
    private static final int DEFAULT_DIVIDER_WIDTH_PIXEL = 1;
    private static final int DEFAULT_NORMAL_BACKGROUND_COLOR = -12303292;
    private static final int DEFAULT_NORMAL_TEXT_COLOR = -1;
    private static final int DEFAULT_PRESSED_BACKGROUND_COLOR = -8947849;
    private static final int DEFAULT_PRESSED_TEXT_COLOR = -1;
    private static final int DEFAULT_TEXT_PADDING_BOTTOM_DP = 8;
    private static final int DEFAULT_TEXT_PADDING_LEFT_DP = 10;
    private static final int DEFAULT_TEXT_PADDING_RIGHT_DP = 10;
    private static final int DEFAULT_TEXT_PADDING_TOP_DP = 8;
    private static final float DEFAULT_TEXT_SIZE_SP = 12.0f;
    private View mAnchorView;
    private int mBackgroundCornerRadius;
    private Context mContext;
    private int mContextPosition;
    private View mContextView;
    private StateListDrawable mCornerItemBackground;
    private int mDividerColor;
    private int mDividerHeight;
    private int mDividerWidth;
    private int mIndicatorHeight;
    private View mIndicatorView;
    private int mIndicatorWidth;
    private StateListDrawable mLeftItemBackground;
    private int mNormalBackgroundColor;
    private int mNormalTextColor;
    private OnPopupListClickListener mOnPopupListClickListener;
    private List<String> mPopupItemList;
    private PopupWindow mPopupWindow;
    private int mPopupWindowHeight;
    private int mPopupWindowWidth;
    private int mPressedBackgroundColor;
    private int mPressedTextColor;
    private float mRawX;
    private float mRawY;
    private StateListDrawable mRightItemBackground;
    private int mScreenHeight;
    private int mScreenWidth;
    private ColorStateList mTextColorStateList;
    private int mTextPaddingBottom;
    private int mTextPaddingLeft;
    private int mTextPaddingRight;
    private int mTextPaddingTop;
    private float mTextSize;

    public interface OnPopupListClickListener {
        void onPopupListClick(View view, int i, int i2);
    }

    public void init(Context context, View anchorView, List<String> popupItemList, OnPopupListClickListener onPopupListClickListener) {
        this.mNormalTextColor = -1;
        this.mPressedTextColor = -1;
        this.mTextSize = (float) sp2px(DEFAULT_TEXT_SIZE_SP);
        this.mTextPaddingLeft = dp2px(10.0f);
        this.mTextPaddingTop = dp2px(8.0f);
        this.mTextPaddingRight = dp2px(10.0f);
        this.mTextPaddingBottom = dp2px(8.0f);
        this.mNormalBackgroundColor = DEFAULT_NORMAL_BACKGROUND_COLOR;
        this.mPressedBackgroundColor = DEFAULT_PRESSED_BACKGROUND_COLOR;
        this.mBackgroundCornerRadius = dp2px(8.0f);
        this.mDividerColor = DEFAULT_DIVIDER_COLOR;
        this.mDividerWidth = 1;
        this.mDividerHeight = 30;
        this.mContext = context;
        this.mAnchorView = anchorView;
        this.mPopupItemList = popupItemList;
        this.mOnPopupListClickListener = onPopupListClickListener;
        this.mPopupWindow = null;
        this.mAnchorView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                PopupList.this.mRawX = event.getRawX();
                PopupList.this.mRawY = event.getRawY();
                return false;
            }
        });
        if (this.mAnchorView instanceof AbsListView) {
            ((AbsListView) this.mAnchorView).setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    PopupList.this.mContextView = view;
                    PopupList.this.mContextPosition = position;
                    PopupList.this.showPopupListWindow();
                    return true;
                }
            });
        } else {
            this.mAnchorView.setOnLongClickListener(new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    PopupList.this.mContextView = v;
                    PopupList.this.mContextPosition = 0;
                    PopupList.this.showPopupListWindow();
                    return true;
                }
            });
        }
        if (this.mScreenWidth == 0) {
            this.mScreenWidth = getScreenWidth();
        }
        if (this.mScreenHeight == 0) {
            this.mScreenHeight = getScreenHeight();
        }
        refreshBackgroundOrRadiusStateList();
        refreshTextColorStateList(this.mPressedTextColor, this.mNormalTextColor);
    }

    private void showPopupListWindow() {
        if (!(this.mContext instanceof Activity) || !((Activity) this.mContext).isFinishing()) {
            if (this.mPopupWindow == null) {
                LayoutParams layoutParams;
                LinearLayout contentView = new LinearLayout(this.mContext);
                contentView.setLayoutParams(new LayoutParams(-2, -2));
                contentView.setOrientation(1);
                LinearLayout popupListContainer = new LinearLayout(this.mContext);
                popupListContainer.setLayoutParams(new LayoutParams(-2, -2));
                popupListContainer.setOrientation(0);
                popupListContainer.setBackgroundDrawable(this.mCornerItemBackground);
                contentView.addView(popupListContainer);
                if (this.mIndicatorView != null) {
                    if (this.mIndicatorView.getLayoutParams() == null) {
                        layoutParams = new LayoutParams(-2, -2);
                    } else {
                        layoutParams = (LayoutParams) this.mIndicatorView.getLayoutParams();
                    }
                    layoutParams.gravity = 17;
                    this.mIndicatorView.setLayoutParams(layoutParams);
                    contentView.addView(this.mIndicatorView);
                }
                int i = 0;
                while (i < this.mPopupItemList.size()) {
                    TextView textView = new TextView(this.mContext);
                    textView.setTextColor(this.mTextColorStateList);
                    textView.setTextSize(0, this.mTextSize);
                    textView.setPadding(this.mTextPaddingLeft, this.mTextPaddingTop, this.mTextPaddingRight, this.mTextPaddingBottom);
                    textView.setClickable(true);
                    final int finalI = i;
                    textView.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            if (PopupList.this.mOnPopupListClickListener != null) {
                                PopupList.this.mOnPopupListClickListener.onPopupListClick(PopupList.this.mContextView, PopupList.this.mContextPosition, finalI);
                                PopupList.this.hidePopupListWindow();
                            }
                        }
                    });
                    textView.setText((CharSequence) this.mPopupItemList.get(i));
                    if (this.mPopupItemList.size() > 1 && i == 0) {
                        textView.setBackgroundDrawable(this.mLeftItemBackground);
                    } else if (this.mPopupItemList.size() > 1 && i == this.mPopupItemList.size() - 1) {
                        textView.setBackgroundDrawable(this.mRightItemBackground);
                    } else if (this.mPopupItemList.size() == 1) {
                        textView.setBackgroundDrawable(this.mCornerItemBackground);
                    } else {
                        textView.setBackgroundDrawable(getCenterItemBackground());
                    }
                    popupListContainer.addView(textView);
                    if (this.mPopupItemList.size() > 1 && i != this.mPopupItemList.size() - 1) {
                        View divider = new View(this.mContext);
                        layoutParams = new LayoutParams(this.mDividerWidth, this.mDividerHeight);
                        layoutParams.gravity = 17;
                        divider.setLayoutParams(layoutParams);
                        divider.setBackgroundColor(this.mDividerColor);
                        popupListContainer.addView(divider);
                    }
                    i++;
                }
                if (this.mPopupWindowWidth == 0) {
                    this.mPopupWindowWidth = getViewWidth(popupListContainer);
                }
                if (this.mIndicatorView != null && this.mIndicatorWidth == 0) {
                    if (this.mIndicatorView.getLayoutParams().width > 0) {
                        this.mIndicatorWidth = this.mIndicatorView.getLayoutParams().width;
                    } else {
                        this.mIndicatorWidth = getViewWidth(this.mIndicatorView);
                    }
                }
                if (this.mIndicatorView != null && this.mIndicatorHeight == 0) {
                    if (this.mIndicatorView.getLayoutParams().height > 0) {
                        this.mIndicatorHeight = this.mIndicatorView.getLayoutParams().height;
                    } else {
                        this.mIndicatorHeight = getViewHeight(this.mIndicatorView);
                    }
                }
                if (this.mPopupWindowHeight == 0) {
                    this.mPopupWindowHeight = getViewHeight(popupListContainer) + this.mIndicatorHeight;
                }
                this.mPopupWindow = new PopupWindow(contentView, this.mPopupWindowWidth, this.mPopupWindowHeight, true);
                this.mPopupWindow.setTouchable(true);
                this.mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            }
            if (this.mIndicatorView != null) {
                float marginLeftScreenEdge = this.mRawX;
                float marginRightScreenEdge = ((float) this.mScreenWidth) - this.mRawX;
                if (marginLeftScreenEdge < ((float) this.mPopupWindowWidth) / 2.0f) {
                    if (marginLeftScreenEdge < (((float) this.mIndicatorWidth) / 2.0f) + ((float) this.mBackgroundCornerRadius)) {
                        this.mIndicatorView.setTranslationX(((((float) this.mIndicatorWidth) / 2.0f) + ((float) this.mBackgroundCornerRadius)) - (((float) this.mPopupWindowWidth) / 2.0f));
                    } else {
                        this.mIndicatorView.setTranslationX(marginLeftScreenEdge - (((float) this.mPopupWindowWidth) / 2.0f));
                    }
                } else if (marginRightScreenEdge >= ((float) this.mPopupWindowWidth) / 2.0f) {
                    this.mIndicatorView.setTranslationX(0.0f);
                } else if (marginRightScreenEdge < (((float) this.mIndicatorWidth) / 2.0f) + ((float) this.mBackgroundCornerRadius)) {
                    this.mIndicatorView.setTranslationX(((((float) this.mPopupWindowWidth) / 2.0f) - (((float) this.mIndicatorWidth) / 2.0f)) - ((float) this.mBackgroundCornerRadius));
                } else {
                    this.mIndicatorView.setTranslationX((((float) this.mPopupWindowWidth) / 2.0f) - marginRightScreenEdge);
                }
            }
            this.mPopupWindow.showAtLocation(this.mAnchorView, 17, ((int) this.mRawX) - (this.mScreenWidth / 2), ((((int) this.mRawY) - (this.mScreenHeight / 2)) - this.mPopupWindowHeight) + this.mIndicatorHeight);
        }
    }

    private void refreshBackgroundOrRadiusStateList() {
        GradientDrawable leftItemPressedDrawable = new GradientDrawable();
        leftItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        leftItemPressedDrawable.setCornerRadii(new float[]{(float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, 0.0f, 0.0f, 0.0f, 0.0f, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius});
        GradientDrawable leftItemNormalDrawable = new GradientDrawable();
        leftItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        leftItemNormalDrawable.setCornerRadii(new float[]{(float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, 0.0f, 0.0f, 0.0f, 0.0f, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius});
        this.mLeftItemBackground = new StateListDrawable();
        this.mLeftItemBackground.addState(new int[]{16842919}, leftItemPressedDrawable);
        this.mLeftItemBackground.addState(new int[0], leftItemNormalDrawable);
        GradientDrawable rightItemPressedDrawable = new GradientDrawable();
        rightItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        rightItemPressedDrawable.setCornerRadii(new float[]{0.0f, 0.0f, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, 0.0f, 0.0f});
        GradientDrawable rightItemNormalDrawable = new GradientDrawable();
        rightItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        rightItemNormalDrawable.setCornerRadii(new float[]{0.0f, 0.0f, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, 0.0f, 0.0f});
        this.mRightItemBackground = new StateListDrawable();
        this.mRightItemBackground.addState(new int[]{16842919}, rightItemPressedDrawable);
        this.mRightItemBackground.addState(new int[0], rightItemNormalDrawable);
        GradientDrawable cornerItemPressedDrawable = new GradientDrawable();
        cornerItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        cornerItemPressedDrawable.setCornerRadii(new float[]{(float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius});
        GradientDrawable cornerItemNormalDrawable = new GradientDrawable();
        cornerItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        cornerItemNormalDrawable.setCornerRadii(new float[]{(float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius, (float) this.mBackgroundCornerRadius});
        this.mCornerItemBackground = new StateListDrawable();
        this.mCornerItemBackground.addState(new int[]{16842919}, cornerItemPressedDrawable);
        this.mCornerItemBackground.addState(new int[0], cornerItemNormalDrawable);
    }

    private StateListDrawable getCenterItemBackground() {
        StateListDrawable centerItemBackground = new StateListDrawable();
        GradientDrawable centerItemPressedDrawable = new GradientDrawable();
        centerItemPressedDrawable.setColor(this.mPressedBackgroundColor);
        GradientDrawable centerItemNormalDrawable = new GradientDrawable();
        centerItemNormalDrawable.setColor(this.mNormalBackgroundColor);
        centerItemBackground.addState(new int[]{16842919}, centerItemPressedDrawable);
        centerItemBackground.addState(new int[0], centerItemNormalDrawable);
        return centerItemBackground;
    }

    private void refreshTextColorStateList(int pressedTextColor, int normalTextColor) {
        states = new int[2][];
        states[0] = new int[]{16842919};
        states[1] = new int[0];
        this.mTextColorStateList = new ColorStateList(states, new int[]{pressedTextColor, normalTextColor});
    }

    public void hidePopupListWindow() {
        if ((!(this.mContext instanceof Activity) || !((Activity) this.mContext).isFinishing()) && this.mPopupWindow != null && this.mPopupWindow.isShowing()) {
            this.mPopupWindow.dismiss();
        }
    }

    public View getIndicatorView() {
        return this.mIndicatorView;
    }

    public View getDefaultIndicatorView(final float widthPixel, final float heightPixel, final int color) {
        ImageView indicator = new ImageView(this.mContext);
        indicator.setImageDrawable(new Drawable() {
            public void draw(Canvas canvas) {
                Path path = new Path();
                Paint paint = new Paint();
                paint.setColor(color);
                paint.setStyle(Style.FILL);
                path.moveTo(0.0f, 0.0f);
                path.lineTo(widthPixel, 0.0f);
                path.lineTo(widthPixel / 2.0f, heightPixel);
                path.close();
                canvas.drawPath(path, paint);
            }

            public void setAlpha(int alpha) {
            }

            public void setColorFilter(ColorFilter colorFilter) {
            }

            public int getOpacity() {
                return -3;
            }

            public int getIntrinsicWidth() {
                return (int) widthPixel;
            }

            public int getIntrinsicHeight() {
                return (int) heightPixel;
            }
        });
        return indicator;
    }

    public void setIndicatorView(View indicatorView) {
        this.mIndicatorView = indicatorView;
    }

    public void setIndicatorSize(int widthPixel, int heightPixel) {
        this.mIndicatorWidth = widthPixel;
        this.mIndicatorHeight = heightPixel;
        LayoutParams layoutParams = new LayoutParams(this.mIndicatorWidth, this.mIndicatorHeight);
        layoutParams.gravity = 17;
        if (this.mIndicatorView != null) {
            this.mIndicatorView.setLayoutParams(layoutParams);
        }
    }

    public int getNormalTextColor() {
        return this.mNormalTextColor;
    }

    public void setNormalTextColor(int normalTextColor) {
        this.mNormalTextColor = normalTextColor;
        refreshTextColorStateList(this.mPressedTextColor, this.mNormalTextColor);
    }

    public int getPressedTextColor() {
        return this.mPressedTextColor;
    }

    public void setPressedTextColor(int pressedTextColor) {
        this.mPressedTextColor = pressedTextColor;
        refreshTextColorStateList(this.mPressedTextColor, this.mNormalTextColor);
    }

    public float getTextSize() {
        return this.mTextSize;
    }

    public void setTextSize(float textSizePixel) {
        this.mTextSize = textSizePixel;
    }

    public int getTextPaddingLeft() {
        return this.mTextPaddingLeft;
    }

    public void setTextPaddingLeft(int textPaddingLeft) {
        this.mTextPaddingLeft = textPaddingLeft;
    }

    public int getTextPaddingTop() {
        return this.mTextPaddingTop;
    }

    public void setTextPaddingTop(int textPaddingTop) {
        this.mTextPaddingTop = textPaddingTop;
    }

    public int getTextPaddingRight() {
        return this.mTextPaddingRight;
    }

    public void setTextPaddingRight(int textPaddingRight) {
        this.mTextPaddingRight = textPaddingRight;
    }

    public int getTextPaddingBottom() {
        return this.mTextPaddingBottom;
    }

    public void setTextPaddingBottom(int textPaddingBottom) {
        this.mTextPaddingBottom = textPaddingBottom;
    }

    public void setTextPadding(int left, int top, int right, int bottom) {
        this.mTextPaddingLeft = left;
        this.mTextPaddingTop = top;
        this.mTextPaddingRight = right;
        this.mTextPaddingBottom = bottom;
    }

    public int getNormalBackgroundColor() {
        return this.mNormalBackgroundColor;
    }

    public void setNormalBackgroundColor(int normalBackgroundColor) {
        this.mNormalBackgroundColor = normalBackgroundColor;
        refreshBackgroundOrRadiusStateList();
    }

    public int getPressedBackgroundColor() {
        return this.mPressedBackgroundColor;
    }

    public void setPressedBackgroundColor(int pressedBackgroundColor) {
        this.mPressedBackgroundColor = pressedBackgroundColor;
        refreshBackgroundOrRadiusStateList();
    }

    public int getBackgroundCornerRadius() {
        return this.mBackgroundCornerRadius;
    }

    public void setBackgroundCornerRadius(int backgroundCornerRadiusPixel) {
        this.mBackgroundCornerRadius = backgroundCornerRadiusPixel;
        refreshBackgroundOrRadiusStateList();
    }

    public int getDividerColor() {
        return this.mDividerColor;
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    public void setDividerWidth(int dividerWidthPixel) {
        this.mDividerWidth = dividerWidthPixel;
    }

    public int getDividerHeight() {
        return this.mDividerHeight;
    }

    public void setDividerHeight(int dividerHeightPixel) {
        this.mDividerHeight = dividerHeightPixel;
    }

    public Resources getResources() {
        if (this.mContext == null) {
            return Resources.getSystem();
        }
        return this.mContext.getResources();
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private int getScreenHeight() {
        WindowManager wm = (WindowManager) this.mContext.getSystemService("window");
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    private int getViewWidth(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        return view.getMeasuredWidth();
    }

    private int getViewHeight(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
        return view.getMeasuredHeight();
    }

    public int dp2px(float value) {
        return (int) TypedValue.applyDimension(1, value, getResources().getDisplayMetrics());
    }

    public int sp2px(float value) {
        return (int) TypedValue.applyDimension(2, value, getResources().getDisplayMetrics());
    }
}
