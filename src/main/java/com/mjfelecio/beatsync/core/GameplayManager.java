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

    public GameplayManager() {
        this.gameEngine = new GameEngine();
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
        isPaused = false;
        GameState.getInstance().setPlaying(true);

        gameEngine.getGameClock().startGame();

        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void restartGameplay() {
        stopGameplay();
        SceneManager.getInstance().loadGameplay();
    }

    public void pauseGameplay() {
        isPaused = true;
        gameEngine.getMusicPlayer().pause();
        gameEngine.getGameClock().pause();
        GameState.getInstance().setPlaying(false);
        gameplayUI.setPaused(true);
    }

    public void resumeGameplay() {
        gameplayUI.setPaused(false);
        isPaused = false;
        gameEngine.getMusicPlayer().resume();
        gameEngine.getGameClock().resume();
        GameState.getInstance().setPlaying(true);
    }

    public void stopGameplay() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameEngine.getMusicPlayer().stop();
        gameEngine.getGameClock().stop();
        GameState.getInstance().setPlaying(false);
    }

    public void quitGameplay() {
        stopGameplay();
        SceneManager.getInstance().loadSongSelect();
    }

    private void update(long deltaTime) {
        if (!GameState.getInstance().isPlaying()) return;

        gameEngine.getGameClock().update();

        // Check if we should start audio
        if (gameEngine.getGameClock().shouldStartAudio()) {
            gameEngine.getMusicPlayer().play();
            gameEngine.getGameClock().markAudioStarted();
        }

        long currentSongTime = gameEngine.getGameClock().getCurrentSongTime();

        gameplayLogic.update(currentSongTime, deltaTime);

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
            switch (event.getCode()) {
                case P -> {
                    if (isPaused) resumeGameplay();
                    else pauseGameplay();
                }
                case ESCAPE -> quitGameplay();
                case BACK_QUOTE -> restartGameplay();
            }
            inputHandler.handleKeyPress(event.getCode(), gameEngine.getGameClock().getCurrentSongTime());
        });
        gameplayScene.setOnKeyReleased(event ->
                inputHandler.handleKeyRelease(event.getCode(), gameEngine.getGameClock().getCurrentSongTime()));
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
