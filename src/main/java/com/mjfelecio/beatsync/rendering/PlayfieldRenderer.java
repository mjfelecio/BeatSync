package com.mjfelecio.beatsync.rendering;

import com.mjfelecio.beatsync.config.GameConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PlayfieldRenderer {
    public void render(GraphicsContext gc) {
        clearScreen(gc);
        drawPlayfieldBorders(gc);
    }

    public void clearScreen(GraphicsContext gc) {
        gc.clearRect(0, 0, GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
    }

    public void drawPlayfieldBorders(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeRect(0, 0, GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
    }

}
