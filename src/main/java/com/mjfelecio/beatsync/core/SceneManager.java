package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.rendering.SceneChangeListener;
import com.mjfelecio.beatsync.state.GameState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager implements SceneChangeListener {
    private static SceneManager instance;
    private final int width;
    private final int height;
    private final GameState gameState;
    private final Stage primaryStage; // This contains the original stage (window) for the game
    private Scene currentScene;

    private SceneManager(int width, int height, Stage primaryStage) {
        this.width = width;
        this.height = height;
        this.primaryStage = primaryStage;
        this.gameState = GameState.getInstance();

        // Register this SceneManager as a listener to GameState changes
        this.gameState.addSceneChangeListener(this);
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

        try {
            switch (gameScene) {
                case TITLE_SCREEN -> {
                    loader = new FXMLLoader(getClass().getResource("/com/mjfelecio/beatsync/views/title_screen.fxml"));
                    return new Scene(loader.load(), width, height);
                }
                case SONG_SELECT -> {
                    loader = new FXMLLoader(getClass().getResource("/com/mjfelecio/beatsync/views/song_select.fxml"));
                    return new Scene(loader.load(), width, height);
                }
                case SETTINGS -> {
                    loader = new FXMLLoader(getClass().getResource("/com/mjfelecio/beatsync/views/settings.fxml"));
                    return new Scene(loader.load(), width, height);
                }
                case RESULT_SCREEN -> {
                    loader = new FXMLLoader(getClass().getResource("/com/mjfelecio/beatsync/views/result_screen.fxml"));
                    return new Scene(loader.load(), width, height);
                }
                default -> {
                    System.out.println("Unknown scene: " + gameScene + ". Returning current scene.");
                    return getCurrentScene();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load " + gameScene + " due to:" + e.getMessage());
            e.printStackTrace();
        }

        // Just return the current scene if the scene isn't found
        return getCurrentScene();
    }
}
