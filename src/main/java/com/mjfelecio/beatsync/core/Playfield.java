package com.mjfelecio.beatsync.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.hydev.obp.BeatmapReader;

import java.io.File;
import java.util.ArrayList;

public class Playfield {
    private final int width;
    private final int height;
    private final int LANES = 4;
    private final int NOTE_SIZE = 80;

    private final File beatmapFile;
    public ArrayList<Note> notes;

    long startTime;

    public Playfield(int width, int height) {
        this.width = width;
        this.height = height;
        this.startTime = System.currentTimeMillis();

        // Initializing a map here temporarily
        beatmapFile = new File("src/main/resources/com/mjfelecio/beatsync/beatmaps/test.osu");
        notes = new BeatmapParser(BeatmapReader.parse(beatmapFile)).parseNotes();
    }

    public void update(GraphicsContext gc) {
        notes.forEach(n -> {
            long timeElapsed = System.currentTimeMillis() - startTime;

            if (timeElapsed >= n.getStartTime()) {
                gc.strokeOval(getCircleCenteredWidthPos(n.getLaneNumber()), n.getY(), NOTE_SIZE, NOTE_SIZE);
                n.setY(n.getY() + 1);
            }
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
