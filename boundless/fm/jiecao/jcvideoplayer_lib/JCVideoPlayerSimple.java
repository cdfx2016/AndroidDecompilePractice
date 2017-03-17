package fm.jiecao.jcvideoplayer_lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class JCVideoPlayerSimple extends JCVideoPlayer {
    public JCVideoPlayerSimple(Context context) {
        super(context);
    }

    public JCVideoPlayerSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getLayoutId() {
        return R.layout.jc_layout_base;
    }

    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        if (this.currentScreen == 2) {
            this.fullscreenButton.setImageResource(R.drawable.jc_shrink);
        } else {
            this.fullscreenButton.setImageResource(R.drawable.jc_enlarge);
        }
        this.fullscreenButton.setVisibility(8);
    }

    public void setUiWitStateAndScreen(int state) {
        super.setUiWitStateAndScreen(state);
        switch (this.currentState) {
            case 0:
                this.startButton.setVisibility(0);
                break;
            case 1:
                this.startButton.setVisibility(4);
                break;
            case 2:
                this.startButton.setVisibility(0);
                break;
        }
        updateStartImage();
    }

    private void updateStartImage() {
        if (this.currentState == 2) {
            this.startButton.setImageResource(R.drawable.jc_click_pause_selector);
        } else if (this.currentState == 7) {
            this.startButton.setImageResource(R.drawable.jc_click_error_selector);
        } else {
            this.startButton.setImageResource(R.drawable.jc_click_play_selector);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.fullscreen && this.currentState == 0) {
            Toast.makeText(getContext(), "Play video first", 0).show();
        } else {
            super.onClick(v);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser && this.currentState == 0) {
            Toast.makeText(getContext(), "Play video first", 0).show();
        } else {
            super.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    public boolean downStairs() {
        return false;
    }
}
