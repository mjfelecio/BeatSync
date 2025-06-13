package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.ScoreDatabase;
import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.Rank;
import com.mjfelecio.beatsync.object.Score;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class ScoreDashboard {
    private Scene scene;
    private ListView<Score> scoreListView;
    private final Beatmap beatmap;
    private Score selectedScore;

    public ScoreDashboard(Beatmap beatmap) {
        this.beatmap = beatmap;
        createScene();
    }

    public void createScene() {
        HBox root = new HBox(60);
        root.setPadding(new Insets(120));
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(24);
        root.setBackground(ImageProvider.SONG_SELECT_BG.getImageAsBackground());

        VBox beatmapInfoCard = createBeatmapInfoCard();

        Label scoreTitleLabel = new Label("Submitted Scores");
        scoreTitleLabel.setFont(FontProvider.ARCADE_R.getFont(16));
        scoreTitleLabel.setStyle("-fx-text-fill: white;");

        VBox scoreListWrapper = new VBox(10);
        scoreListWrapper.setAlignment(Pos.TOP_CENTER);
        scoreListWrapper.setPadding(new Insets(10));
        scoreListWrapper.setStyle(
                """
                   -fx-background-color: linear-gradient(to bottom, #111111, #1A1A1A);
                   -fx-border-color: #0FF;
                   -fx-border-width: 2;
                   -fx-border-radius: 12;
                   -fx-background-radius: 12;
                   -fx-effect: dropshadow(gaussian, #0FF, 15, 0.2, 0, 0);
                """
        );
        scoreListWrapper.getChildren().addAll(scoreTitleLabel, createScoreListView());

        // Bind widths to 65% and 35% of the parent HBox
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            double totalWidth = newVal.doubleValue();
            beatmapInfoCard.setPrefWidth(totalWidth * 0.4);
            scoreListWrapper.setPrefWidth(totalWidth * 0.6);
        });

        root.getChildren().addAll(beatmapInfoCard, scoreListWrapper);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/mjfelecio/beatsync/styles/song-select.css").toExternalForm());
    }

    private VBox createBeatmapInfoCard() {
        VBox card = new VBox(20);
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

        // TODO: Replace this with the beatmap thumbnail later
        ImageView rankImage = new ImageView(getRankImage(Rank.S, 50));
        rankImage.setStyle("-fx-effect: dropshadow(gaussian, #0FF, 30, 0.3, 0, 0);");
        VBox beatmapInfo = createBeatmapInfo();

        card.getChildren().addAll(rankImage, beatmapInfo);
        return card;
    }

    private VBox createBeatmapInfo() {
        VBox beatmapInfo = new VBox();

        Label beatmapName = new Label("Try Sail - Utsuroi");
        beatmapName.setStyle("-fx-text-fill: white;");
        Label difficultyName = new Label("Hard");
        difficultyName.setStyle("-fx-text-fill: white;");
        Label artist = new Label("Try Sail");
        artist.setStyle("-fx-text-fill: white;");
        Label creator = new Label("Scotty");
        creator.setStyle("-fx-text-fill: white;");
        Label songLength = new Label("1m 30s");
        songLength.setStyle("-fx-text-fill: white;");
        Label noteCount = new Label("340 notes");
        noteCount.setStyle("-fx-text-fill: white;");

        beatmapInfo.getChildren().addAll(beatmapName, difficultyName, artist, creator, songLength, noteCount);
        return beatmapInfo;
    }


    private ListView<Score> createScoreListView() {
        scoreListView = new ListView<>();
        scoreListView.setFocusTraversable(false); // Prevent focus border
        scoreListView.setStyle("""
        -fx-background-color: transparent;
        -fx-control-inner-background: transparent;
        """);

        VBox.setVgrow(scoreListView, Priority.ALWAYS);

        ObservableList<Score> scores = null;

        try {
            // TEST
            int beatmapID = 2699390;
            scores = FXCollections.observableArrayList(ScoreDatabase.getScores(beatmapID));
        } catch (SQLException e) {
            System.err.println("Failed to get scores from db: " + e);
        }

        scoreListView.setItems(scores);
        scoreListView.setCellFactory(_ -> new ListCell<>() {
                private final int RANK_IMAGE_SIZE = 50;

                private final HBox content = new HBox(10);
                private final ImageView rankImage = new ImageView();
                private final Label score = new Label("134, 540");
                private final Label submittedAt = new Label("June 12, 2025 | 10:39 pm");
                private final Label maxCombo = new Label("Max Combo: 534");
                private final Label accuracy = new Label("Accuracy: 97.53%");

                private final Button viewPlayResultButton = new Button(">");

                // Containers
                private final HBox statsContainer = new HBox(5);
                private final VBox scoreAndTimeContainer = new VBox(5);
                private final Region spacer = new Region();
                private final VBox maxComboAndAccuracyContainer = new VBox(5);

                {
                    viewPlayResultButton.setMinSize(30, 30);

                    rankImage.setFitWidth(RANK_IMAGE_SIZE);
                    rankImage.setFitHeight(RANK_IMAGE_SIZE);

                    score.setFont(FontProvider.ARCADE_R.getFont(24));
                    score.setStyle("fx-text-fill: #0ff;");

                    submittedAt.setFont(FontProvider.ARCADE_R.getFont(6));
                    submittedAt.setStyle("fx-text-fill: #0ff;");

                    maxCombo.setFont(FontProvider.ARCADE_R.getFont(12));
                    maxCombo.setStyle("fx-text-fill: #0ff;");

                    accuracy.setFont(FontProvider.ARCADE_R.getFont(12));
                    accuracy.setStyle("fx-text-fill: #0ff;");

                    scoreAndTimeContainer.setAlignment(Pos.CENTER_LEFT);
                    scoreAndTimeContainer.getChildren().addAll(score, submittedAt);
                    maxComboAndAccuracyContainer.getChildren().addAll(maxCombo, accuracy);
                    maxComboAndAccuracyContainer.setAlignment(Pos.CENTER_LEFT);

                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    HBox.setHgrow(statsContainer, Priority.ALWAYS);
                    statsContainer.getChildren().addAll(scoreAndTimeContainer, spacer, maxComboAndAccuracyContainer);

                    content.setAlignment(Pos.CENTER_LEFT);
                    content.setPadding(new Insets(5));
                    content.setStyle("-fx-background-color: rgba(0,255,255,0.1); -fx-border-radius: 5; -fx-background-radius: 5;");
                    content.getChildren().addAll(rankImage, statsContainer, viewPlayResultButton);
                }

                @Override
                protected void updateItem(Score score, boolean empty) {
                    super.updateItem(score, empty);
                    if (empty || score == null) {
                        setGraphic(null);
                        setStyle("");
                    } else {
                        rankImage.setImage(getRankImage(score.getRank(), RANK_IMAGE_SIZE));
                        this.score.setText(String.valueOf(score.getScore()));
                        setGraphic(content);
                        setStyle("-fx-background-color: transparent;");

                        viewPlayResultButton.setOnAction(e -> viewFullPlayDetails(score));
                    }
                }
        });

        scoreListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedScore = newVal;
            if (selectedScore != null) {
                SFXPlayer.getInstance().play(SoundEffect.SELECT); // Plays a sound effect when you click on a cell
            }
        });

        return scoreListView;
    }

    private void viewFullPlayDetails(Score score) {
        System.out.println(score);
    }

    private static Image getRankImage(Rank rank, int rankImageSize) {
        return switch (rank) {
            case SS -> ImageProvider.SS_RANK.getImage(rankImageSize, rankImageSize, true, true);
            case S -> ImageProvider.S_RANK.getImage(rankImageSize, rankImageSize, true, true);
            case A -> ImageProvider.A_RANK.getImage(rankImageSize, rankImageSize, true, true);
            case B -> ImageProvider.B_RANK.getImage(rankImageSize, rankImageSize, true, true);
            case C -> ImageProvider.C_RANK.getImage(rankImageSize, rankImageSize, true, true);
            case D -> ImageProvider.D_RANK.getImage(rankImageSize, rankImageSize, true, true);
        };
    }

    public Scene getScene() {
        return scene;
    }
}
