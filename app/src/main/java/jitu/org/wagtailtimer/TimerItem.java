package jitu.org.wagtailtimer;

import java.util.ArrayList;

public class TimerItem {
    private String title;
    private long duration;

    public TimerItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        title = value;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long value) {
        duration = value;
    }
}
