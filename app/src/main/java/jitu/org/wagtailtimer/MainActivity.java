package jitu.org.wagtailtimer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnLongClickListener {
    private TimerChan timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View button = findViewById(R.id.button_start);
        button.setOnLongClickListener(this);
        timer = new TimerChan(this);
        timer.checkUpdate();
        loadMenuItems();
    }

    private void loadMenuItems() {
        ArrayList<HashMap<String, String>> data = getListItems();
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"title", "duration"},
                new int[]{android.R.id.text1, android.R.id.text2});
        ListView view = (ListView) findViewById(R.id.list_item);
        view.setAdapter(adapter);
    }

    public ArrayList<HashMap<String, String>> getListItems() {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (TimerItem item : TimerItem.getSamples()) {
            HashMap<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.getTitle());
            datum.put("duration", formatTime(item.getDuration()));
            data.add(datum);
        }
        return data;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onClickMainButton(View view) {
        timer.onClickButton();
        updateButtonTitle();
    }

    private void updateButtonTitle() {
        Button button = (Button) findViewById(R.id.button_start);
        if (timer.isIdle()) {
            button.setText(R.string.start);
        } else if (timer.isStarted()) {
            button.setText(R.string.pause);
        } else if (timer.isPaused()) {
            button.setText(R.string.restart);
        }
    }

    public void onUpdateTimer(TimerChan timer) {
        TextView textClock = (TextView) findViewById(R.id.text_clock);
        long elapsed = timer.getRest();
        String str = formatTime(elapsed);
        textClock.setText(str);
    }

    private String formatTime(long sec) {
        long s = (sec / 1000) % 60;
        long m = (sec / 1000 / 60) % 60;
        long h = sec / 1000 / 60 / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    @Override
    public boolean onLongClick(View v) {
        View button = findViewById(R.id.button_start);
        if (v != button) {
            return false;
        }
        timer.reset();
        return true;
    }
}
