package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.gameplay.GameSession;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.Score;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.ui.*;
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

    // Keep track of the active GameplayManager for cleanup
    private GameplayManager currentGameplayManager;

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
        if (GameState.getInstance().getCurrentBeatmap() == null) return;

        // Dispose of any previous gameplay to stop timers and media callbacks
        if (currentGameplayManager != null) {
            currentGameplayManager.dispose();
        }

        SFXPlayer.getInstance().play(SoundEffect.ENTER_GAMEPLAY);
        GameplayManager gameplayManager = new GameplayManager();
        currentGameplayManager = gameplayManager;

        GameState.getInstance().getCurrentBeatmap().resetNotesState();

        gameplayManager.loadBeatmap(GameState.getInstance().getCurrentBeatmap());
        gameplayManager.initializeGameplay();
        gameplayManager.startGameplay();

        applyScene(gameplayManager.getGameplayScene());
    }

    public void loadGameplay(Beatmap beatmap) {
        // Dispose of any previous gameplay to stop timers and media callbacks
        if (currentGameplayManager != null) {
            currentGameplayManager.dispose();
        }

        SFXPlayer.getInstance().play(SoundEffect.ENTER_GAMEPLAY);
        GameplayManager gameplayManager = new GameplayManager();
        currentGameplayManager = gameplayManager;

        beatmap.resetNotesState();

        gameplayManager.loadBeatmap(beatmap);
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

    // Temporary workaround to reuse this UI to display the score
    public void loadFullScoreDetails(Score score, Beatmap beatmap) {
        PlayResultUI playResultUI = new PlayResultUI();
        playResultUI.initializeValues(score, beatmap);

        applyScene(playResultUI.getScene());
    }

    public void loadScoreDashboard(Beatmap selectedBeatmap) {
        Scene scoreDashboardScene = new ScoreDashboard(selectedBeatmap).getScene();
        applyScene(scoreDashboardScene);
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
