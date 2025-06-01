package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.core.GameEngine;
import com.mjfelecio.beatsync.core.GameplayManager;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.core.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SceneManager.initialize(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, stage);

        GameState.getInstance().setCurrentScene(GameScene.TITLE_SCREEN);

        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
