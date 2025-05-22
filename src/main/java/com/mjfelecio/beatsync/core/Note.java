package com.mjfelecio.beatsync.core;

public class Note {
    private final int x;
    private int y = -10; // Notes start at the very top of the screen anyway
    private final int startTime;

    public Note(int x, int startTime) {
        this.x = x;
        this.startTime = startTime;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getStartTime() { return startTime; }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return x + "," + startTime;
    }
}
