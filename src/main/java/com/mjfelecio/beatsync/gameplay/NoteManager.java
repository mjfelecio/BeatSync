package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.config.SettingsManager;
import com.mjfelecio.beatsync.judgement.JudgementWindow;
import com.mjfelecio.beatsync.object.Note;

import java.util.*;

public class NoteManager {
    private final List<Note> allNotes;
    private final Set<Note> visibleNotes;

    public interface MissCallback {
        void onNoteMissed(Note note);
    }

    private MissCallback missCallback;

    public void setMissCallback(MissCallback missCallback) {
        this.missCallback = missCallback;
    }

    public NoteManager(List<Note> allNotes) {
        this.allNotes = allNotes;
        this.visibleNotes = new HashSet<>();
    }

    public void updateVisibleNotes(long timeElapsed) {
        allNotes.forEach(n -> n.update(timeElapsed));

        // Delay to make sure that the notes get added to the visibleNotes earlier.
        int DELAY_MS = 100;

        for (Note note : allNotes) {
            if (       !note.isMiss()
                    && !note.isHit()
                    && timeElapsed >= note.getStartTime() - SettingsManager.getInstance().getScrollSpeed() - DELAY_MS)
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

            // If it's a hold note, we should only cull it if the tail of the note has passed the playfield
            long noteTime = n.isHoldNote() ? n.getEndTime() : n.getStartTime();

            long timeSinceNote = currentTime - noteTime;
            boolean shouldRemove = timeSinceNote > (MISS_WINDOW_MS + REMOVAL_DELAY_MS);

            // For hold notes, if the player is still holding, we don't want to cull yet
            if (n.isHoldNote() && n.isHeld()) {
                return false;
            }

            // If it's time to remove it, and it hasn't been hit/held, mark a miss
            if (shouldRemove && !n.isHit() && !n.isHeld()) {
                n.setMiss(true);
                if (missCallback != null) {
                    missCallback.onNoteMissed(n);
                }
                return true;
            }

            // Otherwise, only remove it if we've already judged it (hit or miss) and we're past the window
            return shouldRemove && (n.isHit() || n.isMiss());
        });
    }

    public Note getHittableNote(int laneNumber, long currentTime) {
        return visibleNotes.stream()
                           .filter(n -> n.getLaneNumber() == laneNumber)
                           .filter(n -> !n.isHit() && !n.isMiss())
                           .filter(n -> isWithinHitWindow(n, currentTime))
                           .min(Comparator.comparingLong(n -> Math.abs(currentTime - n.getStartTime())))
                           .orElse(null);
    }

    private boolean isWithinHitWindow(Note note, long currentTime) {
        long timeDiff = Math.abs(currentTime - note.getStartTime());
        return timeDiff <= JudgementWindow.MISS.getMillis();
    }

    public Note getHittableHeldNote(int laneNumber, long currentTime) {
        return visibleNotes.stream()
                .filter(n -> n.getLaneNumber() == laneNumber)
                .filter(Note::isHeld)
                .min(Comparator.comparingLong(n -> Math.abs(currentTime - n.getEndTime())))
                .orElse(null);
    }

    public List<Note> getVisibleNotes() {
        // Return a copy to prevent external modification
        return new ArrayList<>(visibleNotes);
    }
}
