package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.gameplay.GameSession;
import com.mjfelecio.beatsync.judgement.JudgementResult;
import com.mjfelecio.beatsync.judgement.Rank;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.EnumMap;
import java.util.Map;

public class PlayResultUI {
    private Scene scene;

    // Gameplay values (defaults for testing)
    private Rank rank = Rank.S;
    private long score = 0;
    private double accuracy = 0;
    private int maxCombo = 0;
    private final Map<JudgementResult, Integer> judgementCounts;

    public PlayResultUI() {
        // initialize judgementCounts with default values
        judgementCounts = new EnumMap<>(JudgementResult.class);
        for (JudgementResult result : JudgementResult.values()) {
            judgementCounts.put(result, 0);
        }
    }

    public void initializeValues(GameSession gameSession) {
        rank = gameSession.getRank();
        score = gameSession.getScore();
        accuracy = gameSession.getAccuracy();
        maxCombo = gameSession.getMaxCombo();
        judgementCounts.put(JudgementResult.PERFECT, gameSession.getPerfectCount());
        judgementCounts.put(JudgementResult.GREAT, gameSession.getGreatCount());
        judgementCounts.put(JudgementResult.MEH, gameSession.getMehCount());
        judgementCounts.put(JudgementResult.MISS, gameSession.getMissCount());

        // Create the scene once the values has been filled in
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(40);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setBackground(ImageProvider.PLAY_RESULT_BG.getImageAsBackground());

        // Title
        Text title = new Text("Play Result");
        title.setFont(FontProvider.ARCADE_R.getFont(60));
        title.setStyle("-fx-fill: #0FF;");

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
        card.setPadding(new Insets(20));
        card.setSpacing(30);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
            """
               -fx-background-color: linear-gradient(to bottom, #111111, #1A1A1A);
               -fx-border-color: #0FF;
               -fx-border-width: 2;
               -fx-border-radius: 12;
               -fx-background-radius: 12;
               -fx-effect: dropshadow(gaussian, #0FF, 15, 0.2, 0, 0);
            """
        );

        ImageView rankImage = new ImageView(getRankImage(rank));
        // The rank image now displays a different border color depending on the
        // rank being displayed! Yahooo
        String borderColor = getRankColor(rank);
        rankImage.setStyle(String.format(
                "-fx-effect: dropshadow(gaussian, %s, 30, 0.3, 0, 0);", borderColor
        ));
        VBox.setVgrow(rankImage, Priority.ALWAYS);
        VBox statsBox = createStatsBox();

