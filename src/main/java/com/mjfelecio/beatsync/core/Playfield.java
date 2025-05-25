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

public class Playfield {
    private final int width;
    private final int height;
    private final int NUM_LANES = 4;
    private final int NOTE_DIAMETER = 80;

    private GameClock gameClock;
    public ArrayList<Note> notes;
    public ArrayList<Note> activeNotes;
    public final int NOTE_APPROACH_TIME = 1000;

    // Hit window thresholds in ms
    private final int PERFECT_HIT_WINDOW = 30;
    private final int GOOD_HIT_WINDOW = 80;
    private final int MISS_HIT_WINDOW = 150;

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
            notes = new BeatmapParser(ManiaBeatmapParser.parse(beatmapFile)).parseNotes();
            activeNotes = new ArrayList<>();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void update(long timeElapsed) {
        // Check for notes to activate
        notes.forEach(n -> {
            if ((timeElapsed >= n.getStartTime() - NOTE_APPROACH_TIME) && !n.missed && !n.isHit()) {
                activeNotes.add(n);
            }
        });

        // Remove misses automatically
        Iterator<Note> missIter = activeNotes.iterator();
        while (missIter.hasNext()) {
            Note n = missIter.next();
            // If the notes wasn't hit already AND has passed the miss window, mark it as a miss
            if (!n.isHit() && (timeElapsed - n.getStartTime()) > MISS_HIT_WINDOW) {
                registerScore("Miss");
                n.missed = true;
                missIter.remove();
            }
        }

        // Handle presses
        for (int lane = 0; lane < NUM_LANES; lane++) {
            if (isLanePressed[lane]) {
                Note closestNote = null;

                // timeDeltaToClosestNote represents the closest note from the elapsed time in music
                // This should result in only the closest note being removed and not overlapping notes
                long timeDeltaToClosestNote = Long.MAX_VALUE;
                for (Note n : activeNotes) {
                    if (n.getLaneNumber() != lane || n.isHit()) continue;
                    long delta = Math.abs(timeElapsed - n.getStartTime());
                    if (delta < timeDeltaToClosestNote && delta <= MISS_HIT_WINDOW) {
                        timeDeltaToClosestNote = delta;
                        closestNote = n;
                    }
                }

                if (closestNote != null) {
                    closestNote.setHit(true);
                    if (timeDeltaToClosestNote <= PERFECT_HIT_WINDOW) registerScore("Perfect");
                    else if (timeDeltaToClosestNote <= GOOD_HIT_WINDOW) registerScore("Good");
                    else {
                        closestNote.missed = true;
                        registerScore("Miss");
                    }
                }
            }
        }

        // Remove notes that have passed by the playfield
        activeNotes.removeIf(n -> {
            boolean shouldRemove = n.calculateY(timeElapsed, NOTE_APPROACH_TIME, getHitLineY()) > height;
            if (shouldRemove) n.missed = true;
            return shouldRemove;
        });
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
        gc.fillText(judgementResult, (width / 2.0) - 50, 200);

        // Draw notes
        gc.setFill(Color.BLUE);
        activeNotes.forEach(n -> {
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
                judgementResult = "Perfect hit";
                combo++;
            }
            case "Good" -> {
                judgementResult = "Good hit!";
                combo++;
            }
            case "Miss" -> {
                judgementResult = "Missed!";
                combo = 0;
            }
            default -> System.out.println(rating);
        }
    }
}
