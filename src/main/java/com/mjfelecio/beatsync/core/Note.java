package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.config.GameConfig;

import java.util.Objects;

public class Note {
    private final int laneNumber;
    private final int startTime;
    public long currentTime = 0; // Tracks the current time in the map internally
    private final int endTime; // 0 for normal notes
    private final boolean isHold;
    private boolean hit = false;
    private boolean miss = false;

    /**
     * Normal note constructor.
     * @param laneNumber zero-based lane index
     * @param startTime time in ms when note should be hit
     */
    public Note(int laneNumber, int startTime) {
        this.laneNumber = laneNumber;
        this.startTime = startTime;
        this.endTime = 0;
        this.isHold = false;
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
        this.isHold = endTime > startTime;
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
        double timeIntoApproach = currentTime - (startTime - GameConfig.NOTE_APPROACH_TIME);
        double progress = timeIntoApproach / GameConfig.NOTE_APPROACH_TIME;

        // Clamp progress to zero so that it doesn't generate far above the playfield
        progress = Math.max(0, progress);

        // Notes start at y=0 and move to hitLineY
        return progress * GameConfig.HIT_LINE_Y;
    }

    public int getLaneNumber() {
        return laneNumber;
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isHold() {
        return isHold;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return laneNumber == note.laneNumber && startTime == note.startTime && endTime == note.endTime && isHold == note.isHold;
    }

    @Override
    public int hashCode() {
        return Objects.hash(laneNumber, startTime, endTime, isHold);
    }

    @Override
    public String toString() {
        return isHold ? laneNumber + "," + startTime + "," + endTime : laneNumber + "," + startTime;
    }
}
