package com.mjfelecio.beatsync.views;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class PlayResultUI {
    private Label rankLabel;
    private Label accuracyLabel;
    private Label scoreLabel;
    private Label maxComboLabel;

    private final Scene scene;

    public PlayResultUI() {
        VBox root = new VBox(40);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setBackground(ImageProvider.PLAY_RESULT_BG.getImageAsBackground());

        // Title
        Text title = new Text("Play Result");
        title.setFont(FontProvider.ARCADE_R.getFont(64));
        title.setStyle("-fx-fill: #00FFAA;");

        // Main content HBox
        HBox contentBox = new HBox(40);
        contentBox.setAlignment(Pos.CENTER);

        // Left: Card Pane
        VBox cardPane = createCardPane();

        // Right: Judgements and Buttons
        VBox rightPane = new VBox(30);
        rightPane.setAlignment(Pos.TOP_CENTER);
        VBox judgementsBox = createJudgementsBox();
        HBox buttonBox = createButtonGroup();
        rightPane.getChildren().addAll(judgementsBox, buttonBox);

        VBox.setVgrow(judgementsBox, Priority.ALWAYS);

        contentBox.getChildren().addAll(cardPane, rightPane);
        root.getChildren().addAll(title, contentBox);

        scene = new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
    }

    private VBox createCardPane() {
        VBox card = new VBox();
        card.setPrefSize(400, 500);
        card.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setPadding(new Insets(15));
        card.setSpacing(20);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
            """
            -fx-background-color: white;
            -fx-border-color: #CCCCCC;
            -fx-border-width: 1;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(two-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);
            """
        );

        rankLabel = new Label("S");
        rankLabel.setFont(FontProvider.ARCADE_R.getFont(120));
        rankLabel.setAlignment(Pos.CENTER);
        rankLabel.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(rankLabel, Priority.ALWAYS);

        HBox statsBox = createStatsBox();

        card.getChildren().addAll(rankLabel, statsBox);

        return card;
    }

    public HBox createStatsBox() {
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

        return statsBox;
    }

    private VBox createJudgementsBox() {
        VBox judgementsBox = new VBox(40);
        judgementsBox.setAlignment(Pos.TOP_CENTER);

        // TODO: Add Great and Meh in JudgementResult and JudgementWindow enum
        String[] judgementTypes = { "Perfect", "Great", "Meh", "Miss" };
        for (String judgement : judgementTypes) {
            HBox judgementRow = new HBox(20);
            judgementRow.setAlignment(Pos.CENTER_LEFT);

            Text label = new Text(judgement);
            label.setFont(FontProvider.ARCADE_R.getFont(24));
            label.setStyle("-fx-fill: white;");

            Text count = new Text("0");
            count.setFont(FontProvider.ARCADE_R.getFont(24));
            count.setStyle("-fx-fill: white;");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            judgementRow.getChildren().addAll(label, spacer, count);
            judgementsBox.getChildren().add(judgementRow);
        }
        return judgementsBox;
    }

    private HBox createButtonGroup() {
        HBox buttonGroup = new HBox(20);
        buttonGroup.setAlignment(Pos.CENTER);

        Button retryButton = new Button("Retry");
        retryButton.setFont(FontProvider.ARCADE_R.getFont(24));
        retryButton.setStyle(buttonStyle());

        Button songSelectButton = new Button("Song Select");
        songSelectButton.setFont(FontProvider.ARCADE_R.getFont(24));
        songSelectButton.setStyle(buttonStyle());

        buttonGroup.getChildren().addAll(retryButton, songSelectButton);
        return buttonGroup;
    }

    public Image getRankImage(Rank rank) {
        Image image = null;

        switch (rank) {
            case SS -> image = ImageProvider.SS_RANK.getImage();
            case S -> image = ImageProvider.S_RANK.getImage();
            case A -> image = ImageProvider.A_RANK.getImage();
            case B -> image = ImageProvider.B_RANK.getImage();
            case C -> image = ImageProvider.C_RANK.getImage();
            case D -> image = ImageProvider.D_RANK.getImage();
        }

        return image;
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
