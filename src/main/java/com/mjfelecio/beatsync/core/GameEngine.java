package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.gameplay.GameplayLogic;
import com.mjfelecio.beatsync.input.InputHandler;
import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.rendering.PlayfieldRenderer;
import javafx.scene.canvas.GraphicsContext;

import java.io.File;

public class GameEngine {
    private final GameState gameState;
    private final GameClock gameClock;
    private final AudioManager audioManager;
    private final PlayfieldRenderer renderer;
    private final GameplayLogic gameplayLogic;
    private final InputHandler inputHandler;

    public GameEngine() {
        this.gameState = new GameState();
        this.gameClock = new GameClock();
        this.audioManager = new AudioManager();
        this.renderer = new PlayfieldRenderer();
        this.gameplayLogic = new GameplayLogic(gameState);
        this.inputHandler = new InputHandler(gameplayLogic);
    }

    public void initialize(String beatmapOszFolderPath) {
        try {
            // Load beatmap and audio
            Beatmap beatmap = ManiaBeatmapParser.parse(new File("/home/kirbysmashyeet/IdeaProjects/School/BeatSync/src/main/resources/com/mjfelecio/beatsync/beatmaps/1301440 TrySail - Utsuroi (Short Ver.) (another copy).osz_FILES/TrySail - Utsuroi (Short Ver.) (Scotty) [Easy].osu"));
            gameplayLogic.loadBeatmap(beatmap);

            String audio = new File("/home/kirbysmashyeet/IdeaProjects/School/BeatSync/src/main/resources/com/mjfelecio/beatsync/beatmaps/1301440 TrySail - Utsuroi (Short Ver.) (another copy).osz_FILES/audio.mp3").toURI().toString();

            audioManager.loadMusic(audio);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize game", e);
        }
    }

    public GameClock getGameClock() {
        return gameClock;
    }

    public void start() {
        audioManager.play();
        gameClock.start();
        gameState.setPlaying(true);
    }

    public void update(long deltaTime) {
        if (!gameState.isPlaying()) return;

        long currentAudioTime = audioManager.getCurrentTime();
        gameClock.syncToAudioTime(currentAudioTime);
        gameplayLogic.update(currentAudioTime, deltaTime);
    }

    public void render(GraphicsContext gc) {
        renderer.render(gc, gameState, gameplayLogic.getVisibleNotes(),
                inputHandler.getCurrentInput());
    }

    public InputHandler getInputHandler() { return inputHandler; }
    public GameState getGameState() { return gameState; }
    public long getCurrentTime() { return gameClock.getCurrentTime(); }
}
