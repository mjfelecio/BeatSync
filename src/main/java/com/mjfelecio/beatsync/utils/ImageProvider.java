package com.mjfelecio.beatsync.utils;

import com.mjfelecio.beatsync.config.GameConfig;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public enum ImageProvider {
    TITLE_SCREEN_BG("/com/mjfelecio/beatsync/assets/images/title_screen_bg.jpeg"),
    SONG_SELECT_BG("/com/mjfelecio/beatsync/assets/images/song_select_bg.png"),
    SETTINGS_BG("/com/mjfelecio/beatsync/assets/images/settings_bg.png"),
    GAMEPLAY_BG("/com/mjfelecio/beatsync/assets/images/gameplay_bg.jpg"),
    PAUSE_SCREEN_BG("/com/mjfelecio/beatsync/assets/images/pause_screen_bg.png"),
    PLAY_RESULT_BG("/com/mjfelecio/beatsync/assets/images/play_result_bg.png"),

    // Rank Images
    SS_RANK("/com/mjfelecio/beatsync/assets/images/rank/ss-rank.png"),
    S_RANK("/com/mjfelecio/beatsync/assets/images/rank/s-rank.png"),
    A_RANK("/com/mjfelecio/beatsync/assets/images/rank/a-rank.png"),
    B_RANK("/com/mjfelecio/beatsync/assets/images/rank/b-rank.png"),
    C_RANK("/com/mjfelecio/beatsync/assets/images/rank/c-rank.png"),
    D_RANK("/com/mjfelecio/beatsync/assets/images/rank/d-rank.png");

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
