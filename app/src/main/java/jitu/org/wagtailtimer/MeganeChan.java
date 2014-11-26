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

public class MeganeChan {
    private static final String FILE_LAST_MENU = "last_menu.txt";

    private MainActivity activity;
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public MeganeChan(MainActivity activity) {
        this.activity = activity;
    }

    public ArrayList<ItemChan> loadLastMenu() {
        try {
            FileInputStream is = activity.openFileInput(FILE_LAST_MENU);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return parseMenuReader(reader);
        } catch (FileNotFoundException ignored) {
            return new ArrayList<ItemChan>();
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
        ArrayList<ItemChan> results = new ArrayList<ItemChan>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            return parseMenuReader(reader);
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return results;
        }
    }

    public void saveLastMenu(ArrayList<ItemChan> items) {
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
}
