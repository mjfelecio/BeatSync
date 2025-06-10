package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.object.Rank;

public class GameSession {
    private int combo = 0;
    private long score = 0;
    private double accuracy = 100;
    private String lastJudgement = "";
    private int maxCombo = 0;
    private Rank rank;

    // Judgement counts
    private int perfectCount = 0;
    private int greatCount = 0;
    private int mehCount = 0;
    private int missCount = 0;

    public void incrementCombo() {
        combo++;

        // Determine the max combo
        if (maxCombo < combo) {
            maxCombo = combo;
        }
    }
    public void resetCombo() { combo = 0; }
    public int getCombo() {
        return combo;
    }

    public String getLastJudgement() {
        return lastJudgement;
    }
    public void setJudgement(String judgement) { this.lastJudgement = judgement; }

    public long getScore() {
        return score;
    }
    public void setScore(long score) {
        this.score = score;
    }

    public double getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public int getPerfectCount() {
        return perfectCount;
    }

    public void incrementPerfectCount() {
        this.perfectCount++;
    }

    public int getGreatCount() {
        return greatCount;
    }

    public void incrementGreatCount() {
        this.greatCount++;
    }

    public int getMehCount() {
        return mehCount;
    }

    public void incrementMehCount() {
        this.mehCount++;
    }

    public int getMissCount() {
        return missCount;
    }

    public void incrementMissCount() {
        this.missCount++;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void reset() {
        this.score = 0;
        this.combo = 0;
        this.accuracy = 100;
        this.lastJudgement = "";
        perfectCount = 0;
        greatCount = 0;
        mehCount = 0;
        missCount = 0;
        maxCombo = 0;
        rank = null;
    }
}
