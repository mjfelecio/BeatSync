package com.mjfelecio.beatsync.core;

import javafx.scene.media.MediaPlayer;

public class GameClock {
    private MediaPlayer player;
    private double startTime;

    public void start(MediaPlayer player) {
        this.player = player;
        this.startTime = this.player.getCurrentTime().toMillis();
    }

    public long getElapsedTime() {
        return (long) (player.getCurrentTime().toMillis() - startTime);
    }
}
