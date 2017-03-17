package com.google.android.exoplayer2.text.cea;

import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import java.util.Collections;
import java.util.List;

final class CeaSubtitle implements Subtitle {
    private final List<Cue> cues;

    public CeaSubtitle(Cue cue) {
        if (cue == null) {
            this.cues = Collections.emptyList();
        } else {
            this.cues = Collections.singletonList(cue);
        }
    }

    public int getNextEventTimeIndex(long timeUs) {
        return 0;
    }

    public int getEventTimeCount() {
        return 1;
    }

    public long getEventTime(int index) {
        return 0;
    }

    public List<Cue> getCues(long timeUs) {
        return this.cues;
    }
}
