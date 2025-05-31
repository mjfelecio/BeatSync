package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.rendering.GameScene;

public class GameState {
    private static final GameState instance = new GameState();
    private GameScene currentScene;

    private GameState() {}

    public static GameState getInstance() {
        return instance;
    }

    public GameScene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(GameScene currentScene) {
        this.currentScene = currentScene;

    }

    private int combo = 0;
    private long score = 0;
    private String lastJudgement = "";
    private boolean isPlaying = false;

    public void incrementCombo() { combo++; }
    public void resetCombo() { combo = 0; }
    public void setJudgement(String judgement) { this.lastJudgement = judgement; }
    public void setPlaying(boolean b) { this.isPlaying = b; }

    public int getCombo() {
        return combo;
    }

    public String getLastJudgement() {
        return lastJudgement;
    }

    public boolean isPlaying() { return isPlaying; }
}
