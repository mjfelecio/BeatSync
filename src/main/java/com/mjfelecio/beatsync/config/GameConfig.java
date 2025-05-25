package com.mjfelecio.beatsync.config;

public final class GameConfig {
    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;
    public static final int PLAYFIELD_WIDTH = 400;
    public static final int PLAYFIELD_HEIGHT = 700;

    public static final int NUM_LANES = 4;
    public static final int NOTE_DIAMETER = 80;
    // Will probably swap this to a double later if I want them to control approach time
    public static final int NOTE_APPROACH_TIME = 1000;

    public static final int HIT_LINE_Y = PLAYFIELD_HEIGHT - 150;
    public static final String TEST_BEATMAP_PATH = "src/main/resources/com/mjfelecio/beatsync/beatmaps/test.osu";

    private GameConfig() {} // Prevent instantiation
}

