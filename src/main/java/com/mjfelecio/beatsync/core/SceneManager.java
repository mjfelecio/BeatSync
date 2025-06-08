package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.ui.SongSelectUI;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.rendering.SceneChangeListener;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.ui.PlayResultUI;
import com.mjfelecio.beatsync.ui.TitleScreenUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SceneManager implements SceneChangeListener {
    private static SceneManager instance;
    private final Stage primaryStage; // This contains the original stage (window) for the game

    private final int width;
    private final int height;

    private final List<SceneChangeListener> listeners = new ArrayList<>();
    private GameScene currentGameScene;
    private Scene currentScene;

    private SceneManager(int width, int height, Stage primaryStage) {
        this.width = width;
        this.height = height;
        this.primaryStage = primaryStage;

        // Register this SceneManager as a listener to Scene changes
        addSceneChangeListener(this);
    }

    public static void initialize(int width, int height, Stage primaryStage) {
        if (instance == null) {
            instance = new SceneManager(width, height, primaryStage);
        }
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SceneManager must be initialized first!");
        }
        return instance;
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(GameScene currentGameScene) {
        GameScene oldScene = this.currentGameScene;
        this.currentGameScene = currentGameScene;

        // Notify all listeners about the scene change
        notifySceneChange(oldScene, currentGameScene);
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

    @Override
    public void onSceneChange(GameScene oldScene, GameScene newScene) {
        if (newScene != null) {
            Scene scene = getSceneFromGameScene(newScene);
            this.currentScene = scene;

            // Automatically update the primary stage with the new scene
            if (primaryStage != null) {
                primaryStage.setScene(scene);
            }
        }
    }

    public Scene getSceneFromGameScene(GameScene gameScene) {
        FXMLLoader loader;
        Scene scene = null;

        try {
            switch (gameScene) {
                case TITLE_SCREEN -> {
                    scene = new TitleScreenUI().getScene();
                }
                case SONG_SELECT -> {
//                    loader = new FXMLLoader(getClass().getResource("/com/mjfelecio/beatsync/views/song_select.fxml"));
                    scene = new SongSelectUI().getScene();
                }
                case SETTINGS -> {
                    loader = new FXMLLoader(getClass().getResource("/com/mjfelecio/beatsync/views/settings.fxml"));
                    scene = new Scene(loader.load(), width, height);
                }
                case GAMEPLAY -> {
                    GameplayManager gameplayManager = GameplayManager.getInstance();

                    // Resets the notes state so that it can still get rendered on retry
                    // You have no idea how long I spent to fix this bug. 4 fking hours and it was just this simple thing.
                    // I almost rewrote my entire gameplay logic to find what was going on
                    // I hate my liiiiife
                    GameState.getInstance().getCurrentBeatmap().resetNotesState();

                    gameplayManager.loadBeatmap(GameState.getInstance().getCurrentBeatmap());
                    gameplayManager.initializeGameplay();
                    gameplayManager.startGameplay();

                    scene = gameplayManager.getGameplayScene();
                }
                case RESULT_SCREEN -> {
                    scene = new PlayResultUI().getScene();
                }
                default -> {
                    // Just return the current scene if the scene isn't found
                    System.out.println("Unknown scene: " + gameScene + ". Returning current scene.");
                    scene = getCurrentScene();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load " + gameScene + " due to:" + e.getMessage());
            e.printStackTrace();
        }

        return scene;
    }

    // This is more manual approach in case we want to show a different scene not defined in GameScene
    public void changeSceneTo(Scene newScene) {
        if (newScene != null) {
            this.currentScene = newScene;

            // Update the window with the new scene
            if (primaryStage != null) {
                primaryStage.setScene(newScene);
            }
        }
    }
}
