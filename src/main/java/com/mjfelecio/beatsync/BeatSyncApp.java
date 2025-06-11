package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.core.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class BeatSyncApp extends Application {
    @Override
    public void start(Stage stage) {
        // Add -1 as a hacky way to not shift the scenes to the top right of the screen when switching scenes
        SceneManager.initialize(stage);

        try {
            ScoreDatabase.connect(); // Initialize the database on game startup
            ScoreDatabase.createScoresTable(); // Creates the scores table if it isn't already created
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SceneManager.getInstance().loadTitleScreen();

        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.setMaximized(true);
        stage.show();
    }
}
