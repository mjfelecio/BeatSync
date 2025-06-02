package com.mjfelecio.beatsync.controller;

import com.mjfelecio.beatsync.object.Beatmap;
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

    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox();
        root.setPrefSize(600, 800);
        root.setAlignment(Pos.CENTER);

        Label songSelectLabel = new Label("Song Select");
        songSelectLabel.setFont(new Font("Verdana", 25));

        ListView<Beatmap> songListView = createSongListView();
        songListView.setPrefSize(600, 800);

        HBox buttonContainer = new HBox();
        Button backButton = new Button("Back");
        Button playButton = new Button("Play");
        buttonContainer.getChildren().addAll(backButton, playButton);
        buttonContainer.setAlignment(Pos.CENTER);

        root.getChildren().addAll(songSelectLabel, songListView, buttonContainer);

        Scene songSelectScene = new Scene(root, 1920, 1080);
        stage.setScene(songSelectScene);
        stage.show();
    }

    public ListView<Beatmap> createSongListView() {
        ListView<Beatmap> songListView = new ListView<>();

        BeatmapLoader.load();
        ObservableList<Beatmap> songs = FXCollections.observableArrayList(BeatmapLoader.getInstance().getAllBeatmaps());
        songListView.setItems(songs);

        songListView.setCellFactory(listView -> new ListCell<Beatmap>() {
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
            protected void updateItem(Beatmap beatmap, boolean empty) {
                super.updateItem(beatmap, empty);
                if (empty || beatmap == null) {
                    setGraphic(null);
                } else {
                    String imagePath = new File("/" + beatmap.getImagePath()).toURI().toString();
                    thumbnail.setImage(new Image(imagePath));
                    title.setText(beatmap.getTitle());
                    setGraphic(content);
                }
            }
        });

        return songListView;
    }

    public static void main(String[] args) {
        launch();
    }
}
