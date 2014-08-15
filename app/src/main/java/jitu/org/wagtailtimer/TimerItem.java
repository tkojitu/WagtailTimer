package jitu.org.wagtailtimer;

import java.util.ArrayList;
import java.util.List;

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

    public static List<TimerItem> getSamples() {
        List results = new ArrayList();
        results.add(new TimerItem("Squat 1st", 1000 * 60));
        results.add(new TimerItem("Rest", 1000 * 30));
        results.add(new TimerItem("Squat 2nd", 1000 * 60));
        return results;
    }
}
