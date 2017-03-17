package fm.jiecao.jcvideoplayer_lib;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class JCVideoPlayerManager {
    public static HashMap<String, WeakReference<JCMediaPlayerListener>> FIRST_FLOOR_LIST = new HashMap();
    public static WeakReference<JCMediaPlayerListener> SECOND_FLOOR;

    public static void putFirstFloor(JCMediaPlayerListener jcMediaPlayerListener) {
        if (jcMediaPlayerListener.getScreenType() != 2 && jcMediaPlayerListener.getScreenType() != 3) {
            FIRST_FLOOR_LIST.put(jcMediaPlayerListener.getUrl(), new WeakReference(jcMediaPlayerListener));
        }
    }

    public static void putSecondFloor(JCMediaPlayerListener jcMediaPlayerListener) {
        if (jcMediaPlayerListener == null) {
            SECOND_FLOOR = null;
        } else {
            SECOND_FLOOR = new WeakReference(jcMediaPlayerListener);
        }
    }

    public static JCMediaPlayerListener getCurrentJcvd() {
        if (getCurrentJcvdOnSecondFloor() != null) {
            return getCurrentJcvdOnSecondFloor();
        }
        return getCurrentJcvdOnFirtFloor();
    }

    public static JCMediaPlayerListener getCurrentJcvdOnFirtFloor() {
        if (FIRST_FLOOR_LIST.get(JCMediaManager.CURRENT_PLAYING_URL) != null) {
            return (JCMediaPlayerListener) ((WeakReference) FIRST_FLOOR_LIST.get(JCMediaManager.CURRENT_PLAYING_URL)).get();
        }
        return null;
    }

    public static JCMediaPlayerListener getCurrentJcvdOnSecondFloor() {
        if (SECOND_FLOOR != null) {
            return (JCMediaPlayerListener) SECOND_FLOOR.get();
        }
        return null;
    }

    public static void completeAll() {
        if (!(SECOND_FLOOR == null || SECOND_FLOOR.get() == null)) {
            ((JCMediaPlayerListener) SECOND_FLOOR.get()).onCompletion();
            putSecondFloor(null);
        }
        for (String s : FIRST_FLOOR_LIST.keySet()) {
            if (!(FIRST_FLOOR_LIST.get(s) == null || ((WeakReference) FIRST_FLOOR_LIST.get(s)).get() == null || ((JCMediaPlayerListener) ((WeakReference) FIRST_FLOOR_LIST.get(s)).get()).getState() == 0)) {
                ((JCMediaPlayerListener) ((WeakReference) FIRST_FLOOR_LIST.get(s)).get()).onCompletion();
            }
        }
    }
}
