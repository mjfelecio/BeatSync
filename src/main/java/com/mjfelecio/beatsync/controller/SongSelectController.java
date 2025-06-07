package com.mjfelecio.beatsync.controller;

import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.BeatmapSet;
import com.mjfelecio.beatsync.parser.BeatmapLoader;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SongSelectController {
    private ListView<BeatmapSet> songListView;
    private ListView<Beatmap> diffListView;
    private VBox difficultyListViewWrapper;

    private BeatmapSet selectedBeatmapSet;
    private Beatmap selectedBeatmap;

    public Scene getSongSelectScene() {
        VBox root = createRootLayout();
        root.getChildren().addAll(
                createTitleLabel(),
                createListViewSection(),
                createNavigationButtons()
        );
        return new Scene(root, 1920, 1080);
    }

    private VBox createRootLayout() {
        VBox root = new VBox(30);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(1200, 1000);
        root.setStyle("-fx-padding: 20;");
        root.setBackground(ImageProvider.SONG_SELECT_BG.getImageAsBackground());
        return root;
    }

    private Label createTitleLabel() {
        Label title = new Label("Song Select");
        title.setFont(new Font("Verdana", 25));
        title.setStyle("-fx-text-fill: green;");
        return title;
    }

    private HBox createListViewSection() {
        HBox listViewContainer = new HBox(40);
        listViewContainer.setAlignment(Pos.TOP_CENTER);

        VBox songListSection = new VBox(10, createSectionLabel("Select Song"), createSongListView());
        songListSection.setAlignment(Pos.TOP_CENTER);

        VBox difficultySection = new VBox(10);
        difficultySection.setAlignment(Pos.TOP_CENTER);
        difficultyListViewWrapper = new VBox(5);
        difficultyListViewWrapper.setAlignment(Pos.CENTER);
        difficultySection.getChildren().addAll(createSectionLabel("Select Difficulty"), difficultyListViewWrapper);

        listViewContainer.getChildren().addAll(songListSection, difficultySection);
        return listViewContainer;
    }

    private HBox createNavigationButtons() {
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> SceneManager.getInstance().setCurrentScene(GameScene.TITLE_SCREEN));

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> {
            GameState.getInstance().setCurrentBeatmap(selectedBeatmap);
            SceneManager.getInstance().setCurrentScene(GameScene.GAMEPLAY);
        });

        HBox buttonBox = new HBox(20, backButton, playButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(16));
        label.setStyle("-fx-text-fill: green;");
        return label;
    }

    private ListView<BeatmapSet> createSongListView() {
        songListView = new ListView<>();
        BeatmapLoader.load();

        ObservableList<BeatmapSet> beatmapSets = FXCollections.observableArrayList(
                BeatmapLoader.getInstance().getAllBeatmaps()
        );
        songListView.setItems(beatmapSets);

        songListView.setCellFactory(_ -> new ListCell<>() {
            private final HBox content = new HBox(10);
            private final ImageView thumbnail = new ImageView();
            private final Label title = new Label();

            {
                thumbnail.setFitWidth(80);
                thumbnail.setFitHeight(80);
                title.setStyle("-fx-font-size: 16px;");
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(thumbnail, title);
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
                updateDifficultyList(selectedBeatmapSet);
            }
        });

        return songListView;
    }

    private void updateDifficultyList(BeatmapSet beatmapSet) {
        difficultyListViewWrapper.getChildren().clear();
        diffListView = createDifficultyListView(beatmapSet);
        difficultyListViewWrapper.getChildren().add(diffListView);
    }

    private ListView<Beatmap> createDifficultyListView(BeatmapSet beatmapSet) {
        List<Beatmap> sortedDiffs = beatmapSet.getDifficulties().stream()
                .sorted(Comparator.comparingInt(a -> a.getNotes().size()))
                .toList();

        ListView<Beatmap> diffListView = new ListView<>();
        diffListView.setPrefHeight(150);
        diffListView.setItems(FXCollections.observableArrayList(sortedDiffs));

        diffListView.setCellFactory(_ -> new ListCell<>() {
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
