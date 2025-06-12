package com.mjfelecio.beatsync.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Make sure these classes exist in your project:
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.BeatmapSet;
import javafx.scene.media.Media;
import javafx.util.Duration;

public class BeatmapSetParser {

    /**
     * Parses an extracted .osz folder and returns a BeatmapSet object populated with:
     *  - audioPath (first .mp3 found)
     *  - imagePath (first .jpg/.png/.jpeg found)
     *  - a List<Beatmap> (one Beatmap per .osu file, via BeatmapParser.parse)
     *  - title, artist, creator (from the first .osu file’s [Metadata] section)
     *
     * @param beatmapSetFolder directory corresponding to an extracted .osz archive
     * @return BeatmapSet
     * @throws IOException if any file‐IO operation fails
     */
    public static BeatmapSet parse(File beatmapSetFolder) throws IOException {
        if (beatmapSetFolder == null || !beatmapSetFolder.isDirectory()) {
            throw new IllegalArgumentException("beatmapSetFolder must be a valid directory");
        }

        BeatmapSet beatmapSet = new BeatmapSet();
        List<Beatmap> difficulties = new ArrayList<>();

        // Iterate over every file in the folder
        File[] files = beatmapSetFolder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (!f.isFile()) {
                    continue;
                }

                String nameLower = f.getName().toLowerCase();

                // If it's an .mp3 → audioPath
                if (nameLower.endsWith(".mp3")) {
                    beatmapSet.setAudioPath(f.getAbsolutePath());
                    continue;
                }

                // If it's an image (.jpg/.jpeg/.png) → imagePath
                if (nameLower.endsWith(".jpg") || nameLower.endsWith(".jpeg") || nameLower.endsWith(".png")) {
                    beatmapSet.setImagePath(new File(f.getAbsolutePath()).toURI().toASCIIString());
                    continue;
                }

                // If it's a .osu file → parse to Beatmap and add to list
                if (nameLower.endsWith(".osu")) {
                    try {
                        Beatmap diff = BeatmapParser.parse(f);
                        difficulties.add(diff);

                        // Extract metadata (title, artist, creator) from the first .osu we encounter
                        if (beatmapSet.getTitle() == null) {
                            beatmapSet.setTitle(extractMetadataField(f, "Title"));
                            beatmapSet.setArtist(extractMetadataField(f, "Artist"));
                            beatmapSet.setCreator(extractMetadataField(f, "Creator"));
                            beatmapSet.setBeatmapSetID(Integer.parseInt(extractMetadataField(f, "BeatmapSetID")));
                        }
                    } catch (Exception e) {
                        System.err.println("Skipping invalid .osu file: " + f.getName());
                    }
                }
            }
        }

        // Attach the list of parsed difficulties (could be empty if no .osu files found)
        beatmapSet.setDifficulties(difficulties);

        // Supply the beatmaps in the set with the imagePath
        for (Beatmap beatmap: beatmapSet.getDifficulties()) {
            beatmap.setImagePath(beatmapSet.getImagePath());
        }

        return beatmapSet;
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
