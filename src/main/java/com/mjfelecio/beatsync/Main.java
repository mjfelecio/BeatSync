package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.core.AudioManager;
import com.mjfelecio.beatsync.core.GameClock;
import com.mjfelecio.beatsync.core.GameEngine;
import com.mjfelecio.beatsync.core.Playfield;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    private Playfield playfield;
    private GameEngine gameEngine;

    @Override
    public void start(Stage stage) {
        // UI setup
        Canvas canvas = new Canvas(GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new StackPane(canvas), GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.show();

        // Game setup
        gameEngine = new GameEngine();
        gameEngine = new GameEngine();
        try {
            gameEngine.initialize(GameConfig.TEST_BEATMAP_PATH);
            gameEngine.start();
        } catch (RuntimeException e) {
            System.err.println("Failed to start game: " + e.getMessage());
            return;
        }

        // Load the playfield
        playfield = new Playfield(GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT, gameClock);

        // TODO: Create a InputHandler class for this
        // Get inputs from the user
        scene.setOnKeyPressed(event -> playfield.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> playfield.releaseKey(event.getCode()));

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);

                playfield.render(gc);
                playfield.update(gameClock.getCurrentTime());
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
