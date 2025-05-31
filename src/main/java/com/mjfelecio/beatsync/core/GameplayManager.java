package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.gameplay.GameplayLogic;
import com.mjfelecio.beatsync.input.InputHandler;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import com.mjfelecio.beatsync.rendering.PlayfieldRenderer;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;

import java.io.File;

public class GameplayManager {
    private static GameplayManager instance;

    // UI Components
    private Canvas gameCanvas;
    private GraphicsContext gc;
    private Scene gameplayScene;

    // Game Loop
    private AnimationTimer gameLoop;
    private boolean isPaused = false;

    // Game Components
    private final GameEngine gameEngine;
    private final PlayfieldRenderer renderer;
    private final GameplayLogic gameplayLogic;
    private InputHandler inputHandler;

    private GameplayManager() {
        this.gameEngine = GameEngine.getInstance();
        this.renderer = new PlayfieldRenderer();
        this.gameplayLogic = new GameplayLogic(gameEngine.getGameState());
    }

    public static GameplayManager getInstance() {
        if (instance == null) {
            instance = new GameplayManager();
        }
        return instance;
    }

    public void initializeGameplay() {
        // Create canvas and graphics context
        gameCanvas = new Canvas(GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
        gc = gameCanvas.getGraphicsContext2D();

        // Create scene
        gameplayScene = new Scene(new StackPane(gameCanvas),
                GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        // Initialize input handler
        inputHandler = new InputHandler(gameplayLogic);

        // Setup input handling for this scene only
        gameplayScene.setOnKeyPressed(event ->
                inputHandler.handleKeyPress(event.getCode(), gameEngine.getGameClock().getCurrentTime()));
        gameplayScene.setOnKeyReleased(event ->
                inputHandler.handleKeyRelease(event.getCode(), gameEngine.getGameClock().getCurrentTime()));

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

    public void loadBeatmap(String beatmapPath, String audioPath) {
        try {
            Beatmap beatmap = ManiaBeatmapParser.parse(new File(beatmapPath));
            gameplayLogic.loadBeatmap(beatmap);
            gameEngine.getAudioManager().loadMusic(audioPath);
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
        gameEngine.getAudioManager().play();
        gameEngine.getGameClock().start();
        gameEngine.getGameState().setPlaying(true);

        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void pauseGameplay() {
        isPaused = true;
        gameEngine.getAudioManager().pause();
        gameEngine.getGameState().setPlaying(false);
    }

    public void resumeGameplay() {
        isPaused = false;
        gameEngine.getAudioManager().resume();
        gameEngine.getGameState().setPlaying(true);
    }

    public void stopGameplay() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameEngine.getAudioManager().stop();
        gameEngine.getGameState().setPlaying(false);

        // Clean up resources
        cleanupResources();
    }

    private void update(long deltaTime) {
        if (!gameEngine.getGameState().isPlaying()) return;

        long currentAudioTime = gameEngine.getAudioManager().getCurrentTime();
        gameEngine.getGameClock().syncToAudioTime(currentAudioTime);
        gameplayLogic.update(currentAudioTime, deltaTime);
    }

    private void render() {
        renderer.render(gc, gameEngine.getGameState(), gameplayLogic.getVisibleNotes(),
                inputHandler.getCurrentInput());
    }

    private void cleanupResources() {
        // Clean up gameplay-specific resources
        gameCanvas = null;
        gc = null;
        gameplayScene = null;
        inputHandler = null;

        // Reset singleton for next gameplay session
        instance = null;
    }

    // Getters for compatibility with existing code
    public InputHandler getInputHandler() { return inputHandler; }
    public GameplayLogic getGameplayLogic() { return gameplayLogic; }

}
