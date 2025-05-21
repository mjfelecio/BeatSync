package com.mjfelecio.beatsync;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class BeatSyncController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}