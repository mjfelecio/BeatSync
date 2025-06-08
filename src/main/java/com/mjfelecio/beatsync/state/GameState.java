package com.mjfelecio.beatsync.state;

import com.mjfelecio.beatsync.object.Beatmap;

public class GameState {
    private static final GameState instance = new GameState();
    private boolean isPlaying = false;
    private Beatmap currentBeatmap = null;

    private GameState() {}

    public static GameState getInstance() {
        return instance;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Beatmap getCurrentBeatmap() {
        return currentBeatmap;
    }

    public void setCurrentBeatmap(Beatmap currentBeatmap) {
        this.currentBeatmap = currentBeatmap;
    }
}
