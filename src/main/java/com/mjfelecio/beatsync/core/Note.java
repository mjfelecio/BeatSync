package com.mjfelecio.beatsync.core;

public class Note {
    private final int laneNumber;
    private int y = -10; // Notes start at the very top of the screen anyway
    private final int startTime;

    public Note(int laneNumber, int startTime) {
        this.laneNumber = laneNumber;
        this.startTime = startTime;
    }

    public int getLaneNumber() { return laneNumber; }
    public int getY() { return y; }
    public int getStartTime() { return startTime; }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return laneNumber + "," + startTime;
    }
}
