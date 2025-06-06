package com.mjfelecio.beatsync.views;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.utils.FontProvider;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;

import java.util.Objects;

public class GameplayUI {
    // Write-only properties
    private final IntegerProperty score = new SimpleIntegerProperty();
    private final IntegerProperty accuracy = new SimpleIntegerProperty();
    private final IntegerProperty combo = new SimpleIntegerProperty();

    // UI Config

    // UI Elements
    private Canvas gameplayCanvas;
    private final Label scoreValueLabel = new Label("0");
    private final Label accuracyValueLabel = new Label("100%");
    private final HBox root = new HBox();
    private final Image backgroundImage = new Image(
            Objects.requireNonNull(getClass().getResource("/com/mjfelecio/beatsync/images/gameplay_bg.jpg")).toExternalForm()
    );
    private final BackgroundImage bgImage = new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.DEFAULT,
            new BackgroundSize(
                    BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true
            )
    );

    public GameplayUI() {
        setupUI();
    }

    private void setupUI() {
        root.setPadding(new Insets(20, 0, 37, 0));
        root.setBackground(new Background(bgImage));
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

    public void setScore(int value) {
        score.set(value);
    }

    public void setAccuracy(int value) {
        accuracy.set(value);
    }

    public void setCombo(int value) {
        combo.set(value);
    }
}
