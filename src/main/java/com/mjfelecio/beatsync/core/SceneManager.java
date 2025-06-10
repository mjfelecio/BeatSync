package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.gameplay.GameSession;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.ui.PlayResultUI;
import com.mjfelecio.beatsync.ui.SettingsUI;
import com.mjfelecio.beatsync.ui.SongSelectUI;
import com.mjfelecio.beatsync.ui.TitleScreenUI;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private static SceneManager instance;
    private final Stage primaryStage; // This contains the original stage (window) for the game

    private Scene currentScene;

    // Cached scenes for reuse
    private TitleScreenUI cachedTitleScreenUI;
    private SongSelectUI cachedSongSelectUI;
    private SettingsUI cachedSettingsUI;

    private SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public static void initialize(Stage primaryStage) {
        if (instance == null) {
            instance = new SceneManager(primaryStage);
        }
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SceneManager must be initialized first!");
        }
        return instance;
    }

    public void loadTitleScreen() {
        if (cachedTitleScreenUI == null) {
            cachedTitleScreenUI = new TitleScreenUI();
        }
        applyScene(cachedTitleScreenUI.getScene());
    }

    public void loadSongSelect() {
        if (cachedSongSelectUI == null) {
            cachedSongSelectUI = new SongSelectUI();
        }
        applyScene(cachedSongSelectUI.getScene());
    }

    public void loadSettings() {
        if (cachedSettingsUI == null) {
            cachedSettingsUI = new SettingsUI();
        }
        applyScene(cachedSettingsUI.getScene());
    }

    public void loadGameplay() {
        SFXPlayer.getInstance().play(SoundEffect.ENTER_GAMEPLAY);
        GameplayManager gameplayManager = GameplayManager.getInstance();

        // Resets the notes state so that it can still get rendered on retry
        // You have no idea how long I spent to fix this bug. 4 fking hours and it was just this simple thing.
        // I almost rewrote my entire gameplay logic to find what was going on
        // I hate my liiiiife
        GameState.getInstance().getCurrentBeatmap().resetNotesState();

        gameplayManager.loadBeatmap(GameState.getInstance().getCurrentBeatmap());
        gameplayManager.initializeGameplay();
        gameplayManager.startGameplay();

        applyScene(gameplayManager.getGameplayScene());
    }

    public void loadResultScreen(GameSession gameSession) {
        PlayResultUI playResultUI = new PlayResultUI();
        playResultUI.initializeValues(gameSession);

        applyScene(playResultUI.getScene());
        SFXPlayer.getInstance().play(SoundEffect.RESULTS_SWOOSH);
    }

    private void applyScene(Scene newScene) {
        if (newScene == null) {
            System.err.println("Attempted to set null scene. Keeping current scene.");
            return;
        }

        this.currentScene = newScene;

        // Update the window with the new scene
        if (primaryStage != null) {
            primaryStage.setScene(newScene);
            SFXPlayer.getInstance().play(SoundEffect.SCENE_CHANGE);
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }
}
