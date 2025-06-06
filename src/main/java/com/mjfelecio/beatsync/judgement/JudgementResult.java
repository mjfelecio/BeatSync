package com.mjfelecio.beatsync.judgement;

public enum JudgementResult {
    PERFECT(300),
    GREAT(200),
    MEH(100),
    MISS(0);

    private final int score;

    JudgementResult(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
