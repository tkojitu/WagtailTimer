package jitu.org.wagtailtimer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnLongClickListener {
    private static final int REQUEST_ACTION_GET_CONTENT = 11;

    private Coach coach;
    private boolean usingSpeech = true;

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
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (MenuItem item : items) {
            HashMap<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.getTitle());
            datum.put("duration", formatTime(item.getDuration()));
            data.add(datum);
        }
        return data;
    }

    private boolean onMenuSettings() {
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
    }

    public boolean usesSpeech() {
        return usingSpeech;
    }
}
