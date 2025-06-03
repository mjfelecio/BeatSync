package com.mjfelecio.beatsync.judgement;

public enum JudgementResult {
    PERFECT(300),
    GOOD(200),
    MISS(0);

    private final int score;

    JudgementResult(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
