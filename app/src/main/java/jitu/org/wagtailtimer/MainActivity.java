package jitu.org.wagtailtimer;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends Activity implements ValueAnimator.AnimatorUpdateListener {
    private ValueAnimator animator;
    private int timerState;
    private long startTime;
    private long prevTime;
    private long duration = 100 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadMenuItems();
        animator = createAnimator();
    }

    private ValueAnimator createAnimator() {
        ValueAnimator anim = ValueAnimator.ofInt(0);
        anim.addUpdateListener(this);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.RESTART);
        return anim;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickMainButton(View view) {
        if (timerState == 0) {
            startTimer();
        } else if (timerState == 1) {
            pauseTimer();
        } else if (timerState == 2) {
            restartTimer();
        }
    }

    private void startTimer() {
        startTime = prevTime = System.currentTimeMillis();
        timerState = 1;
        animator.start();
    }

    private void pauseTimer() {
        animator.cancel();
        timerState = 2;
    }

    private void restartTimer() {
        animator.start();
        timerState = 1;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator anim) {
        updateClock();
    }

    private void updateClock() {
        long now = System.currentTimeMillis();
        if (now - prevTime < 1000) {
            return;
        }
        prevTime += 1000;
        TextView textClock = (TextView) findViewById(R.id.text_clock);
        long elapsed = duration - (prevTime - startTime);
        String str = formatTime(elapsed);
        textClock.setText(str);
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

    private String formatTime(long sec) {
        long s = (sec / 1000) % 60;
        long m = (sec / 1000 / 60) % 60;
        long h = sec / 1000 / 60 / 60;
        return String.format("%02d:%02d:%02d", h, m, s);
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
}
