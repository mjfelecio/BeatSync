package com.mjfelecio.beatsync.object;

import com.mjfelecio.beatsync.judgement.JudgementResult;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Score {
    private final int beatmapID;
    private final Rank rank;
    private final long score;
    private final double accuracy;
    private final int maxCombo;
    private final LocalDateTime submittedAt;
    private final Map<JudgementResult, Integer> judgementCounts;

    public Score(int beatmapID, Rank rank, long score, double accuracy, int maxCombo, Map<JudgementResult, Integer> judgementCounts, String submittedAt) {
        this.beatmapID = beatmapID;
        this.rank = rank;
        this.score = score;
        this.accuracy = accuracy;
        this.maxCombo = maxCombo;
        this.judgementCounts = judgementCounts;
        this.submittedAt = submittedAt == null ? LocalDateTime.now() : LocalDateTime.parse(submittedAt);
    }

    public int getBeatmapID() {
        return beatmapID;
    }

    public Rank getRank() {
        return rank;
    }

    public long getScore() {
        return score;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public Map<JudgementResult, Integer> getJudgementCounts() {
        return new HashMap<>(judgementCounts);
    }

    @Override
    public String toString() {
        return "Score{" +
                ", beatmapID=" + beatmapID +
                ", rank=" + rank +
                ", score=" + score +
                ", accuracy=" + accuracy +
                ", maxCombo=" + maxCombo +
                ", submittedAt=" + submittedAt +
                ", judgementCounts=" + judgementCounts +
                '}';
    }
}
