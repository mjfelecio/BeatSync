package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.core.Playfield;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;
    private final int PLAYFIELD_WIDTH = 400;
    private final int PLAYFIELD_HEIGHT = 700;
    private Playfield playfield;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        playfield = new Playfield(PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT);
        playfield.render(gc);

        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.show();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case D -> playfield.press(0);
                case F -> playfield.press(1);
                case J -> playfield.press(2);
                case K -> playfield.press(3);
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case D -> playfield.release(0);
                case F -> playfield.release(1);
                case J -> playfield.release(2);
                case K -> playfield.release(3);
            }
        });


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
