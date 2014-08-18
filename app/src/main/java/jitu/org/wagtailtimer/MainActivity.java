package jitu.org.wagtailtimer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnLongClickListener {
    private static final int REQUEST_ACTION_GET_CONTENT = 11;

    private TimerChan timer;
    private ArrayList<TimerItem> items = new ArrayList<TimerItem>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private ToneGenerator generator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View button = findViewById(R.id.button_start);
        button.setOnLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_open:
            return onOpen();
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
            Toast.makeText(this, R.string.missing_file_manager, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_ACTION_GET_CONTENT:
            if (resultCode == RESULT_OK) {
                onActionGetContent(data);
            }
            break;
        default:
            break;
        }
    }

    private void onActionGetContent(Intent data) {
        if (!loadMenu(data)) {
            return;
        }
        showItems();
        if (items.isEmpty()) {
            return;
        }
        timer = new TimerChan(this, items.get(0).getDuration());
        timer.checkUpdate();
        setMainButtonText(R.string.start);
    }

    private void setMainButtonText(int id) {
        Button button = (Button) findViewById(R.id.button_start);
        button.setText(id);
    }

    private boolean loadMenu(Intent data) {
        String path = data.getData().getPath();
        items = parseMenuFile(path);
        return true;
    }

    private ArrayList<TimerItem> parseMenuFile(String path) {
        ArrayList<TimerItem> results = new ArrayList<TimerItem>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(path));
            TimerItem item = new TimerItem();
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
                        item = new TimerItem();
                    } catch (ParseException e) {
                        item.setTitle(line);
                    }
                }
                return results;
            } catch (IOException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                return results;
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            return results;
        }
    }

    private boolean showItems() {
        ArrayList<HashMap<String, String>> data = getItemMap();
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"title", "duration"},
                new int[]{android.R.id.text1, android.R.id.text2});
        ListView view = (ListView) findViewById(R.id.list_item);
        view.setAdapter(adapter);
        return true;
    }

    public ArrayList<HashMap<String, String>> getItemMap() {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (TimerItem item : items) {
            HashMap<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.getTitle());
            datum.put("duration", formatTime(item.getDuration()));
            data.add(datum);
        }
        return data;
    }

    public void onClickMainButton(View view) {
        if (timer == null || timer.isStopped()) {
            onOpen();
        } else {
            timer.onClickButton();
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
        showItems();
        if (items.isEmpty()) {
            generator.startTone(ToneGenerator.TONE_SUP_ERROR, 3000);
            setMainButtonText(R.string.reset_menu);
        } else {
            generator.startTone(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE);
            timer = new TimerChan(this, items.get(0).getDuration());
            timer.start();
        }
        timer.checkUpdate();
    }

    private void updateButtonTitle(TimerChan timer) {
        if (timer.isIdle()) {
            setMainButtonText(R.string.start);
        } else if (timer.isStarted()) {
            setMainButtonText(R.string.pause);
        } else if (timer.isPaused()) {
            setMainButtonText(R.string.restart);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (timer == null) {
            return false;
        }
        View button = findViewById(R.id.button_start);
        if (v != button) {
            return false;
        }
        timer.reset();
        return true;
    }
}
