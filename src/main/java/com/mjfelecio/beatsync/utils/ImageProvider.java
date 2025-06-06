package com.mjfelecio.beatsync.utils;

import com.mjfelecio.beatsync.config.GameConfig;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public enum ImageProvider {
    TITLE_SCREEN_BG("/com/mjfelecio/beatsync/assets/images/title_screen_bg.jpeg"),
    GAMEPLAY_BG("/com/mjfelecio/beatsync/assets/images/gameplay_bg.jpg");

    private final String imagePath;

    ImageProvider(String imagePath) {
        this.imagePath = imagePath;
    }

    public Image getImage() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(this.imagePath)));
    }

    public Image getImage(double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream(this.imagePath)), requestedWidth, requestedHeight, preserveRatio, smooth);
    }

    public Background getImageAsBackground() {
        BackgroundImage bgImage = new BackgroundImage(
                getImage(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                new BackgroundSize(
                        BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true
                )
        );
        return new Background(bgImage);
    }
}
