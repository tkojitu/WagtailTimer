package org.jitu.wagtailtimer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnLongClickListener {
    private static final int REQUEST_ACTION_GET_CONTENT = 11;

    private Coach coach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View button = findViewById(R.id.button_start);
        button.setOnLongClickListener(this);
        coach = new Coach(this);
        coach.onReset(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_open:
            return onOpen();
        case R.id.menu_settings:
            return onMenuSettings();
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean onOpen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        try {
            startActivityForResult(intent, REQUEST_ACTION_GET_CONTENT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ACTION_GET_CONTENT:
            if (resultCode == RESULT_OK) {
                String path = data.getData().getPath();
                coach.onReset(path);
            }
            break;
        default:
            break;
        }
    }

    public boolean showItems(ArrayList<MenuItem> items) {
        ArrayList<HashMap<String, String>> data = toItemMap(items);
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"title", "duration"},
                new int[]{android.R.id.text1, android.R.id.text2});
        ListView view = (ListView) findViewById(R.id.list_item);
        view.setAdapter(adapter);
        return true;
    }

    private ArrayList<HashMap<String, String>> toItemMap(ArrayList<MenuItem> items) {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        for (MenuItem item : items) {
            HashMap<String, String> datum = new HashMap<>(2);
            datum.put("title", item.getTitle());
            datum.put("duration", formatTime(item.getDuration()));
            data.add(datum);
        }
        return data;
    }

    private boolean onMenuSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    public void setTimerButtonText(int id) {
        Button button = (Button) findViewById(R.id.button_start);
        button.setText(id);
    }

    public void onClickTimerButton(View view) {
        coach.onClickTimerButton(((Button)view).getText());
    }

    public void setClock(long time) {
        String str = formatTime(time);
        TextView textClock = (TextView) findViewById(R.id.text_clock);
        textClock.setText(str);
    }

    public String formatTime(long msec) {
        long s = (msec / 1000) % 60;
        if (msec % 1000 > 0) {
            ++s;
        }
        long m = (msec / 1000 / 60) % 60;
        long h = msec / 1000 / 60 / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @Override
    public boolean onLongClick(View v) {
        coach.onReset(null);
        return true;
    }

    public String getResourceString(int id) {
        return getString(id);
    }

    public void onDestroy() {
        coach.onDestroy();
        super.onDestroy();
    }

    public String getSoundType() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.key_sound_list);
        return prefs.getString(key, "");
    }

    public Locale getLanguageLocale() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String key = getString(R.string.key_language_list);
        String value = prefs.getString(key, getString(R.string.lang_default));
        if (getString(R.string.lang_zh).equals(value)) {
            return Locale.CHINESE;
        } else if (getString(R.string.lang_en).equals(value)) {
            return Locale.ENGLISH;
        } else if (getString(R.string.lang_fr).equals(value)) {
            return Locale.FRENCH;
        } else if (getString(R.string.lang_ge).equals(value)) {
            return Locale.GERMAN;
        } else if (getString(R.string.lang_it).equals(value)) {
            return Locale.ITALIAN;
        } else if (getString(R.string.lang_ja).equals(value)) {
            return Locale.JAPANESE;
        } else if (getString(R.string.lang_ko).equals(value)) {
            return Locale.KOREAN;
        } else {
            return Locale.getDefault();
        }
    }
}
