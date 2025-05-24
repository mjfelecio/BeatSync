package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Playfield {
    private final int width;
    private final int height;
    private final int LANES = 4;
    private final int NOTE_SIZE = 80;
    private GameClock gameClock;

    public ArrayList<Note> notes;
    public ArrayList<Note> activeNotes;

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

    public void update(GraphicsContext gc) {
        long timeElapsed = gameClock.getElapsedTime();

        // Check for notes to activate
        notes.forEach(n -> {
            if ((timeElapsed >= n.getTime()) && !activeNotes.contains(n) && !n.isHit()) {
                activeNotes.add(n);
            }
        });

        // Handle automatic misses (position-based)
        activeNotes.removeIf(n -> {
            int noteBottomY = n.getY() + NOTE_SIZE; // bottom of the note
            int hitZoneY = getHitZonePos();

            // If note has moved completely past the hit zone and wasn't hit
            if (!n.isHit() && noteBottomY > hitZoneY + NOTE_SIZE / 2 + MISS_WINDOW) {
                registerScore("Miss");
                return true;
            }

            return false;
        });

        // Handle key pressKey detection for hit scoring
        for (int lane = 0; lane < pressedLanes.length; lane++) {
            if (pressedLanes[lane]) {
                Note bestMatch = null;
                long smallestDelta = Long.MAX_VALUE;

                for (Note note : activeNotes) {
                    if (note.isHit() || note.getLaneNumber() != lane) continue;

                    long delta = Math.abs(note.getTime() - timeElapsed);
                    if (delta < smallestDelta && delta <= MISS_WINDOW) {
                        bestMatch = note;
                        smallestDelta = delta;
                    }
                }

                if (bestMatch != null) {
                    bestMatch.setHit(true);
                    if (smallestDelta <= PERFECT_WINDOW) {
                        registerScore("Perfect");
                    } else if (smallestDelta <= GOOD_WINDOW) {
                        registerScore("Good");
                    } else {
                        registerScore("Miss");
                    }
                }
            }
        }

        // Remove pressed notes
        activeNotes.removeIf(n -> {
            int hitZoneCenter = getHitZonePos() + NOTE_SIZE / 2;
            int noteCenterY = n.getY() + NOTE_SIZE / 2;
            long diff = Math.abs(noteCenterY - hitZoneCenter);

            // Adjust timing windows as needed (30ms, 80ms, etc.)
            boolean pressed = diff <= 30 && pressedLanes[n.getLaneNumber()];


            if (pressed) n.setHit(true);

            return pressed;
        });

        // Render active notes
        activeNotes.removeIf(n -> n.getY() >= height); // Remove notes that have passed the playfield
        activeNotes.forEach(n -> {
            gc.strokeOval(getCircleCenteredWidthPos(n.getLaneNumber()), n.getY(), NOTE_SIZE, NOTE_SIZE);
            n.setY(n.getY() + 1);
        });
    }

    public void render(GraphicsContext gc) {
        int laneWidth = getLaneWidth();

        // Add miss zones cut-off for testing
        gc.setStroke(Color.RED);
        gc.setLineWidth(5);
        gc.strokeLine(0, getHitZonePos() + NOTE_SIZE, width, getHitZonePos() + NOTE_SIZE);
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
            int hitZonePos = getHitZonePos();

            if (pressedLanes[i]) {
                gc.setFill(Color.RED);
                gc.fillOval(circleCenteredWidthPos, hitZonePos, NOTE_SIZE, NOTE_SIZE);
            }

            // Add circles as indications for the hit zones in each lane
            gc.strokeOval(circleCenteredWidthPos, hitZonePos, NOTE_SIZE, NOTE_SIZE);
        }
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

    private int getHitZonePos() {
        return height - 150;
    }

    private int getHitZoneCenter() {
        return getHitZonePos() - (NOTE_SIZE / 2);
    }

    private int getCircleCenteredWidthPos(int laneNum) {
        int laneWidth = getLaneWidth();
        return (laneWidth * laneNum) + (laneWidth - NOTE_SIZE) / 2;
    }

    private void registerScore(String rating) {
        switch (rating) {
            case "Perfect" -> {
                System.out.println("Perfect hit!");
                clickedNotes++;
            }
            case "Good" -> {
                System.out.println("Good hit!");
                clickedNotes++;
            }
            case "Miss" -> {
                System.out.println("Missed!");
            }
        }
    }
}
