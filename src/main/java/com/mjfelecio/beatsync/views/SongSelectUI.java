package com.mjfelecio.beatsync.views;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.BeatmapSet;
import com.mjfelecio.beatsync.parser.BeatmapLoader;
import com.mjfelecio.beatsync.rendering.GameScene;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.io.File;
import java.util.Comparator;
import java.util.List;

public class SongSelectUI {
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
        Scene scene = new Scene(root, GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        scene.getStylesheets().add(getClass().getResource("/com/mjfelecio/beatsync/styles/song-select.css").toExternalForm());
        return scene;
    }

    private VBox createRootLayout() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");
        root.setBackground(ImageProvider.SONG_SELECT_BG.getImageAsBackground());
        return root;
    }

    private Label createTitleLabel() {
        Label title = new Label("Song Select");
        title.setPadding(new Insets(30, 0, 0, 0));
        title.setFont(FontProvider.ARCADE_R.getFont(40));
        title.setStyle("-fx-text-fill: #0FF;");
        return title;
    }

    private HBox createListViewSection() {
        HBox listViewContainer = new HBox(20);
        listViewContainer.setAlignment(Pos.TOP_CENTER);
        listViewContainer.setPrefSize(1000, 450);
        listViewContainer.setMaxWidth(Region.USE_PREF_SIZE);

        // Song List Section
        VBox songListSection = new VBox(10, createSectionLabel("Select Song"), createSongListView());
        songListSection.setAlignment(Pos.TOP_CENTER);
        songListSection.setStyle("""
        -fx-border-color: #0ff;
        -fx-border-width: 2;
        -fx-padding: 10;
        -fx-border-radius: 8;
        """);

        // Difficulty Section
        VBox difficultySection = new VBox(10);
        difficultySection.setAlignment(Pos.TOP_CENTER);
        difficultyListViewWrapper = new VBox(5);
        VBox.setVgrow(difficultyListViewWrapper, Priority.ALWAYS); // This makes the wrapper take up the whole space

        difficultySection.getChildren().addAll(createSectionLabel("Select Difficulty"), difficultyListViewWrapper);
        difficultySection.setStyle("""
        -fx-border-color: #0ff;
        -fx-border-width: 2;
        -fx-padding: 10;
        -fx-border-radius: 8;
        """);

        // Make both sections grow horizontally
        HBox.setHgrow(songListSection, Priority.ALWAYS);
        HBox.setHgrow(difficultySection, Priority.ALWAYS);

        // Bind widths to 65% and 35% of the parent HBox
        listViewContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double totalWidth = newVal.doubleValue();
            songListSection.setPrefWidth(totalWidth * 0.65);
            difficultySection.setPrefWidth(totalWidth * 0.35);
        });

        listViewContainer.getChildren().addAll(songListSection, difficultySection);
        return listViewContainer;
    }

    private HBox createNavigationButtons() {
        Button backButton = new Button("Back");
        backButton.setFont(FontProvider.ARCADE_R.getFont(12));
        backButton.setStyle("""
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """);
        backButton.setOnAction(e -> SceneManager.getInstance().setCurrentScene(GameScene.TITLE_SCREEN));

        Button playButton = new Button("Play");
        playButton.setFont(FontProvider.ARCADE_R.getFont(12));
        playButton.setStyle("""
            -fx-text-fill: #CCCCCC;
            -fx-background-color: black;
            -fx-border-color: #0FF;
            -fx-border-width: 3px;
            -fx-border-radius: 5px;
            -fx-background-radius: 5px;
            -fx-padding: 8 20 8 20;
            -fx-cursor: hand;
        """);
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
        label.setFont(FontProvider.ARCADE_R.getFont(16));
        label.setStyle("-fx-text-fill: #0FF;");
        return label;
    }

    private ListView<BeatmapSet> createSongListView() {
        songListView = new ListView<>();
        songListView.setFocusTraversable(false); // Prevent focus border
        songListView.setStyle("""
        -fx-background-color: transparent;
        -fx-control-inner-background: transparent;
        -fx-padding: 10;
        """);

        // Hide scrollbars using CSS
        songListView.lookupAll(".scroll-bar").forEach(sb -> sb.setVisible(false));
        songListView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            songListView.lookupAll(".scroll-bar").forEach(sb -> sb.setVisible(false));
        });

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
                title.setStyle("""
                -fx-font-size: 16px;
                -fx-text-fill: #0ff;
                -fx-font-family: 'Verdana';
                """);
                content.setAlignment(Pos.CENTER_LEFT);
                content.getChildren().addAll(thumbnail, title);
                content.setStyle("-fx-background-color: rgba(0,255,255,0.1); -fx-padding: 5 10; -fx-border-radius: 5; -fx-background-radius: 5;");
            }

            @Override
            protected void updateItem(BeatmapSet beatmapSet, boolean empty) {
                super.updateItem(beatmapSet, empty);
                if (empty || beatmapSet == null) {
                    setGraphic(null);
                    setStyle(""); // reset cell style
                } else {
                    String imagePath = new File(beatmapSet.getImagePath()).toURI().toASCIIString();
                    thumbnail.setImage(new Image(imagePath));
                    title.setText(beatmapSet.getTitle());
                    setGraphic(content);
                    setStyle("-fx-background-color: transparent;");
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

        diffListView = new ListView<>();
        diffListView.setPrefHeight(150);
        diffListView.setItems(FXCollections.observableArrayList(sortedDiffs));
        diffListView.setStyle("""
        -fx-background-color: transparent;
        -fx-control-inner-background: transparent;
        -fx-padding: 10;
        """);
        VBox.setVgrow(diffListView, Priority.ALWAYS);

        // Hide scrollbars after skin loads
        diffListView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            diffListView.lookupAll(".scroll-bar").forEach(sb -> sb.setVisible(false));
        });

        diffListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Beatmap diff, boolean empty) {
                super.updateItem(diff, empty);
                if (empty || diff == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(diff.getTitle() + " - " + diff.getDiffName());
                    setStyle("""
                    -fx-text-fill: #0ff;
                    -fx-font-size: 14px;
                    -fx-font-family: 'Verdana';
                    -fx-background-color: rgba(0,255,255,0.1);
                    -fx-padding: 5 10;
                    -fx-background-radius: 5;
                """);
                }
            }
        });

        diffListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBeatmap = newVal;
        });

        return diffListView;
    }

}
