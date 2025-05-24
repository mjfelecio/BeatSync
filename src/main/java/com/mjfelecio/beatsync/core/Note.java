package com.mjfelecio.beatsync.core;

public class Note {
    private final int laneNumber;
    private final int startTime;
    private final int endTime; // 0 for normal notes
    private final boolean isHold;
    private boolean hit = false;

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

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    /**
     * Calculates the current Y-coordinate of the note based on elapsed song time.
     * @param elapsedMs elapsed milliseconds since song start
     * @param approachTimeMs how early the note appears before hit
     * @param hitLineY vertical position of the hit line
     * @return Y coordinate where note should be drawn
     */
    public double calculateY(long elapsedMs, long approachTimeMs, int hitLineY) {
        // Calculate how far through the approach time we are
        double timeIntoApproach = elapsedMs - (startTime - approachTimeMs);
        double progress = timeIntoApproach / approachTimeMs;

        // Clamp progress between 0 and 1
        progress = Math.max(0.0, Math.min(1.0, progress));

        // Notes start at y=0 and move to hitLineY
        return progress * hitLineY;
    }

    @Override
    public String toString() {
        return isHold ? laneNumber + "," + startTime + "," + endTime : laneNumber + "," + startTime;
    }
}
