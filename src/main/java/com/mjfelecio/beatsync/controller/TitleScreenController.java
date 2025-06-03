package com.mjfelecio.beatsync.controller;

import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.state.GameState;
import javafx.fxml.FXML;

public class TitleScreenController {

    @FXML
    protected void onStartButtonClick() {
        SceneManager.getInstance().setCurrentScene(GameScene.SONG_SELECT);
    }

    @FXML
    protected void onSettingButtonClick() {
        SceneManager.getInstance().setCurrentScene(GameScene.SETTINGS);
    }
}
