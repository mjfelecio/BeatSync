package com.mjfelecio.beatsync.parser.obj;

public class HitObject {
    private final int x, time, type, hitSound;
    private int y;
    private final Integer endTime; // null for normal notes

    public HitObject(int x, int y, int time, int type, int hitSound, Integer endTime) {
        this.x = x;
        this.y = y;
        this.time = time;
        this.type = type;
        this.hitSound = hitSound;
        this.endTime = endTime;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getTime() { return time; }
    public int getEndTime() { return endTime; }
    public void setY(int y) { this.y = y; }

    public boolean isHold() {
        return (type & 128) != 0;
    }

    @Override
    public String toString() {
        return isHold()
                ? String.format("Hold: x=%d, time=%d, endTime=%d", x, time, endTime)
                : String.format("HitObject: x=%d, time=%d", x, time);
    }
}
