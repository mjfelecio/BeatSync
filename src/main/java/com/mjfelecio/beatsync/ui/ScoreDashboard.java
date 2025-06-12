package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.ScoreDatabase;
import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.Rank;
import com.mjfelecio.beatsync.object.Score;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
        root.setStyle("-fx-border-color: red;");
        root.setBackground(ImageProvider.SONG_SELECT_BG.getImageAsBackground());

        VBox beatmapInfoCard = createBeatmapInfoCard();

        VBox scoreListWrapper = new VBox(createScoreListView());
        scoreListWrapper.setAlignment(Pos.TOP_CENTER);
        scoreListWrapper.setStyle("-fx-border-color: red;");

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
        -fx-padding: 10;
        -fx-border-color: red;
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
            private final Label score = new Label();

            {
                rankImage.setFitWidth(RANK_IMAGE_SIZE);
                rankImage.setFitHeight(RANK_IMAGE_SIZE);
                score.setStyle("""
                -fx-font-size: 16px;
                -fx-text-fill: #0ff;
                -fx-font-family: 'Verdana';
                """);
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(rankImage, score);
                content.setStyle("-fx-background-color: rgba(0,255,255,0.1); -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            }

            @Override
            protected void updateItem(Score score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setGraphic(null);
                    setStyle(""); // reset cell style
                } else {
                    rankImage.setImage(getRankImage(score.getRank(), RANK_IMAGE_SIZE));
                    this.score.setText(String.valueOf(score.getScore()));
                    setGraphic(content);
                    setStyle("-fx-background-color: transparent;");
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
