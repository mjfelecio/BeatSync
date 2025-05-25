package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Playfield {
    private final int width;
    private final int height;
    private final int NUM_LANES = 4;
    private final int NOTE_DIAMETER = 80;

    private GameClock gameClock;
    private NoteManager noteManager;
    public final int NOTE_APPROACH_TIME = 1000;

    private final boolean[] isLanePressed = new boolean[NUM_LANES]; // Keep track of presses

    int combo = 0;
    String judgementResult = "";

    public Playfield(int width, int height, GameClock gameClock) {
        this.width = width;
        this.height = height;
        this.gameClock = gameClock;

        try {
            // Initializing a map here temporarily
            File beatmapFile = new File("src/main/resources/com/mjfelecio/beatsync/beatmaps/test.osu");
            List<Note> notes = ManiaBeatmapParser.parse(beatmapFile).getNotes();
            this.noteManager = new NoteManager(notes);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void update(long timeElapsed) {
        noteManager.update(timeElapsed, isLanePressed);
    }

    public void render(GraphicsContext gc) {
        int laneWidth = getLaneWidth();

        // Create border
        gc.setFill(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeRect(0, 0, width, height);

        for (int i = 0; i < NUM_LANES; i++) {
            gc.setLineWidth(2);

            // Add vertical lines as lane separators
            gc.strokeLine(laneWidth * i, 0, laneWidth * i, height);

            int circleCenteredWidthPos = getCircleCenteredWidthPos(i);

            // Indicator of the key press
            if (isLanePressed[i]) {
                gc.setFill(Color.RED);
                gc.fillOval(circleCenteredWidthPos, getHitLineY(), NOTE_DIAMETER, NOTE_DIAMETER);
                gc.setFill(Color.BLACK);
            }

            // Add circles as indications for the hit zones in each lane
            gc.strokeOval(circleCenteredWidthPos, getHitLineY(), NOTE_DIAMETER, NOTE_DIAMETER);
        }

        gc.setFont(new Font(20));
        gc.fillText("Combo: " + this.combo, 20, 50);
        gc.setFont(new Font(30));
        gc.fillText(judgementResult, (width / 2.0) - 50, height - 250);

        // Draw notes
        gc.setFill(Color.BLUE);
        noteManager.getActiveNotes().forEach(n -> {
            if (n.isHit()) return; // Do not draw the note once it has been hit already
            double y = n.calculateY(gameClock.getElapsedTime(), NOTE_APPROACH_TIME, getHitLineY());
            gc.fillOval(getCircleCenteredWidthPos(n.getLaneNumber()), y, NOTE_DIAMETER, NOTE_DIAMETER);
        });
    }

    public void pressKey(KeyCode code) {
        switch (code) {
            case D -> isLanePressed[0] = true;
            case F -> isLanePressed[1] = true;
            case J -> isLanePressed[2] = true;
            case K -> isLanePressed[3] = true;
        }
    }

    public void releaseKey(KeyCode code) {
        switch (code) {
            case D -> isLanePressed[0] = false;
            case F -> isLanePressed[1] = false;
            case J -> isLanePressed[2] = false;
            case K -> isLanePressed[3] = false;
        }
    }

    private int getLaneWidth() {
        return width / NUM_LANES;
    }

    private int getHitLineY() {
        return height - 150;
    }

    private int getCircleCenteredWidthPos(int laneNum) {
        int laneWidth = getLaneWidth();
        return (laneWidth * laneNum) + (laneWidth - NOTE_DIAMETER) / 2;
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
