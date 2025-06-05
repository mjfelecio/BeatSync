package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.audio.AudioManager;
import com.mjfelecio.beatsync.gameplay.GameSession;

public class GameEngine {
    private static GameEngine instance;

    private final GameSession gameSession;
    private final GameClock gameClock;
    private final AudioManager audioManager;

    private GameEngine() {
        this.gameSession = new GameSession();
        this.gameClock = new GameClock();
        this.audioManager = new AudioManager();
    }

    public static GameEngine getInstance() {
        if (instance == null) {
            instance = new GameEngine();
        }
        return instance;
    }

    // Provide services to managers
    public GameClock getGameClock() { return gameClock; }
    public AudioManager getAudioManager() { return audioManager; }
    public GameSession getGameSession() { return gameSession; }
}
