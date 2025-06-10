package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.config.SettingsManager;
import com.mjfelecio.beatsync.gameplay.GameSession;
import com.mjfelecio.beatsync.gameplay.GameplayLogic;
import com.mjfelecio.beatsync.input.InputHandler;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.rendering.PlayfieldRenderer;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.ui.GameplayUI;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

public class GameplayManager {
    // UI Components
    private GraphicsContext gc;
    private Scene gameplayScene;
    private GameplayUI gameplayUI;

    // Game Loop
    private AnimationTimer gameLoop;
    private boolean isPaused = false;

    // Game Components
    private final GameEngine gameEngine;
    private final PlayfieldRenderer renderer;
    private final GameplayLogic gameplayLogic;
    private InputHandler inputHandler;

    // Audio Lead-in related properties
    long gameStartTime = -1;
    long songStartTime = -1;
    private boolean songStarted = false;

    public GameplayManager() {
        this.gameEngine = GameEngine.getInstance();
        this.renderer = new PlayfieldRenderer();
        this.gameplayLogic = new GameplayLogic(gameEngine.getGameSession());
        inputHandler = new InputHandler(gameplayLogic);
        gameplayUI = new GameplayUI();
        gameplayScene = gameplayUI.getGamePlayScene(); // Get the actual scene
        gc = gameplayUI.getGameplayCanvas().getGraphicsContext2D(); // Get the Graphics context for the program to use
    }

    public void initializeGameplay() {
        setUpSettings();
        setUpInputHandling();

        // Once the music (aka the map) has ended, we navigate to the play result with the gameSession data
        gameEngine.getMusicPlayer().getPlayer().setOnEndOfMedia(() -> {
            navigateToPlayResult(gameEngine.getGameSession());
        });

        // Setup game loop
        gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (!isPaused) {
                    long deltaTime = now - lastTime;
                    lastTime = now;

                    update(deltaTime);
                    render();
                }
            }
        };
    }

    public void loadBeatmap(Beatmap beatmap) {
        try {
            gameplayLogic.loadBeatmap(beatmap);
            gameEngine.getMusicPlayer().loadMusic(beatmap.getAudioPath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load beatmap", e);
        }
    }

    public Scene getGameplayScene() {
        if (gameplayScene == null) {
            initializeGameplay();
        }
        return gameplayScene;
    }

    public void startGameplay() {
        gameStartTime = System.currentTimeMillis();
        songStartTime = gameStartTime + GameConfig.AUDIO_LEAD_IN;

        isPaused = false;
        GameState.getInstance().setPlaying(true);

        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public long getCurrentSongTime() {
        double currentTime = System.currentTimeMillis();

        if (currentTime > songStartTime) {
            // Normal playback - start audio if not started
            if (!songStarted) {
                gameEngine.getMusicPlayer().play();
                gameEngine.getGameClock().start();
                songStarted = true;
            }
        }
        return (long) currentTime - songStartTime;
    }

    public void restartGameplay() {
        stopGameplay();
        SceneManager.getInstance().loadGameplay();
    }

    public void pauseGameplay() {
        isPaused = true;
        gameEngine.getMusicPlayer().pause();
        GameState.getInstance().setPlaying(false);
        gameplayUI.setPaused(true);
    }

    public void resumeGameplay() {
        gameplayUI.setPaused(false);
        isPaused = false;
        gameEngine.getMusicPlayer().resume();
        GameState.getInstance().setPlaying(true);
    }

    public void stopGameplay() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameEngine.getMusicPlayer().stop();
        GameState.getInstance().setPlaying(false);
    }

    private void update(long deltaTime) {
        if (!GameState.getInstance().isPlaying()) return;

        long currentAudioTime = getCurrentSongTime();

        gameEngine.getGameClock().syncToAudioTime(currentAudioTime);
        gameplayLogic.update(currentAudioTime, deltaTime);

        updateUIInfo();
    }

    private void render() {
        renderer.render(gc, gameEngine.getGameSession(), gameplayLogic.getVisibleNotes(),
                inputHandler.getCurrentInput());
    }

    private void updateUIInfo() {
        GameSession gameSession = gameEngine.getGameSession();
        gameplayUI.setScore(gameSession.getScore());
        gameplayUI.setAccuracy(gameSession.getAccuracy());
        gameplayUI.setCombo(gameSession.getCombo());
    }

    private void setUpInputHandling() {
        gameplayScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                if (isPaused) resumeGameplay();
                else pauseGameplay();
            } else if (event.getCode() == KeyCode.BACK_QUOTE) {
                restartGameplay();
            }

            inputHandler.handleKeyPress(event.getCode(), getCurrentSongTime());
        });
        gameplayScene.setOnKeyReleased(event ->
                inputHandler.handleKeyRelease(event.getCode(), getCurrentSongTime()));
    }

    private void setUpSettings() {
        gameEngine.getMusicPlayer().setVolume(SettingsManager.getInstance().getMusicVolume());
        SFXPlayer.getInstance().setVolume(SettingsManager.getInstance().getEffectsVolume()); // IDK why but this doesn't work
    }

    private void navigateToPlayResult(GameSession gameSession) {
        Timeline delayTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            SceneManager.getInstance().loadResultScreen(gameSession);
        }));
        delayTimeline.setCycleCount(1);
        delayTimeline.play();
    }
}
