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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements View.OnLongClickListener {
    private static final int REQUEST_ACTION_GET_CONTENT = 11;

    private Coach coach;
    private ToneGenerator generator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View button = findViewById(R.id.button_start);
        button.setOnLongClickListener(this);
        coach = new Coach(this);
        coach.loadLastMenu();
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
                coach.loadMenu(path);
            }
            break;
        default:
            break;
        }
    }

    public boolean showItems(ArrayList<ItemChan> items) {
        ArrayList<HashMap<String, String>> data = toItemMap(items);
        SimpleAdapter adapter = new SimpleAdapter(this, data,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"title", "duration"},
                new int[]{android.R.id.text1, android.R.id.text2});
        ListView view = (ListView) findViewById(R.id.list_item);
        view.setAdapter(adapter);
        return true;
    }

    private ArrayList<HashMap<String, String>> toItemMap(ArrayList<ItemChan> items) {
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (ItemChan item : items) {
            HashMap<String, String> datum = new HashMap<String, String>(2);
            datum.put("title", item.getTitle());
            datum.put("duration", item.getDurationString());
            data.add(datum);
        }
        return data;
    }

    public void setMainButtonText(int id) {
        Button button = (Button) findViewById(R.id.button_start);
        button.setText(id);
    }

    public void onClickMainButton(View view) {
        coach.onClickTimerButton();
    }

    public void setClockText(String str) {
        TextView textClock = (TextView) findViewById(R.id.text_clock);
        textClock.setText(str);
    }

    @Override
    public boolean onLongClick(View v) {
        coach.resetTimer();
        return true;
    }

    public void playSoundMenu() {
        generator.startTone(ToneGenerator.TONE_SUP_ERROR, 3000);
    }

    public void playSoundItem() {
        generator.startTone(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE);
    }
}
