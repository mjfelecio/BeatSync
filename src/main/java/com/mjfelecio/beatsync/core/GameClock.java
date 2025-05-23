package com.mjfelecio.beatsync.core;

public class GameClock {
    private long startTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public long getElapsedTime(long timeNow) {
        return timeNow - startTime;
    }
}
