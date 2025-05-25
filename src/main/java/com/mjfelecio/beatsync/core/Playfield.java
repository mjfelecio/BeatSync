package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.InputState;
import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Playfield {
    private final int width;
    private final int height;

    private GameClock gameClock;
    private InputState inputManager;
    private NoteManager noteManager;

    int combo = 0;
    String judgementResult = "";

    public Playfield(int width, int height, GameClock gameClock) {
        this.width = width;
        this.height = height;
        this.gameClock = gameClock;
        this.inputManager = new InputState();

        try {
            // Initializing a map here temporarily
            File beatmapFile = new File(GameConfig.TEST_BEATMAP_PATH);
            List<Note> notes = ManiaBeatmapParser.parse(beatmapFile).getNotes();
            this.noteManager = new NoteManager(notes);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void update(long timeElapsed) {
        noteManager.update(timeElapsed, inputManager.getLanePressed());
    }

    public void render(GraphicsContext gc) {
        int laneWidth = getLaneWidth();

        // Create border
        gc.setFill(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeRect(0, 0, width, height);

        for (int i = 0; i < GameConfig.NUM_LANES; i++) {
            gc.setLineWidth(2);

            // Add vertical lines as lane separators
            gc.strokeLine(laneWidth * i, 0, laneWidth * i, height);

            int circleCenteredWidthPos = getCircleCenteredWidthPos(i);

            // Indicator of the key press
            if (inputManager.isPressed(i)) {
                gc.setFill(Color.RED);
                gc.fillOval(circleCenteredWidthPos, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
                gc.setFill(Color.BLACK);
            }

            // Add circles as indications for the hit zones in each lane
            gc.strokeOval(circleCenteredWidthPos, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
        }

        gc.setFont(new Font(20));
        gc.fillText("Combo: " + this.combo, 20, 50);
        gc.setFont(new Font(30));
        gc.fillText(judgementResult, (width / 2.0) - 50, height - 250);

        // Draw notes
        gc.setFill(Color.BLUE);
        noteManager.getActiveNotes().forEach(n -> {
            if (n.isHit()) return; // Do not draw the note once it has been hit already
            double y = n.calculateY(gameClock.getElapsedTime(), GameConfig.NOTE_APPROACH_TIME, GameConfig.HIT_LINE_Y);
            gc.fillOval(getCircleCenteredWidthPos(n.getLaneNumber()), y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
        });
    }

    public void pressKey(KeyCode code) {
        inputManager.press(code);
    }

    public void releaseKey(KeyCode code) {
        inputManager.release(code);
    }



    private int getLaneWidth() {
        return width / GameConfig.NUM_LANES;
    }

    private int getCircleCenteredWidthPos(int laneNum) {
        int laneWidth = getLaneWidth();
        return (laneWidth * laneNum) + (laneWidth - GameConfig.NOTE_DIAMETER) / 2;
    }

    private void registerScore(String rating) {
        switch (rating) {
            case "Perfect" -> {
                judgementResult = JudgementWindow.PERFECT.getDescription();
                combo++;
            }
            case "Good" -> {
                judgementResult = JudgementWindow.GOOD.getDescription();
                combo++;
            }
            case "Miss" -> {
                judgementResult = JudgementWindow.MISS.getDescription();
                combo = 0;
            }
            default -> System.out.println(rating);
        }
    }
}
