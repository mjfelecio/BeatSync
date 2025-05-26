package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.judgement.JudgementProcessor;
import com.mjfelecio.beatsync.judgement.JudgementResult;
import com.mjfelecio.beatsync.judgement.JudgementWindow;

import java.util.ArrayList;
import java.util.List;

public class NoteManager {
    private List<Note> notes;
    private List<Note> activeNotes;
    private String judgementResult;

    public final int height = 700;

    public NoteManager(List<Note> notes) {
        this.notes = notes;
        this.activeNotes = new ArrayList<>();
    }

    public void update(long timeElapsed, boolean[] isLanePressed) {
        // Check for notes to activate
        List<Note> notesToActivate = new ArrayList<>();
        for (Note n : notes) {
            if ((timeElapsed >= n.getStartTime() - GameConfig.NOTE_APPROACH_TIME) && !n.isMiss() && !n.isHit()) {
                notesToActivate.add(n);
            }
        }
        activeNotes.addAll(notesToActivate);

        // Remove misses automatically
        activeNotes.removeIf(n -> {
            if (JudgementProcessor.judge(n, timeElapsed) == JudgementResult.MISS) {
                registerScore("Miss");
                n.setMiss(true);
                return true;
            }
            return false;
        });

        // Handle presses
        for (int lane = 0; lane < GameConfig.NUM_LANES; lane++) {
            if (isLanePressed[lane]) {
                Note closestNote = null;
                long timeDeltaToClosestNote = Long.MAX_VALUE;
                for (Note n : activeNotes) {
                    if (n.getLaneNumber() != lane || n.isHit()) continue;
                    long delta = Math.abs(timeElapsed - n.getStartTime());
                    if (delta < timeDeltaToClosestNote && delta <= JudgementWindow.MISS.getMillis()) {
                        timeDeltaToClosestNote = delta;
                        closestNote = n;
                    }
                }

                if (closestNote != null) {
                    closestNote.setHit(true);
                    if (timeDeltaToClosestNote <= JudgementWindow.PERFECT.getMillis()) registerScore("Perfect");
                    else if (timeDeltaToClosestNote <= JudgementWindow.GOOD.getMillis()) registerScore("Good");
                    else {
                        closestNote.setMiss(true);
                        registerScore("Miss");
                    }
                }
            }
        }

        // Remove notes that have passed by the playfield
        activeNotes.removeIf(n -> {
            boolean passedByPlayfield = n.calculateY(timeElapsed, GameConfig.NOTE_APPROACH_TIME, getHitLineY()) > height;
            if (passedByPlayfield) n.setMiss(true);
            return passedByPlayfield;
        });
    }

    public void registerScore(String score) {
        this.judgementResult = score;
    }

    public String getJudgementResult() {
        return judgementResult;
    }

    public int getHitLineY() {
        return height - 150;
    }

    public List<Note> getActiveNotes() {
        return activeNotes;
    }
}
