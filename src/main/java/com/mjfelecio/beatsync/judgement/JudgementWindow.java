package com.mjfelecio.beatsync.judgement;

public enum JudgementWindow {
    PERFECT(30, "Perfect"),
    GOOD(80, "Good"),
    MISS(150, "Miss"),
    PERFECT_TAIL(60, "Perfect"),
    GOOD_TAIL(160, "Good"),
    MISS_TAIL(300, "Miss");

    private final int millis;
    private final String description;

    JudgementWindow(int millis, String description) {
        this.millis = millis;
        this.description = description;
    }

    public int getMillis() {
        return this.millis;
    }

    public String getDescription() {
        return this.description;
    }
}