        card.getChildren().addAll(rankImage, statsBox);
        return card;
    }

    public VBox createStatsBox() {
        VBox statsBox = new VBox(20);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(40, 0, 0, 0));

        HBox scoreBox = createScoreBox();
        HBox accAndComboBox = createAccAndComboBox();

        statsBox.getChildren().addAll(scoreBox, accAndComboBox);
        return statsBox;
    }

    private HBox createScoreBox() {
        HBox scoreBox = new HBox();
        scoreBox.setAlignment(Pos.CENTER);
        scoreBox.setStyle(
                """
                -fx-background-color: #00FFAA33;
                -fx-background-radius: 8;
                -fx-padding: 10 20;
                """
        );

        Label scoreLabel = new Label(String.format("Score: %d", score)); // Scoooore
        scoreLabel.setFont(FontProvider.ARCADE_R.getFont(22));
        scoreLabel.setStyle("-fx-text-fill: #0FF;");

        scoreBox.getChildren().addAll(scoreLabel);

        return scoreBox;
    }

    private HBox createAccAndComboBox() {
        HBox accAndComboBox = new HBox(10);

        // UI elements
        Label accuracyLabel = new Label(String.format("Acc: %.2f%%", accuracy)); // Format the accuracy before displaying it
        accuracyLabel.setFont(FontProvider.ARCADE_R.getFont(12));
        accuracyLabel.setStyle("-fx-text-fill: #CCCCFF;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label maxComboLabel = new Label(String.format("Max Combo: %d", maxCombo)); // Append the max combo
        maxComboLabel.setFont(FontProvider.ARCADE_R.getFont(12));
        maxComboLabel.setStyle("-fx-text-fill: #CCCCFF;");

        accAndComboBox.getChildren().addAll(accuracyLabel, spacer, maxComboLabel);
        return accAndComboBox;
    }

    private VBox createJudgementsBox() {
        VBox judgementsBox = new VBox(12);
        judgementsBox.setAlignment(Pos.TOP_CENTER);
        judgementsBox.setPadding(new Insets(20));

        String[][] judgementTypes = {
                {"Perfect", "#0FF"},
                {"Great", "#66CCFF"},
                {"Meh", "#FFDD55"},
                {"Miss", "#FF5555"}
        };

        for (String[] judgementData : judgementTypes) {
            String labelText = judgementData[0];
            String color = judgementData[1];

            HBox judgementRow = new HBox(20);
            judgementRow.setAlignment(Pos.CENTER_LEFT);
            judgementRow.setStyle(
                    """
                    -fx-background-color: transparent;
                    -fx-padding: 8 16;
                    -fx-background-radius: 8;
                    """
            );

            Text label = new Text(labelText);
            label.setFont(FontProvider.ARCADE_R.getFont(24));
            label.setStyle(String.format("-fx-fill: %s;", color));

            // Gets the values on the judgementCounts EnumMap
            int countValue = switch (judgementData[0]) {
                case "Perfect" -> judgementCounts.get(JudgementResult.PERFECT);
                case "Great" -> judgementCounts.get(JudgementResult.GREAT);
                case "Meh" -> judgementCounts.get(JudgementResult.MEH);
                case "Miss" -> judgementCounts.get(JudgementResult.MISS);
                default -> throw new IllegalStateException("Unexpected value: " + judgementData[0]);
            };

            Text count = new Text(String.valueOf(countValue));
            count.setFont(FontProvider.ARCADE_R.getFont(24));
            count.setStyle(String.format("-fx-fill: %s;", color));

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
        retryButton.setOnAction(e -> retryBeatmap());

        Button songSelectButton = new Button("Song Select");
        songSelectButton.setFont(FontProvider.ARCADE_R.getFont(24));
        songSelectButton.setStyle(buttonStyle());
        songSelectButton.setOnAction(e -> navigateToSongSelect());

        buttonGroup.getChildren().addAll(retryButton, songSelectButton);
        return buttonGroup;
    }

    private Image getRankImage(Rank rank) {
        Image image = null;
        final int RANK_WIDTH = 250;
        final int RANK_HEIGHT = 0; // We can just set this to 0 since we want to preserve the ration

        switch (rank) {
            case SS -> image = ImageProvider.SS_RANK.getImage(RANK_WIDTH, RANK_HEIGHT, true, true);
            case S -> image = ImageProvider.S_RANK.getImage(RANK_WIDTH, RANK_HEIGHT, true, true);
            case A -> image = ImageProvider.A_RANK.getImage(RANK_WIDTH, RANK_HEIGHT, true, true);
            case B -> image = ImageProvider.B_RANK.getImage(RANK_WIDTH, RANK_HEIGHT, true, true);
            case C -> image = ImageProvider.C_RANK.getImage(RANK_WIDTH, RANK_HEIGHT, true, true);
            case D -> image = ImageProvider.D_RANK.getImage(RANK_WIDTH, RANK_HEIGHT, true, true);
        }

        return image;
    }

    private String getRankColor(Rank rank) {
        return switch (rank) {
            case SS -> "#FFD700";  // Gold
            case S  -> "#C0C0C0";  // Silver
            case A  -> "#00CCFF";  // Aqua Blue
            case B  -> "#AA88FF";  // Soft Purple
            case C  -> "#FF00FF";  // Magenta
            case D  -> "#FF4444";  // Red
        };
    }

    private String buttonStyle() {
        return """
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """;
    }

    private void retryBeatmap() {
        SFXPlayer.getInstance().play(SoundEffect.SELECT);
        SceneManager.getInstance().loadGameplay();
    }

    private void navigateToSongSelect() {
        SFXPlayer.getInstance().play(SoundEffect.SELECT);
        SceneManager.getInstance().loadSongSelect();
    }

    public Scene getScene() {
        createScene();
        return scene;
    }
}
