package com.mjfelecio.beatsync.views;

import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class PlayResultUI {

    private static final double CARD_WIDTH = 300;
    private static final double CARD_HEIGHT = 250;

    private Label rankLabel;
    private Label accuracyLabel;
    private Label scoreLabel;
    private Label maxComboLabel;

    private final Scene scene;

    public PlayResultUI() {
        VBox root = new VBox();
        root.setPadding(new Insets(40));
        root.setSpacing(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setBackground(ImageProvider.PLAY_RESULT_BG.getImageAsBackground());

        // Title
        Text title = new Text("Play Result");
        title.setFont(FontProvider.ARCADE_R.getFont(64));
        title.setStyle("-fx-fill: #00FFAA;");

        // Main content HBox
        HBox contentBox = new HBox();
        contentBox.setAlignment(Pos.CENTER);

        // Left: Card Pane
        VBox cardPane = createCardPane();

        // Right: Judgements and Buttons
        VBox rightPane = new VBox(30);
        rightPane.setAlignment(Pos.CENTER);

        VBox judgementsBox = new VBox(10);
        judgementsBox.setAlignment(Pos.CENTER_LEFT);

        String[] judgementTypes = { "Perfect", "Great", "Good", "Miss" };
        for (String judgement : judgementTypes) {
            HBox judgementRow = new HBox(20);
            judgementRow.setAlignment(Pos.CENTER_LEFT);

            Text label = new Text(judgement);
            label.setFont(FontProvider.ARCADE_R.getFont(20));
            label.setStyle("-fx-fill: white;");

            Text count = new Text("0");
            count.setFont(FontProvider.ARCADE_R.getFont(20));
            count.setStyle("-fx-fill: white;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            judgementRow.getChildren().addAll(label, spacer, count);
            judgementsBox.getChildren().add(judgementRow);
        }

        // Buttons
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        Button retryButton = new Button("Retry");
        retryButton.setFont(FontProvider.ARCADE_R.getFont(24));
        retryButton.setStyle(buttonStyle());

        Button songSelectButton = new Button("Song Select");
        songSelectButton.setFont(FontProvider.ARCADE_R.getFont(24));
        songSelectButton.setStyle(buttonStyle());

        buttonBox.getChildren().addAll(retryButton, songSelectButton);

        rightPane.getChildren().addAll(judgementsBox, buttonBox);
        contentBox.getChildren().addAll(cardPane, rightPane);
        root.getChildren().addAll(title, contentBox);

        scene = new Scene(root, 1280, 720);
    }

    private VBox createCardPane() {
        VBox card = new VBox();
        card.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        card.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setAlignment(Pos.TOP_CENTER);

        card.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #CCCCCC; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8; " +
                        "-fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        rankLabel = new Label("S");
        rankLabel.setFont(FontProvider.ARCADE_R.getFont(96));
        rankLabel.setAlignment(Pos.CENTER);
        rankLabel.setMaxWidth(Double.MAX_VALUE);
        rankLabel.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(rankLabel, Priority.ALWAYS);

        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(10, 0, 0, 0));

        accuracyLabel = new Label("Acc: 0.00%");
        accuracyLabel.setFont(FontProvider.ARCADE_R.getFont(14));

        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(FontProvider.ARCADE_R.getFont(14));

        maxComboLabel = new Label("Max Combo: 0");
        maxComboLabel.setFont(FontProvider.ARCADE_R.getFont(14));

        statsBox.getChildren().addAll(accuracyLabel, scoreLabel, maxComboLabel);
        card.getChildren().addAll(rankLabel, statsBox);

        return card;
    }

    private String buttonStyle() {
        return """
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #00FFAA;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """;
    }

    public Scene getScene() {
        return scene;
    }
}
