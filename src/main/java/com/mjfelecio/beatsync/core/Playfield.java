package com.mjfelecio.beatsync.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Playfield {
    private int width;
    private int height;
    private final int LANES = 4;
    private final int NOTE_SIZE = 80;

    public Playfield(int width, int height) {
        this.width = width;
        this.height = height;
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

            int circleCenteredWidthPos = (laneWidth * i) + (laneWidth - NOTE_SIZE) / 2;
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


}
