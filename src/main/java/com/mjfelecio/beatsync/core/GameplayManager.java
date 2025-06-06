package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.gameplay.GameplayLogic;
import com.mjfelecio.beatsync.input.InputHandler;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.rendering.PlayfieldRenderer;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.views.GameplayScene;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

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
        this.gameplayLogic = new GameplayLogic(gameEngine.getGameSession());
    }

    public static GameplayManager getInstance() {
        if (instance == null) {
            instance = new GameplayManager();
        }
        return instance;
    }

    public void initializeGameplay() {
        // This is the ui, the view itself (I'm not using fxml because it's annoying)
        GameplayScene gameplayUI = new GameplayScene(); // Initialize the UI
        gameplayScene = gameplayUI.getGamePlayScene(); // Get the actual scene
        gc = gameplayUI.getGameplayCanvas().getGraphicsContext2D(); // Get the Graphics context for the program to use

        // Initialize input handler
        inputHandler = new InputHandler(gameplayLogic);

        // Setup input handling for this scene only
        gameplayScene.setOnKeyPressed(event -> {
            // Move this to another class maybe? But I don't care for now
            // Pauses or unpauses the game
            if (event.getCode() == KeyCode.ESCAPE) {
                if (isPaused) {
                    resumeGameplay();
                } else {
                    pauseGameplay();
                }
            }

            inputHandler.handleKeyPress(event.getCode(), gameEngine.getGameClock().getCurrentTime());
        });
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

    public void loadBeatmap(Beatmap beatmap) {
        try {
            gameplayLogic.loadBeatmap(beatmap);
            gameEngine.getAudioManager().loadMusic(beatmap.getAudioPath());
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
        GameState.getInstance().setPlaying(true);

        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public void pauseGameplay() {
        isPaused = true;
        gameEngine.getAudioManager().pause();
        GameState.getInstance().setPlaying(false);
    }

    public void resumeGameplay() {
        isPaused = false;
        gameEngine.getAudioManager().resume();
        GameState.getInstance().setPlaying(true);
    }

    public void stopGameplay() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameEngine.getAudioManager().stop();
        GameState.getInstance().setPlaying(false);

        // Clean up resources
        cleanupResources();
    }

    private void update(long deltaTime) {
        if (!GameState.getInstance().isPlaying()) return;

        long currentAudioTime = gameEngine.getAudioManager().getCurrentTime();
        gameEngine.getGameClock().syncToAudioTime(currentAudioTime);
        gameplayLogic.update(currentAudioTime, deltaTime);
    }

    private void render() {
        renderer.render(gc, gameEngine.getGameSession(), gameplayLogic.getVisibleNotes(),
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
