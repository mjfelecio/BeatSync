package com.mjfelecio.beatsync.rendering;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.gameplay.GameSession;
import com.mjfelecio.beatsync.object.Note;
import com.mjfelecio.beatsync.input.InputState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class PlayfieldRenderer {
    public void render(GraphicsContext gc, GameSession gameSession,
                       List<Note> visibleNotes, InputState inputState) {
        clearScreen(gc);
        drawPlayfieldBorders(gc);
        drawLanes(gc, inputState);
        drawNotes(gc, visibleNotes);
        drawUI(gc, gameSession);
    }

    public void renderEmptyPlayfield(GraphicsContext gc, GameSession gameSession) {
        clearScreen(gc);
        drawPlayfieldBorders(gc);
        drawLanes(gc, new InputState());
        drawUI(gc, gameSession);
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
                if (n.isHoldNote()) {
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

        Color color;

        if (n.getLaneNumber() == 0 || n.getLaneNumber() == 3) {
            color = GameConfig.OUTER_NOTE_COLOR;
        } else {
            color = GameConfig.INNER_NOTE_COLOR;
        }

        gc.setFill(color);
        gc.fillOval(x, y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
    }

    public void drawHoldNotes(GraphicsContext gc, Note n) {
        double startY = n.calculateY();
        double endY = n.calculateHoldEndY();

        double noteHeight = Math.abs(endY - startY);

        if (endY < GameConfig.PLAYFIELD_HEIGHT) {
            int noteWidth = GameConfig.NOTE_DIAMETER;
            int noteX = calculateNoteX(n.getLaneNumber());
            Color noteColor = getLaneColor(n.getLaneNumber());

            // Darken the color of the hold note if it is a miss or is being held
            Color holdColor = n.isHeld() || n.isMiss() ?
                    noteColor.darker() :
                    noteColor.desaturate();

            // TODO: Calculate the arc of the holdNote better so that it doesn't squish when it's height is absurdly small
            if (!n.isHeld()) {
                gc.setFill(holdColor);
                gc.fillRoundRect(noteX, endY + noteWidth, noteWidth, noteHeight, noteWidth, noteWidth);
                gc.setFill(noteColor);
                gc.fillOval(noteX, startY, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
            } else {
                // This makes it so that if the note is held, it doesn't extend beyond the hitline
                double noteHeightTillHitLine = GameConfig.HIT_LINE_Y - endY;

                gc.setFill(holdColor);
                gc.fillRoundRect(noteX, endY + noteWidth, noteWidth, noteHeightTillHitLine, noteWidth, noteWidth);
                gc.setFill(noteColor);
                gc.fillOval(noteX, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
            }

        }
    }

    private void drawUI(GraphicsContext gc, GameSession gameSession) {
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(20));
        // These are just temporary
        gc.fillText("Combo: " + gameSession.getCombo(), 20, 50);
        gc.fillText("Score: " + gameSession.getScore(), 20, 80);
        gc.fillText("Accuracy: " + gameSession.getAccuracy(), 20, 100);

        gc.setFont(new Font(30));
        gc.fillText(gameSession.getLastJudgement(),
                (GameConfig.PLAYFIELD_WIDTH / 2.0) - 50,
                GameConfig.PLAYFIELD_HEIGHT - 250);
    }

    private int calculateNoteX(int laneNumber) {
        int laneWidth = GameConfig.PLAYFIELD_WIDTH / GameConfig.NUM_LANES;
        return (laneWidth * laneNumber) + (laneWidth - GameConfig.NOTE_DIAMETER) / 2;
    }

    /**
     * Gets the color of the note based on its lane.
     * If it's an outer lane, it uses the outer note color.
     * If it's an inner lane, it uses the inner lane color.
     *
     * @param laneNumber the lane number of the note (zero-indexed)
     * @return Color - the color of the laneNumber
    * */
    private Color getLaneColor(int laneNumber) {
        Color color = null;
        if (laneNumber == 0 || laneNumber == 3) {
            color = GameConfig.OUTER_NOTE_COLOR;
        } else if (laneNumber == 1 || laneNumber == 2) {
            color = GameConfig.INNER_NOTE_COLOR;
        }
        return color;
    }
}
