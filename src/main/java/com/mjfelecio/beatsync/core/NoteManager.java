package com.mjfelecio.beatsync.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NoteManager {
    private List<Note> notes;
    private List<Note> activeNotes;
    private String judgementResult;

    public final int NOTE_APPROACH_TIME = 1000;
    public final int NUM_LANES = 4;
    public final int height = 700;

    public NoteManager(List<Note> notes) {
        this.notes = notes;
        this.activeNotes = new ArrayList<>();
    }

    public void update(long timeElapsed, boolean[] isLanePressed) {
        // Check for notes to activate
        notes.forEach(n -> {
            if ((timeElapsed >= n.getStartTime() - NOTE_APPROACH_TIME) && !n.isMiss() && !n.isHit()) {
                activeNotes.add(n);
            }
        });

        // Remove misses automatically
//        Iterator<Note> missIter = activeNotes.iterator();
//        while (missIter.hasNext()) {
//            Note n = missIter.next();
//            // If the notes wasn't hit already AND has passed the miss window, mark it as a miss
//            if (JudgementProcessor.judge(n, timeElapsed) == JudgementResult.MISS) {
//                registerScore("Miss");
//                n.setMiss(true);
//                missIter.remove();
//            }
//        }

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
        for (int lane = 0; lane < NUM_LANES; lane++) {
            if (isLanePressed[lane]) {
                Note closestNote = null;

                // timeDeltaToClosestNote represents the closest note from the elapsed time in music
                // This should result in only the closest note being removed and not overlapping notes
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


//         Remove notes that have passed by the playfield
        activeNotes.removeIf(n -> {
            boolean passedByPlayfield = n.calculateY(timeElapsed, NOTE_APPROACH_TIME, getHitLineY()) > height;
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
