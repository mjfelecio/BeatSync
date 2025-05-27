package com.mjfelecio.beatsync.core;

public class GameState {
    private int combo = 0;
    private long score = 0;
    private String lastJudgement = "";
    private boolean isPlaying = false;

    public void incrementCombo() { combo++; }
    public void resetCombo() { combo = 0; }
    public void setJudgement(String judgement) { this.lastJudgement = judgement; }
    public void setPlaying(boolean b) { this.isPlaying = b; }

    public int getCombo() {
        return combo;
    }

    public String getLastJudgement() {
        return lastJudgement;
    }

    public boolean isPlaying() { return isPlaying; }
}
