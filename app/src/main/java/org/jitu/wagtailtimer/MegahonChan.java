package org.jitu.wagtailtimer;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class MegahonChan implements TextToSpeech.OnInitListener {
    private MainActivity activity;
    private TextToSpeech tts;
    private ToneGenerator generator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    MegahonChan(MainActivity activity) {
        this.activity = activity;
        tts = new TextToSpeech(activity, this);
    }

    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            Toast.makeText(activity, "onInit failed", Toast.LENGTH_LONG).show();
            return;
        }
        int ret = tts.setLanguage(activity.getLanguageLocale());
        if (ret == TextToSpeech.LANG_MISSING_DATA || ret == TextToSpeech.LANG_NOT_SUPPORTED) {
            Toast.makeText(activity, "setLanguage failed", Toast.LENGTH_LONG).show();
        }
    }

    public void shutdown() {
        tts.stop();
        tts.shutdown();
    }

    public void shoutText(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void shoutFinish() {
        String type = activity.getSoundType();
        if (type.equals(activity.getString(R.string.none))) {
            return;
        }
        generator.startTone(ToneGenerator.TONE_SUP_ERROR, 3000);
    }

    public void shoutStart(MenuItem item) {
        String type = activity.getSoundType();
        String title = item.getTitle();
        if (!title.isEmpty() && type.equals(activity.getString(R.string.voice))) {
            shoutText(item.getTitle());
        } else if (type.equals(activity.getString(R.string.tone))) {
            generator.startTone(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE);
        }
    }
}
