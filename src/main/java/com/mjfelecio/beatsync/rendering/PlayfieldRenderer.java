package com.mjfelecio.beatsync.rendering;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.core.GameState;
import com.mjfelecio.beatsync.core.Note;
import com.mjfelecio.beatsync.input.InputState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class PlayfieldRenderer {
    public void render(GraphicsContext gc, GameState gameState,
                       List<Note> visibleNotes, InputState inputState) {

        clearScreen(gc);
        drawPlayfieldBorders(gc);
        drawLanes(gc, inputState);
//        drawNotes(gc, visibleNotes);
//        drawUI(gc, gameState);
    }

    public void clearScreen(GraphicsContext gc) {
        gc.clearRect(0, 0, GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
    }

    public void drawPlayfieldBorders(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setLineWidth(8);
        gc.strokeRect(0, 0, GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
    }

    public void drawLanes(GraphicsContext gc, InputState inputState) {
        int laneWidth = GameConfig.PLAYFIELD_WIDTH / GameConfig.NUM_LANES;

        for (int i = 0; i < GameConfig.NUM_LANES; i++) {
            // Draw lane separator
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeLine(laneWidth * i, 0, laneWidth * i, GameConfig.PLAYFIELD_HEIGHT);

            // Draw hit zone indicators
            int centerX = (laneWidth * i) + (laneWidth - GameConfig.NOTE_DIAMETER) / 2;
            gc.strokeOval(centerX, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);

            if (inputState.isLanePressed(i)) {
                gc.setFill(Color.RED);
                gc.fillOval(centerX, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
                gc.setFill(Color.BLACK);
            }

            gc.strokeOval(centerX, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
        }
    }
}
