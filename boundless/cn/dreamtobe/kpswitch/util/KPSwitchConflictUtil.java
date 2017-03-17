package cn.dreamtobe.kpswitch.util;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class KPSwitchConflictUtil {

    public static class SubPanelAndTrigger {
        final View subPanelView;
        final View triggerView;

        public SubPanelAndTrigger(View subPanelView, View triggerView) {
            this.subPanelView = subPanelView;
            this.triggerView = triggerView;
        }
    }

    public interface SwitchClickListener {
        void onClickSwitch(boolean z);
    }

    public static void attach(View panelLayout, View switchPanelKeyboardBtn, View focusView) {
        attach(panelLayout, switchPanelKeyboardBtn, focusView, null);
    }

    public static void attach(final View panelLayout, View switchPanelKeyboardBtn, final View focusView, final SwitchClickListener switchClickListener) {
        Activity activity = (Activity) panelLayout.getContext();
        if (switchPanelKeyboardBtn != null) {
            switchPanelKeyboardBtn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    boolean switchToPanel = KPSwitchConflictUtil.switchPanelAndKeyboard(panelLayout, focusView);
                    if (switchClickListener != null) {
                        switchClickListener.onClickSwitch(switchToPanel);
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

    public static void attach(View panelLayout, View focusView, SubPanelAndTrigger... subPanelAndTriggers) {
        attach(panelLayout, focusView, null, subPanelAndTriggers);
    }

    public static void attach(final View panelLayout, View focusView, SwitchClickListener switchClickListener, SubPanelAndTrigger... subPanelAndTriggers) {
        Activity activity = (Activity) panelLayout.getContext();
        for (SubPanelAndTrigger subPanelAndTrigger : subPanelAndTriggers) {
            bindSubPanel(subPanelAndTrigger, subPanelAndTriggers, focusView, panelLayout, switchClickListener);
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
        Activity activity = (Activity) panelLayout.getContext();
        panelLayout.setVisibility(0);
        if (activity.getCurrentFocus() != null) {
            KeyboardUtil.hideKeyboard(activity.getCurrentFocus());
        }
    }

    public static void showKeyboard(View panelLayout, View focusView) {
        Activity activity = (Activity) panelLayout.getContext();
        KeyboardUtil.showKeyboard(focusView);
        if (isHandleByPlaceholder(activity)) {
            panelLayout.setVisibility(4);
        }
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
        panelLayout.setVisibility(8);
    }

    public static boolean isHandleByPlaceholder(boolean isFullScreen, boolean isTranslucentStatus, boolean isFitsSystem) {
        return isFullScreen || (isTranslucentStatus && !isFitsSystem);
    }

    static boolean isHandleByPlaceholder(Activity activity) {
        return isHandleByPlaceholder(ViewUtil.isFullScreen(activity), ViewUtil.isTranslucentStatus(activity), ViewUtil.isFitsSystemWindows(activity));
    }

    private static void bindSubPanel(SubPanelAndTrigger subPanelAndTrigger, SubPanelAndTrigger[] subPanelAndTriggers, View focusView, View panelLayout, SwitchClickListener switchClickListener) {
        View triggerView = subPanelAndTrigger.triggerView;
        final View boundTriggerSubPanelView = subPanelAndTrigger.subPanelView;
        final View view = panelLayout;
        final View view2 = focusView;
        final SubPanelAndTrigger[] subPanelAndTriggerArr = subPanelAndTriggers;
        final SwitchClickListener switchClickListener2 = switchClickListener;
        triggerView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Boolean switchToPanel = null;
                if (view.getVisibility() != 0) {
                    KPSwitchConflictUtil.showPanel(view);
                    switchToPanel = Boolean.valueOf(true);
                    KPSwitchConflictUtil.showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggerArr);
                } else if (boundTriggerSubPanelView.getVisibility() == 0) {
                    KPSwitchConflictUtil.showKeyboard(view, view2);
                    switchToPanel = Boolean.valueOf(false);
                } else {
                    KPSwitchConflictUtil.showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggerArr);
                }
                if (switchClickListener2 != null && switchToPanel != null) {
                    switchClickListener2.onClickSwitch(switchToPanel.booleanValue());
                }
            }
        });
    }

    private static void showBoundTriggerSubPanel(View boundTriggerSubPanelView, SubPanelAndTrigger[] subPanelAndTriggers) {
        for (SubPanelAndTrigger panelAndTrigger : subPanelAndTriggers) {
            if (panelAndTrigger.subPanelView != boundTriggerSubPanelView) {
                panelAndTrigger.subPanelView.setVisibility(8);
            }
        }
        boundTriggerSubPanelView.setVisibility(0);
    }
}
