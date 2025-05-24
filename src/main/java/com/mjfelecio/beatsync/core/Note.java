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

    public long getTime() {
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
    public double getY(long elapsedMs, long approachTimeMs, int hitLineY) {
        double timeUntilHit = startTime - elapsedMs;
        double progress = 1.0 - (timeUntilHit / approachTimeMs);
        return hitLineY * Math.min(Math.max(progress, 0.0), 1.0);
    }

    @Override
    public String toString() {
        return isHold ? laneNumber + "," + startTime + "," + endTime : laneNumber + "," + startTime;
    }
}
