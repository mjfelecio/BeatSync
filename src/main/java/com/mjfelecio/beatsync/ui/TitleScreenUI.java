package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class TitleScreenUI {
    private final Scene scene;

    public TitleScreenUI() {
        VBox root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(24);
        root.setBackground(ImageProvider.TITLE_SCREEN_BG.getImageAsBackground());

        // Title Text
        Text titleText = new Text("Beat Sync");
        titleText.setFont(FontProvider.ARCADE_R.getFont(80));
        titleText.setStyle("-fx-fill: #0FF;");
        VBox.setMargin(titleText, new Insets(150, 0, 0, 0));

        // Start Button
        Button startButton = new Button("Start");
        startButton.setFont(FontProvider.ARCADE_R.getFont(48));
        startButton.setStyle("""
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 4px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 10 30 10 30;
            -fx-cursor: hand;
        """);
        startButton.setOnAction(e -> onStartButtonClick());
        VBox.setMargin(startButton, new Insets(100, 0, 0, 0));

        // Settings Button
        Button settingsButton = new Button("Settings");
        settingsButton.setFont(FontProvider.ARCADE_R.getFont(20));
        settingsButton.setStyle("""
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """);
        settingsButton.setOnAction(e -> onSettingButtonClick());

        root.getChildren().addAll(titleText, startButton, settingsButton);

        scene = new Scene(root);
    }

    public Scene getScene() {
        return scene;
    }

    private void onStartButtonClick() {
        SFXPlayer.getInstance().play(SoundEffect.SELECT);
        SceneManager.getInstance().loadSongSelect();
    }

    private void onSettingButtonClick() {
        SFXPlayer.getInstance().play(SoundEffect.SELECT);
        SceneManager.getInstance().loadSettings();
    }
}
