package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.config.SettingsManager;
import com.mjfelecio.beatsync.judgement.JudgementWindow;
import com.mjfelecio.beatsync.object.Note;

import java.util.*;

public class NoteManager {
    private final List<Note> allNotes;
    private final Deque<Note> visibleNotes = new ArrayDeque<>();  // ▶ use Deque for fast add/remove
    private int nextNoteIndex = 0;

    public interface MissCallback {
        void onNoteMissed(Note note);
    }

    private MissCallback missCallback;

    public void setMissCallback(MissCallback missCallback) {
        this.missCallback = missCallback;
    }

    public NoteManager(List<Note> allNotes) {
        this.allNotes = allNotes;
    }

    public void updateVisibleNotes(long timeElapsed) {
        final int DELAY_MS = 100;
        long scrollSpeed = SettingsManager.getInstance().getScrollSpeed();

        // Spawn any new notes whose startTime is within the scroll window
        while (nextNoteIndex < allNotes.size()) {
            Note note = allNotes.get(nextNoteIndex);
            long triggerTime = note.getStartTime() - scrollSpeed - DELAY_MS;
            if (timeElapsed >= triggerTime) {
                note.update(timeElapsed);
                visibleNotes.addLast(note);
                nextNoteIndex++;
            } else {
                break;
            }
        }

        // Update only the notes that are currently on‐screen
        for (Note note : visibleNotes) {
            note.update(timeElapsed);
        }
    }

    public void cullExpiredNotes(long currentTime) {
        Iterator<Note> it = visibleNotes.iterator();
        final int MISS_WINDOW_MS = JudgementWindow.MISS.getMillis();
        final int REMOVAL_DELAY_MS = 100;

        while (it.hasNext()) {
            Note n = it.next();
            long noteTime = n.isHoldNote() ? n.getEndTime() : n.getStartTime();
            long timeSinceNote = currentTime - noteTime;
            boolean shouldRemove = timeSinceNote > (MISS_WINDOW_MS + REMOVAL_DELAY_MS);

            // ▶ don’t cull hold notes that are actively held
            if (n.isHoldNote() && n.isHeld()) {
                continue;
            }

            if (shouldRemove && !n.isHit() && !n.isHeld()) {
                n.setMiss(true);
                if (missCallback != null) missCallback.onNoteMissed(n);
                it.remove();
            } else if (shouldRemove && (n.isHit() || n.isMiss())) {
                it.remove();
            }
        }
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
