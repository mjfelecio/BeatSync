package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.audio.MusicPlayer;
import com.mjfelecio.beatsync.gameplay.GameSession;

public class GameEngine {
    private final GameSession gameSession;
    private final GameClock gameClock;
    private final MusicPlayer musicPlayer;

    public GameEngine() {
        this.gameSession = new GameSession();
        this.gameClock = new GameClock();
        this.musicPlayer = new MusicPlayer();
    }

    // Provide services to managers
    public GameClock getGameClock() { return gameClock; }
    public MusicPlayer getMusicPlayer() { return musicPlayer; }
    public GameSession getGameSession() { return gameSession; }
}
