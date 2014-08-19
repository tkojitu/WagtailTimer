package jitu.org.wagtailtimer;

import java.util.ArrayList;

public class ItemChan {
    private String title;
    private long duration;

    public ItemChan() {
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

    public String getDurationString() {
        return formatTime(duration);
    }

    public static String formatTime(long msec) {
        long s = (msec / 1000) % 60;
        long m = (msec / 1000 / 60) % 60;
        long h = msec / 1000 / 60 / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
