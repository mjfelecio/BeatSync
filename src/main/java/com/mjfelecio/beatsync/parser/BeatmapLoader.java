package com.mjfelecio.beatsync.parser;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.object.Beatmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeatmapLoader {
    private static BeatmapLoader instance = new BeatmapLoader(GameConfig.BEATMAP_DIRECTORY) ;
    private List<Beatmap> beatmaps;
    private List<File> beatmapFiles; // Each file is a beatmap folder extracted from .osz

    private BeatmapLoader(String beatmapsDirectoryPath) {
        try {
            this.beatmapFiles = getBeatmapFiles(beatmapsDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BeatmapLoader getInstance() {
        return instance;
    }

    private List<File> getBeatmapFiles(String beatmapsDirectoryPath) throws IOException {
        List<File> beatmapFiles = new ArrayList<>();
        File beatmapsDirectory = new File(beatmapsDirectoryPath);

        if (beatmapsDirectory.exists() && beatmapsDirectory.isDirectory()) {
            File[] files = beatmapsDirectory.listFiles();
            if (files != null) {
                beatmapFiles.addAll(Arrays.asList(files));
            } else {
                throw new IOException("No beatmap file found");
            }
        }

        return beatmapFiles;
    }

    public static void load() {

    }

    public static void main(String[] args) throws IOException {
        List<File> beatmaps = getInstance().getBeatmapFiles(GameConfig.BEATMAP_DIRECTORY);
        beatmaps.forEach(f -> System.out.println(f.getName()));
    }


}
