package com.mjfelecio.beatsync.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Make sure these classes exist in your project:
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.Difficulty;
import com.mjfelecio.beatsync.parser.DifficultyParser; // Will work on this later, but I am just assuming that it exists

public class BeatmapParser {

    /**
     * Parses an extracted .osz folder and returns a Beatmap object populated with:
     *  - audioPath (first .mp3 found)
     *  - imagePath (first .jpg/.png/.jpeg found)
     *  - a List<Difficulty> (one Difficulty per .osu file, via DifficultyParser.parse)
     *  - title, artist, creator (placeholder stubs for now)
     *
     * @param beatmapFolder directory corresponding to an extracted .osz archive
     * @return Beatmap
     * @throws IOException if any file‐IO operation fails
     */
    public static Beatmap parse(File beatmapFolder) throws IOException {
        if (beatmapFolder == null || !beatmapFolder.isDirectory()) {
            throw new IllegalArgumentException("beatmapFolder must be a valid directory");
        }

        Beatmap beatmap = new Beatmap();
        List<Difficulty> difficulties = new ArrayList<>();

        // Iterate over every file in the folder
        File[] files = beatmapFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isFile()) {
                    continue;
                }

                String nameLower = f.getName().toLowerCase();

                // If it's an .mp3 → audioPath
                if (nameLower.endsWith(".mp3")) {
                    beatmap.setAudioPath(f.getAbsolutePath());
                    continue;
                }

                // If it's an image (.jpg/.jpeg/.png) → imagePath
                if (nameLower.endsWith(".jpg") || nameLower.endsWith(".jpeg") || nameLower.endsWith(".png")) {
                    beatmap.setImagePath(f.getAbsolutePath());
                    continue;
                }

                // If it's a .osu file → parse to Difficulty and add to list
                if (nameLower.endsWith(".osu")) {
                    Difficulty diff = DifficultyParser.parse(f);
                    difficulties.add(diff);

                    // For now, just pick up placeholder metadata from the first .osu file seen
                    if (beatmap.getTitle() == null) {
                        beatmap.setTitle(extractTitlePlaceholder(f));
                        beatmap.setArtist(extractArtistPlaceholder(f));
                        beatmap.setCreator(extractCreatorPlaceholder(f));
                    }
                }
            }
        }

        // Attach the list of parsed difficulties (could be empty if no .osu files found)
        beatmap.setDifficulties(difficulties);

        return beatmap;
    }

    private static String extractTitlePlaceholder(File osuFile) {
        // TODO: open osuFile, read “[Metadata]” section, extract “Title: …”
        return "";
    }

    private static String extractArtistPlaceholder(File osuFile) {
        // TODO: open osuFile, read “[Metadata]” section, extract “Artist: …”
        return "";
    }

    private static String extractCreatorPlaceholder(File osuFile) {
        // TODO: open osuFile, read “[Metadata]” section, extract “Creator: …”
        return "";
    }
}
