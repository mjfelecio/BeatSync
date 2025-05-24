package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.core.AudioEngine;
import com.mjfelecio.beatsync.core.GameClock;
import com.mjfelecio.beatsync.core.Playfield;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    private Playfield playfield;
    private GameClock gameClock;
    private AudioEngine musicPlayer;

    @Override
    public void start(Stage stage) {
        // Initialize the canvas that serves as our playfield
        int PLAYFIELD_WIDTH = 400;
        int PLAYFIELD_HEIGHT = 700;
        Canvas canvas = new Canvas(PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Initialize the scene and window
        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.show();

        gameClock = new GameClock();
        playfield = new Playfield(PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT, gameClock);
        musicPlayer = new AudioEngine();

        playfield.render(gc);
        // I'm using File.toURI() so that it parses files with spaces properly
        // TODO: Have a class dedicated to loading all of the beatmaps files, including audio
        String filePath = new File("src/main/resources/com/mjfelecio/beatsync/1301440 TrySail - Utsuroi (Short Ver.) (another copy).osz_FILES/audio.mp3").toURI().toString();
        musicPlayer.setMusic(new Media(filePath));
        // TODO: Have a dedicated class that handles starting the playing
        musicPlayer.getPlayer().play();
        gameClock.start();

        // TODO: Create a InputHandler class for this
        // Get inputs from the user
        scene.setOnKeyPressed(event -> playfield.pressKey(event.getCode()));
        scene.setOnKeyReleased(event -> playfield.releaseKey(event.getCode()));

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                playfield.render(gc);
                playfield.update(gc);
            }
        }.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
