package com.mjfelecio.beatsync.gameplay;

public class GameSession {
    private int combo = 0;
    private long score = 0;
    private long accuracy = 100;
    private String lastJudgement = "";

    public void incrementCombo() { combo++; }
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

    public long getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public void reset() {
        this.score = 0;
        this.combo = 0;
        this.accuracy = 100;
        this.lastJudgement = "";
    }
}
