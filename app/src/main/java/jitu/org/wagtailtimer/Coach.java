package jitu.org.wagtailtimer;

import android.widget.Toast;
import java.util.ArrayList;

public class Coach {
    private MainActivity activity;
    private TimerChan timer;
    private ArrayList<ItemChan> items = new ArrayList<ItemChan>();
    private MeganeChan megane;

    public Coach(MainActivity activity) {
        this.activity = activity;
        this.megane = new MeganeChan(activity);
    }

    public void loadLastMenu() {
        items = megane.loadLastMenu();
        if (!items.isEmpty()) {
            timer = new TimerChan(this, items.get(0).getDuration());
            timer.checkUpdate();
        }
        activity.showItems(items);
        activity.setMainButtonText(R.string.start);
    }

    public ArrayList<ItemChan> loadMenu(String path) {
        items = megane.loadMenu(path);
        if (!items.isEmpty()) {
            megane.saveLastMenu(items);
            timer = new TimerChan(this, items.get(0).getDuration());
            timer.checkUpdate();
        }
        activity.showItems(items);
        activity.setMainButtonText(R.string.start);
        return items;
    }

    public void onUpdateTimer(TimerChan timer) {
        String rest = timer.getRestString();
        activity.setClockText(rest);
    }

    public void onTimerStateChanged(TimerChan timer) {
        if (timer.isStopped()) {
            doNextItem();
        } else {
            updateButtonTitle(timer);
        }
    }

    private void doNextItem() {
        if (items.isEmpty()) {
            return;
        }
        items.remove(0);
        activity.showItems(items);
        if (items.isEmpty()) {
            activity.playSoundMenu();
            activity.setMainButtonText(R.string.reset_menu);
        } else {
            activity.playSoundItem();
            timer = new TimerChan(this, items.get(0).getDuration());
            timer.start();
        }
        timer.checkUpdate();
    }

    private void updateButtonTitle(TimerChan timer) {
        if (timer.isIdle()) {
            activity.setMainButtonText(R.string.start);
        } else if (timer.isStarted()) {
            activity.setMainButtonText(R.string.pause);
        } else if (timer.isPaused()) {
            activity.setMainButtonText(R.string.restart);
        }
    }

    public ArrayList<ItemChan> getItems() {
        return items;
    }

    public void onClickTimerButton() {
        if (timer == null || timer.isStopped()) {
            loadLastMenu();
            if (getItems().isEmpty()) {
                Toast.makeText(activity, "Missing last menu.", Toast.LENGTH_LONG).show();
            }
        } else {
            timer.onClickButton();
        }
    }
    public void resetTimer() {
        if (timer == null) {
            return;
        }
        timer.reset();
    }
}
