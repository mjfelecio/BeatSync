package com.mjfelecio.beatsync.object;

import java.time.LocalDateTime;

public class Score {
    private final int id;
    private final int beatmapID;
    private final Rank rank;
    private final long score;
    private final double accuracy;
    private final int maxCombo;
    private final LocalDateTime submittedAt;

    public Score(int id, int beatmapID, Rank rank, long score, double accuracy, int maxCombo) {
        this.id = id;
        this.beatmapID = beatmapID;
        this.rank = rank;
        this.score = score;
        this.accuracy = accuracy;
        this.maxCombo = maxCombo;
        this.submittedAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
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
}
