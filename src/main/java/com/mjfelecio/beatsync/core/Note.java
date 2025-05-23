package com.mjfelecio.beatsync.core;

public class Note {
    private final int x;
    private int y = -100; // Notes start at the very top of the screen anyway
    private final int time;
    private final int endTime; // Null for normal notes
    private final boolean isHoldNote;

    // Creates a normal note
    public Note(int laneNumber, int startTime) {
        this.x = laneNumber;
        this.time = startTime;
        this.endTime = 0;
        this.isHoldNote = false;
    }

    // Creates a hold note
    public Note(int laneNumber, int startTime, int endTime) {
        this.x = laneNumber;
        this.time = startTime;
        this.endTime = endTime;
        this.isHoldNote = true;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getTime() { return time; }
    public int getEndTime() { return endTime; }
    public void setY(int y) { this.y = y; }

    public int getLaneNumber() {
        int lane = 0;

        // Note that the lanes are zero-indexed
        switch (x) {
            case 64 -> lane = 0;
            case 192 -> lane = 1;
            case 320 -> lane = 2;
            case 448 -> lane = 3;
        }

        return lane;
    }

    @Override
    public String toString() {
        return isHoldNote ? x + "," + time + "," + endTime : x + "," + time;
    }
}
