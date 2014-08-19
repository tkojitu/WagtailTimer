package jitu.org.wagtailtimer;

import android.animation.ValueAnimator;

public class TimerChan implements ValueAnimator.AnimatorUpdateListener {
    private Coach coach;
    private ValueAnimator animator;
    private int state;
    private long startTime;
    private long prevTime;
    private long initialDuration;
    private long duration;
    private long innerRest;

    public TimerChan(Coach coach, long duration) {
        this.coach = coach;
        this.initialDuration = this.duration = duration;
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
        duration = initialDuration;
        beStarted();
        animator.start();
    }

    public void pause() {
        animator.cancel();
        innerRest = getRest();
        bePaused();
    }

    public void restart() {
        startTime = prevTime = System.currentTimeMillis();
        duration = innerRest;
        beStarted();
        animator.start();
    }

    public void reset() {
        if (isStarted()) {
            pause();
        }
        startTime = 0;
        state = 0;
        prevTime = 0;
        duration = initialDuration;
        beIdle();
        notifyTimer();
    }

    public void stop() {
        animator.cancel();
        innerRest = 0;
        beStopped();
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
        if (getRest() <= 0 && !isStopped()) {
            stop();
        }
    }

    private void notifyTimer() {
        coach.onUpdateTimer(this);
    }

    public long getRest() {
        long result = duration - (prevTime - startTime);
        return  (result < 0) ? 0 : result;
    }

    public String getRestString() {
        return TimerItem.formatTime(getRest());
    }

    public boolean isIdle() {
        return state == 0;
    }

    private void beIdle() {
        setState(0);
    }

    public boolean isStarted() {
        return state == 1;
    }

    private void beStarted() {
        setState(1);
    }

    public boolean isPaused() {
        return state == 2;
    }

    private void bePaused() {
        setState(2);
    }

    public boolean isStopped() {
        return state == 3;
    }

    private void beStopped() {
        setState(3);
    }

    private void setState(int value) {
        state = value;
        notifyTimerStateChanged();
    }

    public void notifyTimerStateChanged() {
        coach.onTimerStateChanged(this);
    }
}
