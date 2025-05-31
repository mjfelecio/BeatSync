package com.mjfelecio.beatsync.rendering;

import com.mjfelecio.beatsync.core.GameState;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance;
    private final int width;
    private final int height;
    private final GameState gameState;
    private Scene currentScene;

    private SceneManager(int width, int height, Stage primaryStage) {
        this.width = width;
        this.height = height;
        this.gameState = GameState.getInstance();
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

    public void setCurrentScene(GameScene gameScene) {
        this.currentScene = getSceneFromGameScene(gameScene);
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
            }
        } catch (Exception e) {
            System.err.println("Failed to load " + gameScene + " due to:" + e.getMessage());
            e.printStackTrace();
        }

        // Just return the current scene if the scene isn't found
        return getCurrentScene();
    }
}
