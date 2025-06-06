package com.mjfelecio.beatsync.judgement;

public enum JudgementWindow {
    PERFECT(30),
    GREAT(80),
    MEH(120),
    MISS(150),
    PERFECT_TAIL(60),
    GREAT_TAIL(160),
    MEH_TAIL(240),
    MISS_TAIL(300);

    private final int millis;

    JudgementWindow(int millis) {
        this.millis = millis;
    }

    public int getMillis() {
        return this.millis;
    }
}
