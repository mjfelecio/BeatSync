package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.judgement.JudgementWindow;
import com.mjfelecio.beatsync.object.Note;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NoteManager {
    private final List<Note> allNotes;
    private final List<Note> visibleNotes;

    public interface MissCallback {
        void onNoteMissed(Note note);
    }

    private MissCallback missCallback;

    public void setMissCallback(MissCallback missCallback) {
        this.missCallback = missCallback;
    }

    public NoteManager(List<Note> allNotes) {
        this.allNotes = allNotes;
        this.visibleNotes = new ArrayList<>();
    }

    public void updateNotesPosition(long timeElapsed) {
        allNotes.forEach(n -> n.update(timeElapsed));

        // Add newly visible notes based on currentTime
        for (Note note : allNotes) {
            if (   !note.isMiss()
                && !note.isHit()
                && timeElapsed >= note.getStartTime() - GameConfig.NOTE_APPROACH_TIME
                && !visibleNotes.contains(note))
            {
                visibleNotes.add(note);
            }
        }
    }

    public void cullExpiredNotes(long currentTime) {
        visibleNotes.removeIf(n -> {
            final int MISS_WINDOW_MS = JudgementWindow.MISS.getMillis();
            // Arbitrary delay to allow the note to move below the playfield before removing it
            final int REMOVAL_DELAY_MS = 100;

            long timeSinceNote = currentTime - n.getStartTime();
            boolean shouldRemove = timeSinceNote > (MISS_WINDOW_MS + REMOVAL_DELAY_MS);

            if (shouldRemove && !n.isHit()) {
                n.setMiss(true);
            }

            // Sends a callback to an outside class that can listen if a note was missed
            // due to the note being beyond the hitZone
            if (missCallback != null && shouldRemove) {
                if (n.isMiss()) {
                    missCallback.onNoteMissed(n);
                }
            }

            return shouldRemove;
        });
    }

    public Note getHittableNote(int laneNumber, long currentTime) {
        return visibleNotes.stream()
                           .filter(n -> n.getLaneNumber() == laneNumber)
                           .filter(n -> !n.isHit())
                           .filter(n -> isWithinHitWindow(n, currentTime))
                           .min(Comparator.comparingLong(n -> Math.abs(currentTime - n.getStartTime())))
                           .orElse(null);
    }

    private boolean isWithinHitWindow(Note note, long currentTime) {
        long timeDiff = Math.abs(currentTime - note.getStartTime());
        return timeDiff <= JudgementWindow.MISS.getMillis();
    }

    public List<Note> getVisibleNotes() {
        // Return a copy to prevent external modification
        return new ArrayList<>(visibleNotes);
    }
}
