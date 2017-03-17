package com.fanyu.boundless.util;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class KPSwitchConflictUtil {
    public static int mFlag;

    public interface SwitchClickListener {
        void onClickSwitch(boolean z, int i);
    }

    public static void attach(final View panelLayout, View switchBtnOne, View switchBtnTwo, View switchBtnThree, final View focusView, final SwitchClickListener switchClickListener) {
        Activity activity = (Activity) panelLayout.getContext();
        if (switchBtnOne != null) {
            switchBtnOne.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (KPSwitchConflictUtil.mFlag == 0) {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(true, 1);
                        }
                        KPSwitchConflictUtil.showPanel(panelLayout);
                        KPSwitchConflictUtil.mFlag = 1;
                    } else if (KPSwitchConflictUtil.mFlag == 1) {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(false, 1);
                        }
                        KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
                    } else {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(true, 1);
                        }
                        KPSwitchConflictUtil.showPanel(panelLayout);
                        KPSwitchConflictUtil.mFlag = 1;
                    }
                }
            });
        }
        if (switchBtnTwo != null) {
            switchBtnTwo.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.d("mFlag", " switchBtnTwo  mFlag ==  " + KPSwitchConflictUtil.mFlag);
                    if (KPSwitchConflictUtil.mFlag == 0) {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(true, 2);
                        }
                        KPSwitchConflictUtil.showPanel(panelLayout);
                        KPSwitchConflictUtil.mFlag = 2;
                    } else if (KPSwitchConflictUtil.mFlag == 2) {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(false, 2);
                        }
                        KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
                    } else {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(true, 2);
                        }
                        KPSwitchConflictUtil.showPanel(panelLayout);
                        KPSwitchConflictUtil.mFlag = 2;
                    }
                }
            });
        }
        if (switchBtnThree != null) {
            switchBtnThree.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (KPSwitchConflictUtil.mFlag == 0) {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(true, 3);
                        }
                        KPSwitchConflictUtil.showPanel(panelLayout);
                        KPSwitchConflictUtil.mFlag = 3;
                    } else if (KPSwitchConflictUtil.mFlag == 3) {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(false, 3);
                        }
                        KPSwitchConflictUtil.showKeyboard(panelLayout, focusView);
                    } else {
                        if (switchClickListener != null) {
                            switchClickListener.onClickSwitch(true, 3);
                        }
                        KPSwitchConflictUtil.showPanel(panelLayout);
                        KPSwitchConflictUtil.mFlag = 3;
                    }
                }
            });
        }
        if (isHandleByPlaceholder(activity)) {
            focusView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1) {
                        panelLayout.setVisibility(4);
                    }
                    return false;
                }
            });
        }
    }

    public static void showPanel(View panelLayout) {
        KeyboardUtil.hideKeyboard(((Activity) panelLayout.getContext()).getCurrentFocus());
    }

    public static void showKeyboard(View panelLayout, View focusView) {
        mFlag = 0;
        KeyboardUtil.showKeyboard(focusView);
    }

    public static boolean switchPanelAndKeyboard(View panelLayout, View focusView) {
        boolean switchToPanel = panelLayout.getVisibility() != 0;
        if (switchToPanel) {
            showPanel(panelLayout);
        } else {
            showKeyboard(panelLayout, focusView);
        }
        return switchToPanel;
    }

    public static void hidePanelAndKeyboard(View panelLayout) {
        Activity activity = (Activity) panelLayout.getContext();
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
            focusView.clearFocus();
        }
        mFlag = 0;
        panelLayout.setVisibility(8);
    }

    public static boolean isHandleByPlaceholder(boolean isFullScreen, boolean isTranslucentStatus, boolean isFitsSystem) {
        return isFullScreen || (isTranslucentStatus && !isFitsSystem);
    }

    static boolean isHandleByPlaceholder(Activity activity) {
        return isHandleByPlaceholder(ViewUtil.isFullScreen(activity), ViewUtil.isTranslucentStatus(activity), ViewUtil.isFitsSystemWindows(activity));
    }
}
