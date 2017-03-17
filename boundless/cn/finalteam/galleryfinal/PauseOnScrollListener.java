package cn.finalteam.galleryfinal;

import android.app.Activity;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class PauseOnScrollListener implements OnScrollListener {
    private final OnScrollListener externalListener;
    private final boolean pauseOnFling;
    private final boolean pauseOnScroll;

    public abstract void pause();

    public abstract void resume();

    public Activity getActivity() {
        return Global.mPhotoSelectActivity;
    }

    public PauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        this(pauseOnScroll, pauseOnFling, null);
    }

    protected PauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling, OnScrollListener customListener) {
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        this.externalListener = customListener;
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case 0:
                resume();
                break;
            case 1:
                if (this.pauseOnScroll) {
                    pause();
                    break;
                }
                break;
            case 2:
                if (this.pauseOnFling) {
                    pause();
                    break;
                }
                break;
        }
        if (this.externalListener != null) {
            this.externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (this.externalListener != null) {
            this.externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
