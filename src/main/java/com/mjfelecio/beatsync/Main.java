package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.core.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // Add -1 as a hacky way to not shift the scenes to the top right of the screen when switching scenes
        SceneManager.initialize(GameConfig.SCREEN_WIDTH - 1 , GameConfig.SCREEN_HEIGHT - 1, stage);

        SceneManager.getInstance().setCurrentScene(GameScene.TITLE_SCREEN);

        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
