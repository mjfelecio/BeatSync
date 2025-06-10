package com.mjfelecio.beatsync.rendering;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.gameplay.GameSession;
import com.mjfelecio.beatsync.object.Note;
import com.mjfelecio.beatsync.input.InputState;
import com.mjfelecio.beatsync.utils.FontProvider;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;

public class PlayfieldRenderer {
    private static final Color[] LANE_COLORS = {
            GameConfig.OUTER_NOTE_COLOR, // Lane 0
            GameConfig.INNER_NOTE_COLOR, // Lane 1
            GameConfig.INNER_NOTE_COLOR, // Lane 2
            GameConfig.OUTER_NOTE_COLOR  // Lane 3
    };

    public void render(GraphicsContext gc, GameSession gameSession,
                       List<Note> visibleNotes, InputState inputState) {
        clearCanvasToBlack(gc);
        drawHitZoneIndicators(gc, inputState);
        drawNotes(gc, visibleNotes);
        drawUI(gc, gameSession);
    }

    public void clearCanvasToBlack(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
    }

    public void drawHitZoneIndicators(GraphicsContext gc, InputState inputState) {
        int laneWidth = GameConfig.PLAYFIELD_WIDTH / GameConfig.NUM_LANES;

        for (int i = 0; i < GameConfig.NUM_LANES; i++) {
            gc.setLineWidth(2);

            // Draw hit zone indicators
            int centerX = (laneWidth * i) + (laneWidth - GameConfig.NOTE_DIAMETER) / 2;
            gc.setStroke(Color.WHITE);
            gc.strokeOval(centerX, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);

            // Make the hit zone flash to white if the lane is pressed
            if (inputState.isLanePressed(i)) {
                gc.setFill(Color.WHITE);
                gc.fillOval(centerX, GameConfig.HIT_LINE_Y, GameConfig.NOTE_DIAMETER, GameConfig.NOTE_DIAMETER);
            }
        }
    }

    public void drawNotes(GraphicsContext gc, List<Note> visibleNotes) {
        // Draw notes
        for (Note n : visibleNotes) {
            if (!n.isHit()) {
                if (n.isHoldNote()) {
                    drawHoldNotes(gc, n);
                } else {
                    drawRegularNotes(gc, n);
                }
            }
        }
    }

    public void drawRegularNotes(GraphicsContext gc, Note n) {
        int x = calculateNoteX(n.getLaneNumber());
        double y = n.calculateY();

        Color color = getLaneColor(n.getLaneNumber());

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

    private final Text measurementText = new Text();

    private void drawUI(GraphicsContext gc, GameSession gameSession) {
        gc.setFill(Color.LIGHTBLUE);

        double centerX = GameConfig.PLAYFIELD_WIDTH / 2.0;
        double judgementY = GameConfig.PLAYFIELD_HEIGHT - 250;
        double comboY = judgementY - 50;

        // Draw combo (centered)
        String comboText = String.valueOf(gameSession.getCombo());
        Font comboFont = FontProvider.ARCADE_R.getFont(20);
        gc.setFont(comboFont);
        double comboWidth = getTextWidth(comboText, comboFont);
        gc.fillText(comboText, centerX - (comboWidth / 2), comboY);

        // Draw judgement (centered)
        String judgementText = gameSession.getLastJudgement();
        if (judgementText != null && !judgementText.isEmpty()) {
            Font judgementFont = FontProvider.ARCADE_R.getFont(20);
            gc.setFill(getJudgementColor(judgementText));
            gc.setFont(judgementFont);
            double judgementWidth = getTextWidth(judgementText, judgementFont);
            gc.fillText(judgementText, centerX - (judgementWidth / 2), judgementY);
        }
    }

    private Color getJudgementColor(String judgement) {
        return switch (judgement.toUpperCase()) {
            case "PERFECT" -> Color.GOLD;
            case "GREAT" -> Color.LIGHTBLUE;
            case "MEH" -> Color.YELLOW;
            case "MISS" -> Color.RED;
            default -> Color.WHITE;
        };
    }

    private double getTextWidth(String text, Font font) {
        measurementText.setText(text);
        measurementText.setFont(font);
        return measurementText.getBoundsInLocal().getWidth();
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
        return LANE_COLORS[laneNumber];
    }
}
