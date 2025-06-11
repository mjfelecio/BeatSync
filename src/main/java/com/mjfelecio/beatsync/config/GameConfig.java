package com.mjfelecio.beatsync.config;

import javafx.scene.paint.Color;

public final class GameConfig {
    public static final String ENVIRONMENT = "DEV"; // OR PROD

    public static final int SCREEN_WIDTH = 1920;
    public static final int SCREEN_HEIGHT = 1080;
    public static final int PLAYFIELD_WIDTH = 400;
    public static final int PLAYFIELD_HEIGHT = 700;

    public static final int NUM_LANES = 4;
    public static final int NOTE_DIAMETER = 80;

    public static final int AUDIO_LEAD_IN = 1000;

    public static final int HIT_LINE_Y = PLAYFIELD_HEIGHT - 150;
    public static final String BEATMAP_DIRECTORY = ENVIRONMENT.equals("DEV") ?  "src/main/resources/com/mjfelecio/beatsync/beatmaps" : "beatmaps";

    // Note Colors
    public static Color INNER_NOTE_COLOR = Color.valueOf("#11B6E4");
    public static Color OUTER_NOTE_COLOR = Color.valueOf("#B885FF");

    private GameConfig() {} // Prevent instantiation
}

