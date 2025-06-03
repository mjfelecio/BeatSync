package com.mjfelecio.beatsync.judgement;

public class ScoreManager {
    private int currentScore = 0;
    private final int maxScore;
    private int totalHits = 0;
    private int totalPossibleScore = 0;

    public ScoreManager(int regularNoteCount, int holdNoteCount) {
        this.maxScore = (regularNoteCount * 300) + (holdNoteCount * 2 * 300);
    }

    public void registerJudgement(JudgementResult judgement) {
        currentScore += judgement.getScore();
        totalPossibleScore += 300;
        totalHits++;
    }

    public double getAccuracy() {
        return totalHits == 0 ? 100.0 : (currentScore * 100.0 / (totalHits * 300));
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
