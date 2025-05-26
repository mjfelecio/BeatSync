package com.mjfelecio.beatsync.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class GameEngine {
    private final AudioManager audioManager;
    private final GameClock gameClock;

    public GameEngine() {
        this.audioManager = new AudioManager();
        this.gameClock = new GameClock();
    }

    public void loadAudioAndStartClock() {
        // I'm using File.toURI() so that it parses files with spaces properly
        // TODO: Have a class dedicated to loading all of the beatmaps files, including audio
        String filePath = new File("src/main/resources/com/mjfelecio/beatsync/beatmaps/1301440 TrySail - Utsuroi (Short Ver.) (another copy).osz_FILES/audio.mp3").toURI().toString();
        this.audioManager.setMusic(new Media(filePath));
        // TODO: Have a dedicated class that handles starting the playing
        MediaPlayer player = this.audioManager.getPlayer();
        player.play();
        gameClock.start(player);
    }

    public GameClock getGameClock() {
        return gameClock;
    }
}
