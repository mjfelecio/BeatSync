package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.parser.ManiaBeatmapParser;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class test {
    private final int width;
    private final int height;
    private final int LANES = 4;
    private final int NOTE_SIZE = 80;

    private final int PERFECT_WINDOW = 30;
    private final int GOOD_WINDOW = 80;
    private final int MISS_WINDOW = 150;

    private GameClock gameClock;
    private ArrayList<Note> notes;
    private ArrayList<Note> activeNotes;
    private boolean[] pressedLanes = new boolean[LANES];

    private final int APPROACH_TIME = 1000;

    public test(int width, int height, GameClock clock) {
        this.width = width;
        this.height = height;
        this.gameClock = clock;

        try {
            File beatmapFile = new File("src/main/resources/com/mjfelecio/beatsync/beatmaps/test.osu");
            notes = new BeatmapParser(ManiaBeatmapParser.parse(beatmapFile)).parseNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        activeNotes = new ArrayList<>();
    }

    /**
     * Update active notes and handle hits/misses.
     */
    public void update(long timeElapsed) {
        // Activate notes whose time has arrived
        for (Note n : notes) {
            if (timeElapsed >= n.getTime() && !activeNotes.contains(n) && !n.isHit()) {
                activeNotes.add(n);
            }
        }

        // Remove misses automatically
        Iterator<Note> missIter = activeNotes.iterator();
        while (missIter.hasNext()) {
            Note n = missIter.next();
            // If the notes wasn't hit already AND has passed the miss window, mark it as a miss
            // Explanation for future me:
            //  The notes are supposed to be hit at a specific time in the music
            //  If they haven't been hit yet at their specific time in the specified TIMING WINDOWS, they're a miss
            if (!n.isHit() && (timeElapsed - n.getTime()) > MISS_WINDOW) {
                registerScore("Miss");
                missIter.remove();
            }
        }

        // Handle presses
        for (int lane = 0; lane < LANES; lane++) {
            if (pressedLanes[lane]) {
                Note best = null;

                // bestDelta represents the closest note from the elapsed time in music
                // This should result in only one note being removed and not overlapping notes
                // However, I'm still running into bugs with it
                // TODO: Fix multiple notes being removed at the same time if they are too close together
                long bestDelta = Long.MAX_VALUE;
                for (Note n : activeNotes) {
                    if (n.getLaneNumber() != lane || n.isHit()) continue;
                    long delta = Math.abs(timeElapsed - n.getTime());
                    if (delta < bestDelta && delta <= MISS_WINDOW) {
                        bestDelta = delta;
                        best = n;
                    }
                }

                if (best != null) {
                    best.setHit(true);
                    if (bestDelta <= PERFECT_WINDOW) registerScore("Perfect");
                    else if (bestDelta <= GOOD_WINDOW) registerScore("Good");
                    else registerScore("Miss");
                }
            }
        }

        // Remove processed notes from active list
//        activeNotes.removeIf(n -> n.isHit() || (n.getY(timeElapsed, APPROACH_TIME, height - 100) > height));
    }

    /**
     * Draw playfield and active notes.
     */
    public void render(GraphicsContext gc) {
        int laneWidth = width / LANES;
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Draw lanes and hit zones
        for (int i = 0; i < LANES; i++) {
            gc.setStroke(Color.WHITE);
            gc.strokeLine(i * laneWidth, 0, i * laneWidth, height);
            int hitY = height - 150;
            gc.strokeOval(i * laneWidth + (laneWidth - NOTE_SIZE) / 2, hitY, NOTE_SIZE, NOTE_SIZE);
        }

        // Draw notes
        gc.setFill(Color.BLUE);
        for (Note n : activeNotes) {
            // WAit, not sure about the parameter yet
//            double y = n.getY(gameClock.getElapsedTime(System.currentTimeMillis()), APPROACH_TIME, height - 100);
//            gc.fillOval(n.getLaneNumber() * laneWidth + (laneWidth - NOTE_SIZE) / 2,
//                    y, NOTE_SIZE, NOTE_SIZE);
        }
    }

    public void pressKey(KeyCode code) {
        switch(code) {
            case D -> pressedLanes[0] = true;
            case F -> pressedLanes[1] = true;
            case J -> pressedLanes[2] = true;
            case K -> pressedLanes[3] = true;
        }
    }

    public void releaseKey(KeyCode code) {
        switch(code) {
            case D -> pressedLanes[0] = false;
            case F -> pressedLanes[1] = false;
            case J -> pressedLanes[2] = false;
            case K -> pressedLanes[3] = false;
        }
    }

    private void registerScore(String rating) {
        // TODO: hook into ScoreProcessor
        System.out.println(rating);
    }
}
//
//package com.mjfelecio.beatsync.core;
//
//public class Not {
//    private final int x;
//    private int y = -100; // Notes start at the very top of the screen anyway
//    private final int time;
//    private final int endTime; // Null for normal notes
//    private final boolean isHoldNote;
//    private boolean isHit = false;
//
//    // Creates a normal note
//    public Not(int laneNumber, int startTime) {
//        this.x = laneNumber;
//        this.time = startTime;
//        this.endTime = 0;
//        this.isHoldNote = false;
//    }
//
//    // Creates a hold note
//    public Not(int laneNumber, int startTime, int endTime) {
//        this.x = laneNumber;
//        this.time = startTime;
//        this.endTime = endTime;
//        this.isHoldNote = true;
//    }
//
//    public int getX() { return x; }
//
//    /**
//     * Calculates the current Y-coordinate of the note based on elapsed song time.
//     * @param elapsedMs elapsed milliseconds since song start
//     * @param approachTimeMs how early the note appears before hit
//     * @param hitLineY vertical position of the hit line
//     * @return Y coordinate where note should be drawn
//     */
//    public double getY(long elapsedMs, long approachTimeMs, int hitLineY) {
//        double timeUntilHit = this.time - elapsedMs;
//        double progress = 1.0 - (timeUntilHit / approachTimeMs);
//        return hitLineY * Math.min(Math.max(progress, 0.0), 1.0);
//    }
//
//    public int getTime() { return time; }
//    public int getEndTime() { return endTime; }
//    public void setY(int y) { this.y = y; }
//
//    public boolean isHit() { return isHit; }
//    public void setHit(boolean b) { isHit = b; }
//
//    public int getLaneNumber() {
//        int lane = 0;
//
//        // Note that the lanes are zero-indexed
//        switch (x) {
//            case 64 -> lane = 0;
//            case 192 -> lane = 1;
//            case 320 -> lane = 2;
//            case 448 -> lane = 3;
//        }
//
//        return lane;
//    }
//
//    @Override
//    public String toString() {
//        return isHoldNote ? x + "," + time + "," + endTime : x + "," + time;
//    }
//}
//
//package com.mjfelecio.beatsync;
//
//import com.mjfelecio.beatsync.core.AudioEngine;
//import com.mjfelecio.beatsync.core.GameClock;
//import com.mjfelecio.beatsync.core.Playfield;
//import javafx.animation.AnimationTimer;
//import javafx.application.Application;
//import javafx.scene.Scene;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.layout.StackPane;
//import javafx.scene.media.Media;
//import javafx.stage.Stage;
//
//import java.io.File;
//
//public class Main extends Application {
//    private final int WIDTH = 1920;
//    private final int HEIGHT = 1080;
//    private final int PLAYFIELD_WIDTH = 400;
//    private final int PLAYFIELD_HEIGHT = 700;
//
//    private Playfield playfield;
//    private GameClock gameClock;
//    private AudioEngine audioEngine;
//
//    @Override
//    public void start(Stage stage) {
//        gameClock = new GameClock();
//
//        Canvas canvas = new Canvas(PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//
//        playfield = new Playfield(PLAYFIELD_WIDTH, PLAYFIELD_HEIGHT, gameClock);
//        playfield.render(gc);
//
//        String filePath = new File(
//                "src/main/resources/com/mjfelecio/beatsync/1301440 TrySail - Utsuroi (Short Ver.) (another copy).osz_FILES/audio.mp3"
//        ).toURI().toString();
//        audioEngine = new AudioEngine();
//        audioEngine.setMusic(new Media(filePath));
//        audioEngine.getPlayer().play();
//        gameClock.start();
//
//        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
//        stage.setScene(scene);
//        stage.setTitle("Beat Sync: VSRG made with Java");
//        stage.show();
//
//        scene.setOnKeyPressed(event -> playfield.pressKey(event.getCode()));
//        scene.setOnKeyReleased(event -> playfield.releaseKey(event.getCode()));
//
//        new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                gc.clearRect(0, 0, WIDTH, HEIGHT);
//
//                playfield.render(gc);
//                playfield.update(gameClock.getElapsedTime(now));
//            }
//        }.start();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
