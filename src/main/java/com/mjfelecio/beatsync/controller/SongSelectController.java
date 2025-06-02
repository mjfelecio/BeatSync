package com.mjfelecio.beatsync.controller;

import com.mjfelecio.beatsync.object.BeatmapSet;
import com.mjfelecio.beatsync.object.Difficulty;
import com.mjfelecio.beatsync.parser.BeatmapLoader;
import javafx.application.Application;
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
import javafx.stage.Stage;

import java.io.File;

public class SongSelectController extends Application {
    private ListView<BeatmapSet> songListView = new ListView<>();
    private ListView<Difficulty> diffListView = new ListView<>();
    private VBox difficultyListViewWrapper;

    private BeatmapSet selectedBeatmapSet;
    private Difficulty selectedDifficulty;

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        root.setPrefSize(600, 800);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");

        Label songSelectLabel = new Label("Song Select");
        songSelectLabel.setFont(new Font("Verdana", 25));

        songListView = createSongListView();
        songListView.setPrefSize(400, 300);

        difficultyListViewWrapper = new VBox(5);
        difficultyListViewWrapper.setAlignment(Pos.CENTER);
        difficultyListViewWrapper.setPrefHeight(150);

        HBox buttonContainer = new HBox(10);
        Button backButton = new Button("Back");
        Button playButton = new Button("Play");
        buttonContainer.getChildren().addAll(backButton, playButton);
        buttonContainer.setAlignment(Pos.CENTER);

        root.getChildren().addAll(songSelectLabel, songListView, new Label("Difficulties"), difficultyListViewWrapper, buttonContainer);

        Scene songSelectScene = new Scene(root, 1920, 1080);
        stage.setScene(songSelectScene);
        stage.show();
    }

    public ListView<BeatmapSet> createSongListView() {
        BeatmapLoader.load();
        ObservableList<BeatmapSet> songs = FXCollections.observableArrayList(BeatmapLoader.getInstance().getAllBeatmaps());
        songListView.setItems(songs);

        songListView.setCellFactory(listView -> new ListCell<BeatmapSet>() {
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
                    String imagePath = new File(beatmapSet.getImagePath()).toURI().toString();
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

    private ListView<Difficulty> createDifficultyListView(BeatmapSet beatmapSet) {
        ListView<Difficulty> diffListView = new ListView<>();
        diffListView.setPrefHeight(120);
        diffListView.setItems(FXCollections.observableArrayList(beatmapSet.getDifficulties()));
        diffListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Difficulty diff, boolean empty) {
                super.updateItem(diff, empty);
                setText((diff == null || empty) ? null : diff.getTitle());
            }
        });

        diffListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedDifficulty = newVal;
            System.out.println("Selected difficulty: " + selectedDifficulty);
        });

        return diffListView;
    }

    public static void main(String[] args) {
        launch();
    }
}
