package com.mjfelecio.beatsync.core;

public class GameClock {
    private long startTime;
    private long currentTime;

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void syncToAudioTime(long currentAudioTime) {
        this.currentTime = currentAudioTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }
}
