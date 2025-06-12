package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.audio.SFXPlayer;
import com.mjfelecio.beatsync.audio.SoundEffect;
import com.mjfelecio.beatsync.core.SceneManager;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.BeatmapSet;
import com.mjfelecio.beatsync.core.BeatmapLoader;
import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.utils.FontProvider;
import com.mjfelecio.beatsync.utils.ImageProvider;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.Comparator;
import java.util.List;

public class SongSelectUI {
    private final Scene scene;

    private ListView<BeatmapSet> songListView;
    private ListView<Beatmap> diffListView;
    private VBox difficultyListViewWrapper;

    private BeatmapSet selectedBeatmapSet;
    private Beatmap selectedBeatmap;

    public SongSelectUI() {
        VBox root = createRootLayout();
        root.getChildren().addAll(
                createTitleLabel(),
                createListViewSection(),
                createNavigationButtons()
        );
        StackPane rootWrapper = new StackPane(root);
        scene = new Scene(rootWrapper);
        scene.getStylesheets().add(getClass().getResource("/com/mjfelecio/beatsync/styles/song-select.css").toExternalForm());
    }

    public Scene getScene() {
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
        backButton.setOnAction(e -> SceneManager.getInstance().loadTitleScreen());

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
            navigateToGameplay();
        });

        HBox buttonBox = new HBox(20, backButton, playButton);
        buttonBox.setAlignment(Pos.CENTER);
        return buttonBox;
    }

    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setFont(FontProvider.ARCADE_R.getFont(16));
        label.setStyle("-fx-text-fill: #CCCCCC;");
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
                thumbnail.setFitWidth(60);
                thumbnail.setFitHeight(60);
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
                    thumbnail.setImage(new Image(beatmapSet.getImagePath()));
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
                SFXPlayer.getInstance().play(SoundEffect.SELECT); // Plays a sound effect when you click on a cell
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

        diffListView.setCellFactory(_ -> new ListCell<>() {
            private final HBox content = new HBox(10);
            private final Label title = new Label();
            private final Region spacer = new Region();
            private final Region scoreNavigationButton = createScoreNavigationButton();

            {
                HBox.setHgrow(spacer, Priority.ALWAYS);
                title.setStyle("""
                -fx-font-size: 14px;
                -fx-text-fill: #0ff;
                -fx-font-family: 'Verdana';
                """);
                content.setAlignment(Pos.CENTER_LEFT);
                content.setPadding(new Insets(0, 10, 0, 0));
                content.getChildren().addAll(title, spacer, scoreNavigationButton);
            }

            @Override
            protected void updateItem(Beatmap diff, boolean empty) {
                super.updateItem(diff, empty);
                if (empty || diff == null) {
                    setGraphic(null);
                } else {
                    title.setText(diff.getDiffName());

                    scoreNavigationButton.setOnMouseClicked(e -> {
                        System.out.println("Clicked score list for: " + diff.getDiffName());
                    });

                    setStyle("""
                    -fx-background-color: rgba(0,255,255,0.05);
                    -fx-background-radius: 6;
                    -fx-padding: 5 10;
                    """);

                    setGraphic(content);
                }
            }
        });

        diffListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedBeatmap = newVal;
            SFXPlayer.getInstance().play(SoundEffect.SELECT);
        });

        return diffListView;
    }

    public void navigateToGameplay() {
        if (selectedBeatmap == null) {
            SFXPlayer.getInstance().play(SoundEffect.NOTIFICATION_ERROR);
            NotificationManager.showNotification(scene, Notification.ERROR, "No beatmap currently selected");
            return;
        }
        GameState.getInstance().setCurrentBeatmap(selectedBeatmap);
        SceneManager.getInstance().loadGameplay();
        selectedBeatmap = null; // Remove the currently selected beatmap
    }

    public Region createScoreNavigationButton() {
        Region scoreNavigationButton = createArrowHeadRegion(16, 16, 1);

        scoreNavigationButton.setOnMouseEntered(e -> scoreNavigationButton.setStyle("""
                -fx-cursor: hand;
                -fx-background-color: rgba(0,255,255,0.4);
                -fx-background-radius: 4px;
                -fx-padding: 4;
                """));
        scoreNavigationButton.setOnMouseExited(e -> scoreNavigationButton.setStyle("""
                -fx-cursor: hand;
                -fx-background-color: #0FF;
                -fx-background-radius: 4px;
                -fx-padding: 4;
                """));

        return scoreNavigationButton;
    }

    // I didn't make this, I just had claude create a shape for me.
    public static Region createArrowHeadRegion(double width, double height, double strokeWidth) {
        // Create the arrow head shape using Path
        Path arrowShape = new Path();

        double halfHeight = height / 2;
        double lineOffset = strokeWidth * 0.75; // Gap between the two lines

        // First arrow line (top)
        MoveTo start1 = new MoveTo(0, halfHeight - lineOffset);
        LineTo tip1 = new LineTo(width, halfHeight);
        LineTo end1 = new LineTo(0, halfHeight - strokeWidth - lineOffset);

        // Second arrow line (bottom)
        MoveTo start2 = new MoveTo(0, halfHeight + lineOffset);
        LineTo tip2 = new LineTo(width, halfHeight);
        LineTo end2 = new LineTo(0, halfHeight + strokeWidth + lineOffset);

        arrowShape.getElements().addAll(start1, tip1, end1, start2, tip2, end2);

        // Make the lines rounded
        arrowShape.setStrokeWidth(strokeWidth);
        arrowShape.setStroke(Color.web("#0FF"));
        arrowShape.setFill(null); // No fill, just strokes
        arrowShape.setStrokeLineCap(StrokeLineCap.ROUND);
        arrowShape.setStrokeLineJoin(StrokeLineJoin.ROUND);

        // Create Region and set the shape
        Region region = new Region();
        region.setShape(arrowShape);
        region.setStyle("-fx-background-color: #0FF;");
        region.setPrefSize(width, height);
        region.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        region.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        return region;
    }
}
