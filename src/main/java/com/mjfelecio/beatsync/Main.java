package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.core.Playfield;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    private Playfield playfield;

    @Override
    public void start(Stage stage) {


        Canvas playField = generatePlayField();

        Scene scene = new Scene(new StackPane(playField), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setTitle("Beat Sync: VSRG made with Java");
        stage.show();

//        new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                gc.clearRect(0, 0, WIDTH, HEIGHT);
//                playField.render(gc);
//                // In future: noteManager.renderNotes(gc, audioTime);
//            }
//        }.start();
    }

    private Canvas generatePlayField() {
        final int PLAY_FIELD_HEIGHT = 700;
        final int PLAY_FIELD_WIDTH = 400;
        final int NUM_OF_LANES = 4;
        final int LANE_WIDTH = PLAY_FIELD_WIDTH / NUM_OF_LANES;
        final int NOTE_SIZE = 80;
        final int HIT_ZONE_HEIGHT = PLAY_FIELD_HEIGHT - 150;

        Canvas playField = new Canvas(PLAY_FIELD_WIDTH, PLAY_FIELD_HEIGHT);
        GraphicsContext gc = playField.getGraphicsContext2D();

        // Create border
        gc.setFill(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeRect(0, 0 , PLAY_FIELD_WIDTH, PLAY_FIELD_HEIGHT);

        for (int i = 0; i < NUM_OF_LANES; i++) {
            gc.setLineWidth(2);

            // Add vertical lines as lane separators
            gc.strokeLine(LANE_WIDTH * i, 0, LANE_WIDTH * i, PLAY_FIELD_HEIGHT);

            int circleCenteredWidthPos = (LANE_WIDTH * i) + (LANE_WIDTH - NOTE_SIZE) / 2;

            // Add circles as indications for the hit zones in each lane
            gc.strokeOval(circleCenteredWidthPos, HIT_ZONE_HEIGHT, NOTE_SIZE, NOTE_SIZE);
        }

        return playField;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
