package jitu.org.wagtailtimer;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MeganeChan {
    private static final String FILE_LAST_MENU = "last_menu.txt";

    private MainActivity activity;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public MeganeChan(MainActivity activity) {
        this.activity = activity;
    }

    public ArrayList<MenuItem> loadMenu(String path) {
        try {
            FileInputStream is;
            if (path == null) {
                is = activity.openFileInput(FILE_LAST_MENU);
            } else {
                is = new FileInputStream(path);
            }
            ArrayList<MenuItem> items = loadMenuFromStream(is);
            if (!items.isEmpty()) {
                saveLastMenu(items);
            }
            return items;
        } catch (FileNotFoundException e) {
            return loadRawMenu();
        }
    }

    private ArrayList<MenuItem> loadMenuFromStream(InputStream is) {
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(is));
        return parseMenuReader(reader);
    }

    private ArrayList<MenuItem> loadRawMenu() {
        InputStream is = activity.getResources().openRawResource(R.raw.default_menu);
        return loadMenuFromStream(is);
    }

    private ArrayList<MenuItem> parseMenuReader(BufferedReader reader) {
        ArrayList<MenuItem> results = new ArrayList<MenuItem>();
        MenuItem item = new MenuItem();
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
                    long msec = dateToMsec(date);
                    item.setDuration(msec);
                    results.add(item);
                    item = new MenuItem();
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

    private long dateToMsec(Date date) {
        long h = date.getHours() * 60 * 60;
        long m = date.getMinutes() * 60;
        long s = date.getSeconds();
        return (h + m + s) * 1000;
    }

    public void saveLastMenu(ArrayList<MenuItem> items) {
        if (items.isEmpty()) {
            return;
        }
        try {
            FileOutputStream fos = activity.openFileOutput(FILE_LAST_MENU, Context.MODE_PRIVATE);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            try {
                for (MenuItem item : items) {
                    writeItem(writer, item);
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

    private void writeItem(BufferedWriter writer, MenuItem item) throws IOException {
        writer.write(item.getTitle(), 0, item.getTitle().length());
        writer.newLine();
        String duration = activity.formatTime(item.getDuration());
        writer.write(duration, 0, duration.length());
        writer.newLine();
        writer.newLine();
    }
}
