package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Playfield {
    private final int width;
    private final int height;
    private final int LANES = 4;
    private final int NOTE_SIZE = 80;

    private GameClock gameClock;
    public ArrayList<Note> notes;
    public ArrayList<Note> activeNotes;
    public final int APPROACH_TIME = 1000;

    // Hit window thresholds in ms
    private final int PERFECT_WINDOW = 30;
    private final int GOOD_WINDOW = 80;
    private final int MISS_WINDOW = 150;

    private final boolean[] pressedLanes = new boolean[LANES]; // Keep track of presses

    int clickedNotes = 0;

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
            if ((timeElapsed >= n.getStartTime()) && !activeNotes.contains(n) && !n.isHit()) {
                activeNotes.add(n);
            }
        });

        // Remove misses automatically
        Iterator<Note> missIter = activeNotes.iterator();
        while (missIter.hasNext()) {
            Note n = missIter.next();
            // If the notes wasn't hit already AND has passed the miss window, mark it as a miss
            // Explanation for future me:
            //  The notes are supposed to be hit at a specific time in the music
            //  If they haven't been hit yet at their specific time in the specified TIMING WINDOWS, they're a miss
            if (!n.isHit() && (timeElapsed - n.getStartTime()) > MISS_WINDOW) {
                registerScore("Miss");
                missIter.remove();
            }
        }

        // Handle presses
        for (int lane = 0; lane < LANES; lane++) {
            if (pressedLanes[lane]) {
                Note best = null;

                // bestDelta represents the closest note from the elapsed time in music
                // This should result in only one note being removed and not overlapping notes
                // However, I'm still running into bugs with it
                // TODO: Fix multiple notes being removed at the same time if they are too close together
                long bestDelta = Long.MAX_VALUE;
                for (Note n : activeNotes) {
                    if (n.getLaneNumber() != lane || n.isHit()) continue;
                    long delta = Math.abs(timeElapsed - n.getStartTime());
                    if (delta < bestDelta && delta <= MISS_WINDOW) {
                        bestDelta = delta;
                        best = n;
                    }
                }

                if (best != null) {
                    best.setHit(true);
                    if (bestDelta <= PERFECT_WINDOW) registerScore("Perfect");
                    else if (bestDelta <= GOOD_WINDOW) registerScore("Good");
                    else registerScore("Miss");
                }
            }
        }
        activeNotes.removeIf(n -> n.getY(timeElapsed, APPROACH_TIME, getHitZoneY()) > height); // Remove notes that have passed the playfield
    }

    public void render(GraphicsContext gc) {
        int laneWidth = getLaneWidth();

        // Add miss zones cut-off for testing
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.strokeLine(0, getHitZoneY(), width, getHitZoneY());
        gc.setStroke(Color.BLACK);

        // Create border
        gc.setFill(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeRect(0, 0, width, height);

        for (int i = 0; i < LANES; i++) {
            gc.setLineWidth(2);

            // Add vertical lines as lane separators
            gc.strokeLine(laneWidth * i, 0, laneWidth * i, height);

            int circleCenteredWidthPos = getCircleCenteredWidthPos(i);
            // This is the topLeft pos, so that the circle generated is in the middle of the hitZone
            int hitZoneY = getHitZoneTopLeftY();

            // Indicator of the key press
            if (pressedLanes[i]) {
                gc.setFill(Color.RED);
                gc.fillOval(circleCenteredWidthPos, hitZoneY, NOTE_SIZE, NOTE_SIZE);
            }

            // Add circles as indications for the hit zones in each lane
            gc.strokeOval(circleCenteredWidthPos, hitZoneY, NOTE_SIZE, NOTE_SIZE);
        }

        // Draw notes
        activeNotes.forEach(n -> {
            double y = n.getY(gameClock.getElapsedTime(), APPROACH_TIME, getHitZoneTopLeftY());
            gc.fillOval(getCircleCenteredWidthPos(n.getLaneNumber()), y, NOTE_SIZE, NOTE_SIZE);
        });
    }

    public void pressKey(KeyCode code) {
        switch (code) {
            case D -> pressedLanes[0] = true;
            case F -> pressedLanes[1] = true;
            case J -> pressedLanes[2] = true;
            case K -> pressedLanes[3] = true;
        }
    }

    public void releaseKey(KeyCode code) {
        switch (code) {
            case D -> pressedLanes[0] = false;
            case F -> pressedLanes[1] = false;
            case J -> pressedLanes[2] = false;
            case K -> pressedLanes[3] = false;
        }
    }

    private int getLaneWidth() {
        return width / LANES;
    }

    private int getHitZoneTopLeftY() {
        return height - 150;
    }

    // This is the absolute perfect y level of a judgement
    private int getHitZoneY() {
        return getHitZoneTopLeftY() + (NOTE_SIZE / 2);
    }

    private int getCircleCenteredWidthPos(int laneNum) {
        int laneWidth = getLaneWidth();
        return (laneWidth * laneNum) + (laneWidth - NOTE_SIZE) / 2;
    }

    private void registerScore(String rating) {
//        switch (rating) {
//            case "Perfect" -> {
//                System.out.println("Perfect hit!");
//                clickedNotes++;
//            }
//            case "Good" -> {
//                System.out.println("Good hit!");
//                clickedNotes++;
//            }
//            case "Miss" -> {
//                System.out.println("Missed!");
//            }
//        }
    }
}
