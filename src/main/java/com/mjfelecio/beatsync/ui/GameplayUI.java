package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

public class GameplayUI {
    // Write-only properties
    private final LongProperty score = new SimpleLongProperty();
    private final DoubleProperty accuracy = new SimpleDoubleProperty();
    private final IntegerProperty combo = new SimpleIntegerProperty();

    // UI Elements
    private Canvas gameplayCanvas;
    private final Label scoreValueLabel = new Label("0");
    private final Label accuracyValueLabel = new Label("100%");
    private final HBox root = new HBox();

    public GameplayUI() {
        setupUI();
    }

    private void setupUI() {
        root.setPadding(new Insets(20, 0, 29, 0));
        root.setBackground(ImageProvider.GAMEPLAY_BG.getImageAsBackground());
        root.setAlignment(Pos.BOTTOM_CENTER);
        root.setSpacing(20);

        // Score VBox (Left)
        VBox scoreBox = setupScoreBox();
        StackPane scoreWrapper = new StackPane(scoreBox);
        scoreWrapper.setPrefHeight(1080); // match scene height
        scoreWrapper.setAlignment(Pos.BOTTOM_CENTER);
        scoreWrapper.setPadding(new Insets(0, 0, 60, 0));

        // Canvas (Center)
        gameplayCanvas = new Canvas(GameConfig.PLAYFIELD_WIDTH, GameConfig.PLAYFIELD_HEIGHT);
        gameplayCanvas.setStyle(
                "-fx-border-color: #27fa00; " +
                "-fx-border-width: 2; "
        );

        GraphicsContext gc = gameplayCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gameplayCanvas.getWidth(), gameplayCanvas.getHeight());

        // Wrap in a StackPane
        StackPane canvasWrapper = new StackPane(gameplayCanvas);
        canvasWrapper.setBorder(new Border(new BorderStroke(
                Color.web("#27fa00"),
                BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY,
                new BorderWidths(2)
        )));

        // Accuracy VBox (Right)
        VBox accuracyBox = setupAccuracyBox();
        StackPane accuracyWrapper = new StackPane(accuracyBox);
        accuracyWrapper.setPrefHeight(1080); // match scene height
        accuracyWrapper.setAlignment(Pos.BOTTOM_CENTER);
        accuracyWrapper.setPadding(new Insets(0, 0, 60, 0));

        // Assemble HBox
        root.getChildren().addAll(scoreWrapper, canvasWrapper, accuracyWrapper);

        // Bind values to UI (write-only via binding)
        score.addListener((obs, oldVal, newVal) -> scoreValueLabel.setText(String.valueOf(newVal)));
        accuracy.addListener((obs, oldVal, newVal) -> accuracyValueLabel.setText(newVal + "%"));
    }

    private VBox setupScoreBox() {
        VBox scoreBox = new VBox();

        Label scoreLabel = new Label("Score");

        scoreLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        scoreValueLabel.setFont(FontProvider.ARCADE_R.getFont(36));
        scoreLabel.setTextFill(Color.WHITE);
        scoreValueLabel.setTextFill(Color.WHITE);

        scoreBox.setSpacing(5);
        scoreBox.setPadding(new Insets(10, 0, 0, 0)); // padding at the top
        scoreBox.setAlignment(Pos.TOP_CENTER);

        scoreBox.setPrefSize(250, 100);
        scoreBox.setMinSize(250, 100);
        scoreBox.setMaxSize(250, 100);

        scoreBox.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: #CCCCCC; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; "
        );

        scoreBox.getChildren().addAll(scoreLabel, scoreValueLabel);
        return scoreBox;
    }

    private VBox setupAccuracyBox() {
        VBox accuracyBox = new VBox();

        Label accuracyLabel = new Label("Accuracy");

        accuracyLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        accuracyValueLabel.setFont(FontProvider.ARCADE_R.getFont(36));
        accuracyLabel.setTextFill(Color.WHITE);
        accuracyValueLabel.setTextFill(Color.WHITE);

        accuracyBox.setSpacing(5);
        accuracyBox.setPadding(new Insets(10, 0, 0, 0)); // padding at the top
        accuracyBox.setAlignment(Pos.TOP_CENTER);

        accuracyBox.setPrefSize(250, 100);
        accuracyBox.setMinSize(250, 100);
        accuracyBox.setMaxSize(250, 100);

        accuracyBox.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: #CCCCCC; " +
                "-fx-border-width: 3; " +
                "-fx-border-radius: 8; " +
                "-fx-background-radius: 8; " +
                "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        accuracyBox.getChildren().addAll(accuracyLabel, accuracyValueLabel);
        return accuracyBox;
    }

    public Scene getGamePlayScene() {
        return new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }

    public Canvas getGameplayCanvas() {
        return gameplayCanvas;
    }

    public void setScore(long value) {
        score.set(value);
    }

    public void setAccuracy(double value) {
        accuracy.set(value);
    }

    public void setCombo(int value) {
        combo.set(value);
    }

    public void resetValues() {
        setScore(0);
        setAccuracy(100);
        setCombo(0);
    }
}
