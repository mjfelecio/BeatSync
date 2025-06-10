package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.config.SettingsManager;
import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.judgement.JudgementMode;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SettingsUI {
    private Scene scene;

    private final IntegerProperty scrollSpeed = new SimpleIntegerProperty(1000);
    private final IntegerProperty musicVolume = new SimpleIntegerProperty(100);
    private final IntegerProperty effectsVolume = new SimpleIntegerProperty(100);
    private final ObjectProperty<JudgementMode> selectedJudgement = new SimpleObjectProperty<>(JudgementMode.NORMAL);

    public SettingsUI() {
        setupUI();
    }

    private void setupUI() {
        VBox root = new VBox();
        root.setBackground(ImageProvider.SETTINGS_BG.getImageAsBackground());
        root.setPadding(new Insets(40));
        root.setSpacing(30);
        root.setAlignment(Pos.TOP_CENTER);

        // Title
        Label title = new Label("SETTINGS");
        title.setPadding(new Insets(50, 0, 0, 0));
        title.setFont(FontProvider.ARCADE_R.getFont(48));
        title.setTextFill(Color.web("#0FF"));

        VBox settingsContent = new VBox(30);
        settingsContent.setAlignment(Pos.TOP_CENTER);
        settingsContent.setPrefSize(1000, 800);
        settingsContent.setMaxWidth(Region.USE_PREF_SIZE);
        settingsContent.setMinWidth(Region.USE_PREF_SIZE);

        HBox navigationButtons = createNavigationButtons();

        settingsContent.getChildren().addAll(
                createGameplaySettings(),
                createAudioSettings()
        );

        root.getChildren().addAll(title, settingsContent, navigationButtons);
        StackPane rootWrapper = new StackPane(root);
        scene = new Scene(rootWrapper);
    }

    private VBox createGameplaySettings() {
        VBox gameplayBox = new VBox(15);
        gameplayBox.setAlignment(Pos.TOP_LEFT);

        Label sectionLabel = new Label("Gameplay");
        sectionLabel.setFont(FontProvider.ARCADE_R.getFont(24));
        sectionLabel.setTextFill(Color.WHITE);

        // Scroll Speed
        Label scrollLabel = new Label("Scroll Speed");
        scrollLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        scrollLabel.setTextFill(Color.LIGHTGRAY);

        Slider scrollSlider = createThemedSlider(500, 2000, 1000);
        scrollSlider.setBlockIncrement(100);
        scrollSlider.setShowTickLabels(true);
        scrollSlider.setShowTickMarks(true);
        scrollSlider.setMajorTickUnit(500);
        scrollSlider.valueProperty().bindBidirectional(scrollSpeed);

        // Judgement Mode
        Label judgementLabel = new Label("Judgement Window");
        judgementLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        judgementLabel.setTextFill(Color.LIGHTGRAY);

        ToggleGroup judgementGroup = new ToggleGroup();

        RadioButton forgiving = createRadio("Forgiving", JudgementMode.FORGIVING, judgementGroup);
        RadioButton normal = createRadio("Normal", JudgementMode.NORMAL, judgementGroup);
        RadioButton precise = createRadio("Precise", JudgementMode.PRECISE, judgementGroup);

        normal.setSelected(true);

        HBox judgementButtons = new HBox(10, forgiving, normal, precise);
        judgementButtons.setAlignment(Pos.CENTER_LEFT);

        gameplayBox.getChildren().addAll(
                sectionLabel,
                scrollLabel, scrollSlider,
                judgementLabel, judgementButtons
        );

        return gameplayBox;
    }

    private VBox createAudioSettings() {
        VBox audioBox = new VBox(15);
        audioBox.setAlignment(Pos.TOP_LEFT);

        Label sectionLabel = new Label("Audio");
        sectionLabel.setFont(FontProvider.ARCADE_R.getFont(24));
        sectionLabel.setTextFill(Color.WHITE);

        // Music Volume
        Label musicLabel = new Label("Music");
        musicLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        musicLabel.setTextFill(Color.LIGHTGRAY);

        Slider musicSlider = createThemedSlider(0, 100, 100);
        musicSlider.setShowTickLabels(true);
        musicSlider.setShowTickMarks(true);
        musicSlider.setMajorTickUnit(25);
        musicSlider.valueProperty().bindBidirectional(musicVolume);

        // Effects Volume
        Label effectsLabel = new Label("Effects");
        effectsLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        effectsLabel.setTextFill(Color.LIGHTGRAY);

        Slider effectsSlider = createThemedSlider(0, 100, 100);
        effectsSlider.setShowTickLabels(true);
        effectsSlider.setShowTickMarks(true);
        effectsSlider.setMajorTickUnit(25);
        effectsSlider.valueProperty().bindBidirectional(effectsVolume);

        audioBox.getChildren().addAll(
                sectionLabel,
                musicLabel, musicSlider,
                effectsLabel, effectsSlider
        );

        return audioBox;
    }

    private RadioButton createRadio(String text, JudgementMode value, ToggleGroup group) {
        RadioButton button = new RadioButton(text);
        button.setFont(FontProvider.ARCADE_R.getFont(14));
        button.setTextFill(Color.WHITE);
        button.setToggleGroup(group);
        button.setOnAction(_ -> selectedJudgement.set(value));
        return button;
    }

    private Slider createThemedSlider(double min, double max, double initialValue) {
        Slider slider = new Slider(min, max, initialValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit((max - min) / 4);
        slider.setBlockIncrement(100);
        slider.setMaxWidth(500);
        slider.setStyle("""
        -fx-control-inner-background: #111;
        -fx-background-color: transparent;
        -fx-accent: #0ff;
        -fx-text-box-border: transparent;
        -fx-focus-color: #0ff;
        """);
        return slider;
    }

    private HBox createNavigationButtons() {
        Button backButton = new Button("Back");
        backButton.setFont(FontProvider.ARCADE_R.getFont(14));
        backButton.setStyle("""
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """);
        backButton.setOnAction(e -> navigateToTitleScreen());

        Button saveButton = new Button("Save");
        saveButton.setFont(FontProvider.ARCADE_R.getFont(14));
        saveButton.setStyle("""
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """);
        saveButton.setOnAction(e -> saveSettings());

        HBox buttonBox = new HBox(20, backButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(0, 0, 50, 0));
        return buttonBox;
    }

    public Scene getScene() {
        return scene;
    }

    private void navigateToTitleScreen() {
        SFXPlayer.getInstance().play(SoundEffect.SELECT);
        SceneManager.getInstance().loadTitleScreen();
    }

    private void saveSettings() {
        SettingsManager settingsManager = SettingsManager.getInstance();
        settingsManager.setScrollSpeed(scrollSpeed.intValue());
        settingsManager.setJudgementMode(selectedJudgement.getValue());
        settingsManager.setMusicVolume(musicVolume.intValue());
        settingsManager.setEffectsVolume(effectsVolume.intValue());
        SFXPlayer.getInstance().play(SoundEffect.SAVE_SETTINGS);
        NotificationManager.showNotification(scene, Notification.SUCCESS, "Settings saved!");
    }
}
