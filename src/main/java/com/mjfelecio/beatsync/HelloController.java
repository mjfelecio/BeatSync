package com.mjfelecio.beatsync;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.hydev.obp.Beatmap;
import org.hydev.obp.BeatmapReader;

import java.io.File;

public class HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}