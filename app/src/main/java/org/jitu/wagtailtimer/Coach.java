package org.jitu.wagtailtimer;

import java.util.ArrayList;

public class Coach {
    private MainActivity activity;
    private MeganeChan megane;
    private MegahonChan megahon;
    private StateChan state = new StateChan(this);
    private TokeiChan tokei = new TokeiChan(this);
    private ArrayList<MenuItem> items = new ArrayList<>();
    private long startedTime;

    public Coach(MainActivity activity) {
        this.activity = activity;
        this.megane = new MeganeChan(activity);
        this.megahon = new MegahonChan(activity);
    }

    public void loadMenu(String path) {
        items = megane.loadMenu(path);
        activity.showItems(items);
        activity.setTimerButtonText(R.string.start);
        setClockTextInitial();
    }

    private void setClockTextInitial() {
        long duration = 0;
        if (!items.isEmpty()) {
            MenuItem item = items.get(0);
            duration = item.getDuration();
        }
        activity.setClock(duration);
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }

    public void onClickTimerButton(CharSequence seq) {
        String str = seq.toString();
        if (activity.getResourceString(R.string.start).equals(str)) {
            state.onStart();
        } else if (activity.getResourceString(R.string.pause).equals(str)) {
            state.onPause();
        } else if (activity.getResourceString(R.string.restart).equals(str)) {
            state.onRestart();
        } else if (activity.getResourceString(R.string.reset_menu).equals(str)) {
            state.onReset(null);
        }
    }

    public void onReset(String path) {
        state.onReset(path);
    }

    public void onTimer() {
        if (state == null) {
            return;
        }
        state.onTimer();
    }

    public void  start() {
        startedTime = System.currentTimeMillis();
        activity.setTimerButtonText(R.string.pause);
        megahon.shoutStart(items.get(0));
    }

    public void pause() {
        if (items.isEmpty()) {
            return;
        }
        MenuItem item = items.get(0);
        item.setDuration(item.getDuration() - (System.currentTimeMillis() - startedTime));
        activity.setTimerButtonText(R.string.restart);
    }

    public void restart() {
        startedTime = System.currentTimeMillis();
        activity.setTimerButtonText(R.string.pause);
    }

    public void update() {
        long elapsed = System.currentTimeMillis() - startedTime;
        MenuItem item = items.get(0);
        if (item.getDuration() < elapsed) {
            nextItem();
            return;
        }
        long rest = item.getDuration() - elapsed;
        if (rest < 0) {
            rest = 0;
        }
        activity.setClock(rest);
    }

    public void nextItem() {
        items.remove(0);
        if (items.isEmpty()) {
            megahon.shoutFinish();
            activity.setTimerButtonText(R.string.reset_menu);
            activity.setClock(0);
        } else {
            startedTime = System.currentTimeMillis();
            setClockTextInitial();
            megahon.shoutStart(items.get(0));
        }
        activity.showItems(items);
    }

    public void onDestroy() {
        tokei.cancel();
        megahon.shutdown();
    }
}
