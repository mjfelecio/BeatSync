package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.state.GameState;

public class GameEngine {
    private static GameEngine instance;

    private final GameState gameState;
    private final GameClock gameClock;
    private final AudioManager audioManager;

    private GameEngine() {
        this.gameState = GameState.getInstance();
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
    public GameState getGameState() { return gameState; }
}
