package com.mjfelecio.beatsync.state;

import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.rendering.SceneChangeListener;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private static final GameState instance = new GameState();
    private GameScene currentScene;
    private boolean isPlaying = false;
    private List<SceneChangeListener> listeners = new ArrayList<>();
    private Beatmap currentBeatmap;

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
