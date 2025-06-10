package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.config.GameConfig;

public class GameClock {
    private long gameStartTime = -1;
    private long songStartTime = -1;
    private long currentTime = 0;
    private boolean songStarted = false;
    private boolean isRunning = false;

    public void startGame() {
        gameStartTime = System.currentTimeMillis();
        songStartTime = gameStartTime + GameConfig.AUDIO_LEAD_IN;
        isRunning = true;
        songStarted = false;
    }

    public void update() {
        if (!isRunning) return;

        long systemTime = System.currentTimeMillis();
        currentTime = systemTime - songStartTime;
    }

    public long getCurrentSongTime() {
        return currentTime;
    }

    public boolean shouldStartAudio() {
        return !songStarted && currentTime >= 0;
    }

    public void markAudioStarted() {
        songStarted = true;
    }

    public boolean isInLeadIn() {
        return currentTime < 0;
    }

    public double getLeadInTimeRemaining() {
        return isInLeadIn() ? Math.abs(currentTime) / 1000.0 : 0.0;
    }

    public void stop() {
        isRunning = false;
        currentTime = 0;
        songStarted = false;
    }

    public void pause() {
        isRunning = false;
    }

    public void resume() {
        // Recalculate start times to account for pause duration
        long pauseDuration = System.currentTimeMillis() - (songStartTime + currentTime);
        gameStartTime += pauseDuration;
        songStartTime += pauseDuration;
        isRunning = true;
    }
}