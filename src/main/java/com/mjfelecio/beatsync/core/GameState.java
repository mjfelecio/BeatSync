package com.mjfelecio.beatsync.core;

public class GameState {
    private int combo = 0;
    private long score = 0;
    private String lastJudgement = "";
    private boolean isPlaying = false;

    public void incrementCombo() { combo++; }
    public void resetCombo() { combo = 0; }
    public void setJudgement(String judgement) { this.lastJudgement = judgement; }
}
