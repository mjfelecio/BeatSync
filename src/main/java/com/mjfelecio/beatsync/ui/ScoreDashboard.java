package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.ScoreDatabase;
import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.object.Rank;
import com.mjfelecio.beatsync.object.Score;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class ScoreDashboard {
    private Scene scene;
    private ListView<Score> scoreListView;
    private final int beatmapID;
    private Score selectedScore;

    public ScoreDashboard(int beatmapID) {
        this.beatmapID = beatmapID;
        createScene();
    }

    public void createScene() {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(24);
        root.setBackground(ImageProvider.SONG_SELECT_BG.getImageAsBackground());

        root.getChildren().addAll(createScoreListView());

        scene = new Scene(root);
    }

    private ListView<Score> createScoreListView() {
        scoreListView = new ListView<>();
        ObservableList<Score> scores = null;
        scoreListView.setFocusTraversable(false); // Prevent focus border
        scoreListView.setStyle("""
        -fx-background-color: transparent;
        -fx-control-inner-background: transparent;
        -fx-padding: 10;
        """);

        try {
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
