package jitu.org.wagtailtimer;

import android.animation.ValueAnimator;

public class TimerChan implements ValueAnimator.AnimatorUpdateListener {
    private MainActivity activity;
    private ValueAnimator animator;
    private int state;
    private long startTime;
    private long prevTime;
    private long duration = 100 * 1000;

    public TimerChan(MainActivity activity) {
        this.activity = activity;
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
    public void onAnimationUpdate(ValueAnimator anim) {
        checkUpdate();
    }

    public void onClickButton() {
        if (isIdle()) {
            start();
        } else if (isStarted()) {
            pause();
        } else if (isPaused()) {
            restart();
        }
    }
    public void start() {
        startTime = prevTime = System.currentTimeMillis();
        beStarted();
        animator.start();
    }

    public void pause() {
        animator.cancel();
        bePaused();
    }

    public void restart() {
        animator.start();
        beStarted();
    }

    public void reset() {
        if (isStarted()) {
            pause();
        }
        startTime = 0;
        state = 0;
        prevTime = 0;
        beIdle();
        notifyTimer();
    }

    public void checkUpdate() {
        if (isStarted()) {
            long now = System.currentTimeMillis();
            if (now - prevTime < 1000) {
                return;
            }
            prevTime += 1000;
        }
        notifyTimer();
    }

    private void notifyTimer() {
        activity.onUpdateTimer(this);
    }

    public long getElapsed() {
        return  duration - (prevTime - startTime);
    }

    public boolean isStarted() {
        return state == 1;
    }

    private void beStarted() {
        state = 1;
    }

    public boolean isPaused() {
        return state == 2;
    }

    private void bePaused() {
        state = 2;
    }

    public boolean isIdle() {
        return state == 0;
    }

    private void beIdle() {
        state = 0;
    }
}
