package jitu.org.wagtailtimer;

import android.animation.ValueAnimator;

public class TimerChan implements ValueAnimator.AnimatorUpdateListener {
    private Coach coach;
    private ValueAnimator animator;

    public TimerChan(Coach coach) {
        this.coach = coach;
        createAnimator();
    }

    private void createAnimator() {
        animator = ValueAnimator.ofInt(0);
        animator.addUpdateListener(this);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator anim) {
        coach.onTimer();
    }

    public void cancel() {
        animator.cancel();
    }
}
