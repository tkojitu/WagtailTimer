package jitu.org.wagtailtimer;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

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
        int ret = tts.setLanguage(Locale.getDefault());
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
        generator.startTone(ToneGenerator.TONE_SUP_ERROR, 3000);
    }

    public void shoutStart(MenuItem item) {
        if (activity.usesSpeech()) {
            shoutText(item.getTitle());
        } else {
            generator.startTone(ToneGenerator.TONE_CDMA_ALERT_AUTOREDIAL_LITE);
        }
    }
}
