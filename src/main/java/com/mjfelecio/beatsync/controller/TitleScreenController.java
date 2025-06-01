package com.mjfelecio.beatsync.controller;

import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.state.GameState;
import javafx.fxml.FXML;

public class TitleScreenController {

    @FXML
    protected void onStartButtonClick() {
        GameState.getInstance().setCurrentScene(GameScene.GAMEPLAY);
    }

    @FXML
    protected void onSettingButtonClick() {
        GameState.getInstance().setCurrentScene(GameScene.SETTINGS);
    }
}
