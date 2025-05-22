package com.mjfelecio.beatsync.core;

public class Note {
    private final int x;
    private int y = -10; // Notes start at the very top of the screen anyway
    private final int startTime;

    public Note(int laneNumber, int startTime) {
        this.x = laneNumber;
        this.startTime = startTime;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getStartTime() { return startTime; }

    public void setY(int y) {
        this.y = y;
    }

    public int getLaneNumber() {
        int lane = 0;
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
        return x + "," + startTime;
    }
}
