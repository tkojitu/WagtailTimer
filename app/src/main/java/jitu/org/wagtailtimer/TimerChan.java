package jitu.org.wagtailtimer;

import android.animation.ValueAnimator;

public class TimerChan implements ValueAnimator.AnimatorUpdateListener {
    private Coach coach;

    public TimerChan(Coach coach) {
        this.coach = coach;
        createAnimator();
    }

    private void createAnimator() {
        ValueAnimator anim = ValueAnimator.ofInt(0);
        anim.addUpdateListener(this);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator anim) {
        coach.onTimer();
    }
}
