package jitu.org.wagtailtimer;

import java.util.ArrayList;

public class TimerItem {
    private String title;
    private long duration;

    public TimerItem(String title, long duration) {
        this.title = title;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public static ArrayList<TimerItem> getSamples() {
        ArrayList results = new ArrayList();
        results.add(new TimerItem("Squat 1st", 1000 * 5));
        results.add(new TimerItem("Rest", 1000 * 3));
        results.add(new TimerItem("Squat 2nd", 1000 * 5));
        return results;
    }
}
