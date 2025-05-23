package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.hydev.obp.BeatmapReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Playfield {
    private final int width;
    private final int height;
    private final int LANES = 4;
    private final int NOTE_SIZE = 80;

    public ArrayList<Note> notes;
    public ArrayList<Note> activeNotes;

    long startTime;

    public Playfield(int width, int height) {
        this.width = width;
        this.height = height;
        this.startTime = System.nanoTime();

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
        long currentTime = System.nanoTime();
        long timeElapsed = currentTime - startTime;

        // Check for notes to activate
        notes.forEach(n -> {
            if ((timeElapsed / 1_000_000) >= n.getTime() && !activeNotes.contains(n)) {
                activeNotes.add(n);
            }
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

            // Add circles as indications for the hit zones in each lane
            gc.strokeOval(circleCenteredWidthPos, hitZonePos, NOTE_SIZE, NOTE_SIZE);
        }
    }

    private int getLaneWidth() {
        return width / LANES;
    }

    private int getHitZonePos() {
        return height - 150;
    }

    private int getCircleCenteredWidthPos(int laneNum) {
        int laneWidth = getLaneWidth();
        return (laneWidth * laneNum) + (laneWidth - NOTE_SIZE) / 2;
    }


}
