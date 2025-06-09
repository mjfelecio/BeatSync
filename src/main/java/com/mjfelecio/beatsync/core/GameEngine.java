package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.audio.MusicPlayer;
import com.mjfelecio.beatsync.gameplay.GameSession;

public class GameEngine {
    private static GameEngine instance;

    private final GameSession gameSession;
    private final GameClock gameClock;
    private final MusicPlayer musicPlayer;

    private GameEngine() {
        this.gameSession = new GameSession();
        this.gameClock = new GameClock();
        this.musicPlayer = new MusicPlayer();
    }

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    // Provide services to managers
    public GameClock getGameClock() { return gameClock; }
    public MusicPlayer getMusicPlayer() { return musicPlayer; }
    public GameSession getGameSession() { return gameSession; }
}
