package jitu.org.wagtailtimer;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Coach {
    private static final String FILE_LAST_MENU = "last_menu.txt";

    private MainActivity activity;
    private TimerChan timer;
    private ArrayList<ItemChan> items = new ArrayList<ItemChan>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public Coach(MainActivity activity) {
        this.activity = activity;
    }

    public void loadLastMenu() {
        try {
            FileInputStream is = activity.openFileInput(FILE_LAST_MENU);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            items = parseMenuReader(reader);
            if (!items.isEmpty()) {
                timer = new TimerChan(this, items.get(0).getDuration());
                timer.checkUpdate();
            }
            activity.showItems(items);
            activity.setMainButtonText(R.string.start);
        } catch (FileNotFoundException ignored) {
        }
    }

    private ArrayList<ItemChan> parseMenuReader(BufferedReader reader) {
        ArrayList<ItemChan> results = new ArrayList<ItemChan>();
        ItemChan item = new ItemChan();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    continue;
                }
                try {
                    Date date = format.parse(line);
                    long h = date.getHours() * 60 * 60;
                    long m = date.getMinutes() * 60;
                    long s = date.getSeconds();
                    item.setDuration((h + m + s) * 1000);
                    results.add(item);
                    item = new ItemChan();
                } catch (ParseException e) {
                    item.setTitle(line);
                }
            }
            return results;
        } catch (IOException e) {
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return results;
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Toast.makeText(activity, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public ArrayList<ItemChan> loadMenu(String path) {
        items = parseMenuFile(path);
        if (!items.isEmpty()) {
            saveLastMenu();
            timer = new TimerChan(this, items.get(0).getDuration());
            timer.checkUpdate();
        }
        activity.showItems(items);
        activity.setMainButtonText(R.string.start);
        return items;
    }

    private ArrayList<ItemChan> parseMenuFile(String path) {
        ArrayList<ItemChan> results = new ArrayList<ItemChan>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            return parseMenuReader(reader);
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return results;
        }
    }

    private void saveLastMenu() {
        try {
            FileOutputStream fos = activity.openFileOutput(FILE_LAST_MENU, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            try {
                for (ItemChan item : items) {
                    writer.write(item.getTitle(), 0, item.getTitle().length());
                    writer.newLine();
                    String duration = item.getDurationString();
                    writer.write(duration, 0, duration.length());
                    writer.newLine();
                    writer.newLine();
                }
            } catch (IOException e) {
                Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
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
