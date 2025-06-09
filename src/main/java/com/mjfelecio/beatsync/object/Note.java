package com.mjfelecio.beatsync.object;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.config.SettingsManager;

import java.util.Objects;

public class Note {
    private final int laneNumber;
    private final int startTime;
    public long currentTime = 0; // Tracks the current time in the map internally
    private final int endTime; // 0 for normal notes
    private final boolean isHoldNote;
    private boolean hit = false;
    private boolean miss = false;
    private boolean isHeld = false;

    /**
     * Normal note constructor.
     * @param laneNumber zero-based lane index
     * @param startTime time in ms when note should be hit
     */
    public Note(int laneNumber, int startTime) {
        this.laneNumber = laneNumber;
        this.startTime = startTime;
        this.endTime = 0;
        this.isHoldNote = false;
    }

    /**
     * Hold note constructor.
     * @param laneNumber zero-based lane index
     * @param startTime time in ms when hold starts
     * @param endTime time in ms when hold ends
     */
    public Note(int laneNumber, int startTime, int endTime) {
        this.laneNumber = laneNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isHoldNote = endTime > startTime;
    }

    public void update(long timeElapsed) {
        this.currentTime = timeElapsed;
    }

    /**
     * Calculates the current Y-coordinate of the note based on elapsed song time.
     * @return Y coordinate where note should be drawn
     */
    public double calculateY() {
        // Calculate how far through the approach time we are
        double approachTime = SettingsManager.getInstance().getScrollSpeed();
        double timeIntoApproach = currentTime - (startTime - approachTime);
        double progress = timeIntoApproach / approachTime;

        // Clamp progress to -GameConfig.NOTE_DIAMETER so that it looks like it came from above
        // and not just pop out of nowhere
        progress = Math.max(-GameConfig.NOTE_DIAMETER, progress);

        // Notes start at y=0 and move to hitLineY
        return progress * GameConfig.HIT_LINE_Y;
    }

    // Gets the y of the end of the hold note
    public double calculateHoldEndY() {
        // Calculate how far through the approach time we are
        double approachTime = SettingsManager.getInstance().getScrollSpeed();
        double timeIntoApproach = currentTime - (endTime - approachTime);
        double progress = timeIntoApproach / approachTime;

        // Clamp progress to -GameConfig.NOTE_DIAMETER so that it looks like it came from above
        // and not just pop out of nowhere
        progress = Math.max(-GameConfig.NOTE_DIAMETER, progress);

        // Notes start at y=0 and move to hitLineY
        return progress * GameConfig.HIT_LINE_Y;
    }

    public int getLaneNumber() {
        return laneNumber;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isHoldNote() {
        return isHoldNote;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isMiss() {
        return miss;
    }

    public void setMiss(boolean miss) {
        this.miss = miss;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isHeld() {
        return isHeld;
    }

    public void setHeld(boolean b) {
        this.isHeld = b;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return laneNumber == note.laneNumber && startTime == note.startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(laneNumber, startTime);
    }

    @Override
    public String toString() {
        return isHoldNote ? laneNumber + "," + startTime + "," + endTime : laneNumber + "," + startTime;
    }
}
