package edu.eci.arsw.primefinder;

public class PauseControl {
    private boolean paused = false;

    public synchronized void setPaused(boolean paused) {
        this.paused = paused;
    }

    public synchronized boolean isPaused() {
        return paused;
    }
}
