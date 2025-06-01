package com.mjfelecio.beatsync.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
     *  - title, artist, creator (from the first .osu file’s [Metadata] section)
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

                    // Extract metadata (title, artist, creator) from the first .osu we encounter
                    if (beatmap.getTitle() == null) {
                        beatmap.setTitle(extractMetadataField(f, "Title"));
                        beatmap.setArtist(extractMetadataField(f, "Artist"));
                        beatmap.setCreator(extractMetadataField(f, "Creator"));
                    }
                }
            }
        }

        // Attach the list of parsed difficulties (could be empty if no .osu files found)
        beatmap.setDifficulties(difficulties);

        return beatmap;
    }

    /**
     * Reads the given .osu file and returns the value of the specified metadata key
     * (e.g., "Title", "Artist", or "Creator") from its [Metadata] section.
     *
     * @param osuFile   the .osu file to read
     * @param fieldName the metadata key to extract (exactly as it appears, e.g., "Title")
     * @return the metadata value, or an empty string if not found
     * @throws IOException if reading the file fails
     */
    private static String extractMetadataField(File osuFile, String fieldName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(osuFile))) {
            String line;
            boolean inMetadataSection = false;
            String prefix = fieldName + ":";

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!inMetadataSection) {
                    if (line.equalsIgnoreCase("[Metadata]")) {
                        inMetadataSection = true;
                    }
                    continue;
                }

                // If we've reached another section, stop looking
                if (line.startsWith("[") && line.endsWith("]") && !line.equalsIgnoreCase("[Metadata]")) {
                    break;
                }

                if (line.startsWith(prefix)) {
                    // Extract everything after "FieldName:" (skip any leading whitespace)
                    String value = line.substring(prefix.length()).trim();
                    return value;
                }
            }
        }

        // Return empty if the field wasn't found
        return "";
    }
}
