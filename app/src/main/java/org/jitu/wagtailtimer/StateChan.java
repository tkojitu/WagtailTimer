package org.jitu.wagtailtimer;

public class StateChan {
    private State idling = new StateIdling(this);
    private State ongoing = new StateOngoing(this);
    private State pausing = new StatePausing(this);
    private State state = idling;
    private Coach coach;

    public StateChan(Coach coach) {this.coach = coach;}

    public State getIdling() {return idling;}
    public State getOngoing() {return ongoing;}
    public State getPausing() {return pausing;}

    public void onStart() {state = state.onStart();}
    public void onPause() {state = state.onPause();}
    public void onRestart() {state = state.onRestart();}
    public void onReset(String path) {state = state.onReset(path);}
    public void onTimer() {state = state.onTimer();}

    public void start() {coach.start();}
    public void pause() {coach.pause();}
    public void restart() {coach.restart();}
    public void reset(String path) {coach.loadMenu(path);}
    public void update() {coach.update();}

    public boolean hasItems() {return coach.hasItems();}
}

abstract class State {
    protected StateChan chan;
    public State(StateChan chan) {this.chan = chan;}
    public State onStart() {return this;}
    public State onPause() {return this;}
    public State onRestart() {return this;}
    public State onReset(String path) {
        chan.reset(path);
        return chan.getIdling();
    }
    public State onTimer() {return this;}
}

class StateIdling extends State {
    public StateIdling(StateChan chan) {super(chan);}
    public State onStart() {
        if (!chan.hasItems()) {
            return this;
        }
        chan.start();
        return chan.getOngoing();
    }
}

class StateOngoing extends State {
    public StateOngoing(StateChan chan) {super(chan);}
    public State onPause() {
        chan.pause();
        return chan.getPausing();
    }
    public State onTimer() {
        chan.update();
        if (!chan.hasItems()) {
            return chan.getIdling();
        }
        return this;
    }
}

class StatePausing extends State {
    public StatePausing(StateChan chan) {super(chan);}
    public State onRestart() {
        chan.restart();
        return chan.getOngoing();
    }
}
