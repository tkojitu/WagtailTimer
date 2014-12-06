package jitu.org.wagtailtimer;

public class MenuItem {
    private String title = "";
    private long duration;

    public MenuItem() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String value) {
        if (value == null) {
            title = "";
        } else {
            title = value;
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long value) {
        duration = value;
    }
}
