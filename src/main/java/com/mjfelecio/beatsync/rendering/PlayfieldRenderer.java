package com.mjfelecio.beatsync.rendering;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.core.GameState;
import com.mjfelecio.beatsync.object.Note;
import com.mjfelecio.beatsync.input.InputState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class PlayfieldRenderer {
    public void render(GraphicsContext gc, GameState gameState,
                       List<Note> visibleNotes, InputState inputState) {
        clearScreen(gc);
        drawPlayfieldBorders(gc);
        drawLanes(gc, inputState);
        drawNotes(gc, visibleNotes);
        drawUI(gc, gameState);
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

    public void drawNotes(GraphicsContext gc, List<Note> visibleNotes) {
        // Draw notes
        visibleNotes.forEach(n -> {
            if (!n.isHit()) {
                if (n.isHold()) {
                    drawHoldNotes(gc, n);
                } else {
                    drawRegularNotes(gc, n);
                }
            }
        });
    }

    public void drawRegularNotes(GraphicsContext gc, Note n) {
        int x = calculateNoteX(n.getLaneNumber());
        double y = n.calculateY();

        gc.setFill(Color.BLUE);
        gc.fillOval(x, y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
    }

    public void drawHoldNotes(GraphicsContext gc, Note n) {
        double startY = n.calculateY();
        double endY = n.calculateHoldEndY();

        // Calculate the height of the long note (Idk if this method works lol)
        // I'm just directly using the timeDiff in milliseconds as the pixel size
        double noteHeight = Math.abs(endY - startY);

        if (startY < GameConfig.PLAYFIELD_HEIGHT) {
            int noteWidth = GameConfig.NOTE_DIAMETER;
            int noteX = calculateNoteX(n.getLaneNumber());

            gc.setFill(Color.CORNFLOWERBLUE);
            gc.fillRoundRect(noteX, endY + noteWidth, noteWidth, noteHeight, noteWidth, noteWidth);
            gc.setFill(Color.BLUE);
            gc.fillOval(noteX, startY, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
        }
    }

    private void drawUI(GraphicsContext gc, GameState gameState) {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(20));
        gc.fillText("Combo: " + gameState.getCombo(), 20, 50);
        gc.setFont(new Font(30));
        gc.fillText(gameState.getLastJudgement(),
                (GameConfig.PLAYFIELD_WIDTH / 2.0) - 50,
                GameConfig.PLAYFIELD_HEIGHT - 250);
    }

    private int calculateNoteX(int laneNumber) {
        int laneWidth = GameConfig.PLAYFIELD_WIDTH / GameConfig.NUM_LANES;
        return (laneWidth * laneNumber) + (laneWidth - GameConfig.NOTE_DIAMETER) / 2;
    }
}
