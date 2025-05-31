package com.mjfelecio.beatsync.state;

import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.rendering.SceneChangeListener;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static final GameState instance = new GameState();
    private GameScene currentScene;
    private List<SceneChangeListener> listeners = new ArrayList<>();

    private GameState() {}

    public static GameState getInstance() {
        return instance;
    }

    public GameScene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(GameScene currentScene) {
        GameScene oldScene = this.currentScene;
        this.currentScene = currentScene;

        // Notify all listeners about the scene change
        notifySceneChange(oldScene, currentScene);
    }

    // Method to register listeners
    public void addSceneChangeListener(SceneChangeListener listener) {
        listeners.add(listener);
    }

    public void removeSceneChangeListener(SceneChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifySceneChange(GameScene oldScene, GameScene newScene) {
        for (SceneChangeListener listener : listeners) {
            listener.onSceneChange(oldScene, newScene);
        }
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
