package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.object.BeatmapSet;
import com.mjfelecio.beatsync.parser.BeatmapSetParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatmapLoader {
    private static BeatmapLoader instance = new BeatmapLoader(GameConfig.BEATMAP_DIRECTORY);

    private List<BeatmapSet> beatmapSets;       // Parsed BeatmapSet objects
    private List<File> beatmapFiles;      // Each File is a folder extracted from a .osz

    /**
     * Private constructor reads all folder‐paths under the configured directory.
     */
    private BeatmapLoader(String beatmapsDirectoryPath) {
        try {
            this.beatmapFiles = getBeatmapFiles(beatmapsDirectoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to list beatmap folders in: " + beatmapsDirectoryPath, e);
        }
        this.beatmapSets = new ArrayList<>();
    }

    /**
     * Singleton access to the BeatmapLoader.
     */
    public static BeatmapLoader getInstance() {
        return instance;
    }

    /**
     * Scans the root beatmap directory for subfolders (each representing an extracted .osz).
     *
     * @param beatmapsDirectoryPath path to the directory containing extracted beatmap folders
     * @return a list of File objects (each is a folder under beatmapsDirectoryPath)
     * @throws IOException if the directory is invalid or empty
     */
    private List<File> getBeatmapFiles(String beatmapsDirectoryPath) throws IOException {
        List<File> beatmapFiles = new ArrayList<>();
        File beatmapsDirectory = new File(beatmapsDirectoryPath);

        if (beatmapsDirectory.exists() && beatmapsDirectory.isDirectory()) {
            File[] files = beatmapsDirectory.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        beatmapFiles.add(f);
                    }
                }
                if (beatmapFiles.isEmpty()) {
                    throw new IOException("No beatmap folders found in directory: " + beatmapsDirectoryPath);
                }
            } else {
                throw new IOException("No files found in beatmap directory: " + beatmapsDirectoryPath);
            }
        } else {
            throw new IOException("BeatmapSet directory does not exist or is not a directory: " + beatmapsDirectoryPath);
        }

        return beatmapFiles;
    }

    /**
     * Loads (parses) all beatmap folders into BeatmapSet objects and stores them in an internal list.
     * Should be called once at startup (or whenever we want to refresh the library, like if importing is implemented).
     */
    public static void load() {
        BeatmapLoader loader = getInstance();
        loader.beatmapSets.clear();

        for (File folder : loader.beatmapFiles) {
            try {
                // Use BeatmapSetParser.parse(...) to convert a folder → BeatmapSet
                BeatmapSet bm = BeatmapSetParser.parse(folder);
                loader.beatmapSets.add(bm);
            } catch (IOException e) {
                // If any folder fails to parse, print a stack trace and continue with others
                System.err.println("Failed to parse beatmap folder: " + folder.getName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the full list of loaded BeatmapSet objects.
     * Call load() first to populate this list.
     */
    public List<BeatmapSet> getAllBeatmaps() {
        return new ArrayList<>(beatmapSets);
    }

    /**
     * Returns a BeatmapSet by its title (first match). Titles are retrieved from metadata.
     * Returns null if none matches.
     */
    public BeatmapSet getBeatmapByTitle(String title) {
        if (title == null) return null;
        for (BeatmapSet bm : beatmapSets) {
            if (title.equalsIgnoreCase(bm.getTitle())) {
                return bm;
            }
        }
        return null;
    }

    /**
     * Returns a BeatmapSet by its zero-based index in the internal list.
     * Throws IndexOutOfBoundsException if idx is invalid.
     */
    public BeatmapSet getBeatmapByIndex(int idx) {
        return beatmapSets.get(idx);
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // Experimental/Future Methods
    // ────────────────────────────────────────────────────────────────────────────────

    /**
     * (Experimental) Reloads the beatmap file list from disk, then re-runs load() to re-parse.
     */
    public void reloadFromDisk() {
        try {
            this.beatmapFiles = getBeatmapFiles(GameConfig.BEATMAP_DIRECTORY);
        } catch (IOException e) {
            System.err.println("Failed to refresh beatmap folder list from disk: " + e.getMessage());
        }
        load();
    }

    /**
     * (Experimental) Finds all Beatmaps whose artist matches the given name (case‐insensitive).
     */
    public List<BeatmapSet> getBeatmapsByArtist(String artist) {
        List<BeatmapSet> matches = new ArrayList<>();
        if (artist == null) return matches;
        for (BeatmapSet bm : beatmapSets) {
            if (artist.equalsIgnoreCase(bm.getArtist())) {
                matches.add(bm);
            }
        }
        return matches;
    }

    /**
     * (Experimental) Returns the total number of loaded beatmapSets.
     */
    public int getTotalBeatmapCount() {
        return beatmapSets.size();
    }

    // ────────────────────────────────────────────────────────────────────────────────
    // End of Experimental Methods
    // ────────────────────────────────────────────────────────────────────────────────

    /**
     * Optional main method for testing the loader in isolation.
     */
    public static void main(String[] args) {
        // Ensure we load all beatmapSets before querying
        BeatmapLoader.load();

        System.out.println("Total beatmapSets loaded: " + getInstance().getTotalBeatmapCount());
        System.out.println("All beatmap titles:");
        for (BeatmapSet bm : getInstance().getAllBeatmaps()) {
            System.out.println(" - " + bm.getTitle());
        }

        String sampleTitle = "My Song Title";
        BeatmapSet found = getInstance().getBeatmapByTitle(sampleTitle);
        if (found != null) {
            System.out.println("Found beatmap: " + found.getTitle() + " by " + found.getArtist());
        } else {
            System.out.println("No beatmap found with title: " + sampleTitle);
        }
    }
}
