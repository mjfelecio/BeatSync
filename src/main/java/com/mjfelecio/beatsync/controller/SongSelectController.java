package com.mjfelecio.beatsync.controller;

import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.BeatmapSet;
import com.mjfelecio.beatsync.parser.BeatmapLoader;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.state.GameState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SongSelectController {
    private ListView<BeatmapSet> songListView = new ListView<>();
    private ListView<Beatmap> diffListView = new ListView<>();
    private VBox difficultyListViewWrapper;

    private BeatmapSet selectedBeatmapSet;
    private Beatmap selectedBeatmap;

    public Scene getSongSelectScene() {
            VBox root = new VBox();
            root.setPrefSize(600, 800);
            root.setAlignment(Pos.CENTER);
            root.setStyle("-fx-padding: 20;");

            Label songSelectLabel = new Label("Song Select");
            songSelectLabel.setFont(new Font("Verdana", 25));

            songListView = createSongListView();
            songListView.setPrefWidth(400);

            difficultyListViewWrapper = new VBox(5);
            difficultyListViewWrapper.setAlignment(Pos.CENTER);
            difficultyListViewWrapper.setPrefHeight(150);

            HBox buttonContainer = new HBox(10);

            Button backButton = new Button("Back");
            backButton.setOnAction(e -> SceneManager.getInstance().setCurrentScene(GameScene.TITLE_SCREEN));

            Button playButton = new Button("Play");
            playButton.setOnAction(e -> {
                GameState.getInstance().setCurrentBeatmap(selectedBeatmap);
                SceneManager.getInstance().setCurrentScene(GameScene.GAMEPLAY);
            });

            buttonContainer.getChildren().addAll(backButton, playButton);
            buttonContainer.setAlignment(Pos.CENTER);

            root.getChildren().addAll(songSelectLabel, songListView, new Label("Difficulties"), difficultyListViewWrapper, buttonContainer);

        return new Scene(root, 1920, 1080);
    }

    public ListView<BeatmapSet> createSongListView() {
        BeatmapLoader.load();
        ObservableList<BeatmapSet> songs = FXCollections.observableArrayList(BeatmapLoader.getInstance().getAllBeatmaps());
        songListView.setItems(songs);

        songListView.setCellFactory(_ -> new ListCell<>() {
            private final HBox content;
            private final ImageView thumbnail;
            private final Label title;

            {
                thumbnail = new ImageView();
                thumbnail.setFitWidth(50);
                thumbnail.setFitHeight(50);
                title = new Label();
                title.setStyle("-fx-font-size: 16px;");
                content = new HBox(10, thumbnail, title);
                content.setAlignment(Pos.CENTER_LEFT);
            }

            @Override
            protected void updateItem(BeatmapSet beatmapSet, boolean empty) {
                super.updateItem(beatmapSet, empty);
                if (empty || beatmapSet == null) {
                    setGraphic(null);
                } else {
                    String imagePath = new File(beatmapSet.getImagePath()).toURI().toASCIIString();
                    thumbnail.setImage(new Image(imagePath));
                    title.setText(beatmapSet.getTitle());
                    setGraphic(content);
                }
            }
        });

        songListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBeatmapSet = newVal;
            if (selectedBeatmapSet != null) {
                difficultyListViewWrapper.getChildren().remove(diffListView);

                diffListView = createDifficultyListView(selectedBeatmapSet);
                difficultyListViewWrapper.getChildren().add(diffListView);
            }
        });

        return songListView;
    }

    private ListView<Beatmap> createDifficultyListView(BeatmapSet beatmapSet) {
        ListView<Beatmap> diffListView = new ListView<>();
        diffListView.setPrefHeight(120);

        // Sort difficulties by note count ascending (more notes = higher diff)
        // This is a temporary workaround until I get to implement star rating calculation
        List<Beatmap> sortedDiffs = beatmapSet.getDifficulties().stream()
                .sorted(Comparator.comparingInt(a -> a.getNotes().size()))
                .toList();

        diffListView.setItems(FXCollections.observableArrayList(sortedDiffs));
        diffListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Beatmap diff, boolean empty) {
                super.updateItem(diff, empty);
                setText((diff == null || empty) ? null : diff.getTitle() + " - " + diff.getDiffName());
            }
        });

        diffListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBeatmap = newVal;
        });

        return diffListView;
    }
}
