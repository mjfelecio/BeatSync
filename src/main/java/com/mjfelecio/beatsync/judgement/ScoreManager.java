package com.mjfelecio.beatsync.judgement;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ScoreManager {
    private int currentScore = 0;
    private final int maxScore;
    private int totalHits = 0;
    private int totalPossibleScore = 0;
    private final int PERFECT_SCORE_VALUE = JudgementResult.PERFECT.getScore();

    public ScoreManager(int regularNoteCount, int holdNoteCount) {
        this.maxScore = (regularNoteCount * PERFECT_SCORE_VALUE) + (holdNoteCount * 2 * PERFECT_SCORE_VALUE);
    }

    public void registerJudgement(JudgementResult judgement) {
        currentScore += judgement.getScore();
        totalPossibleScore += PERFECT_SCORE_VALUE;
        totalHits++;
    }

    public double getAccuracy() {
        double accuracy = totalHits == 0 ? 100.0 : (currentScore * 100.0 / (totalHits * PERFECT_SCORE_VALUE));
        // Round to the 2 decimal digits
        return new BigDecimal(accuracy).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public String getRank() {
        double percent = (currentScore * 100.0 / maxScore);
        if (percent >= 90) return "S";
        if (percent >= 70) return "A";
        if (percent >= 50) return "B";
        if (percent >= 40) return "C";
        if (percent >= 30) return "D";
        return "F";
    }

    public int getScore() {
        return currentScore;
    }

    public int getMaxScore() {
        return maxScore;
    }
}
