package com.mjfelecio.beatsync.parser;

import com.mjfelecio.beatsync.config.GameConfig;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.object.Note;
import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

 /* See: https://osu.ppy.sh/wiki/en/Client/File_formats/osu_(file_format)
     I probably need:
    [General]
        AudioFilename
        AudioLeadIn
        Stack Leniency
        Mode (Reject map if it isn't osu mania)

    [Metadata]
        Title
        Artist
        Creator
        Version
        BeatmapID
        BeatmapSetID

    [Beatmap]
        Approach Rate (Maybe, needs more research)

    [Events]
        Breaks

    [TimingPoints]

    [HitObjects] - ALL
    * */

public class BeatmapParser {
    private enum Section {
        NONE,
        GENERAL,
        METADATA,
        DIFFICULTY,
        EVENTS,
        TIMING_POINTS,
        HIT_OBJECTS
    }

    public static Beatmap parse(File file) throws IOException {
        Beatmap beatmap = new Beatmap();
        Section section = Section.NONE;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("//")) continue;

                // Section headers
                if (line.startsWith("[") && line.endsWith("]")) {
                    section = switch (line) {
                        case "[General]" -> Section.GENERAL;
                        case "[Metadata]" -> Section.METADATA;
                        case "[Beatmap]" -> Section.DIFFICULTY;
                        case "[Events]" -> Section.EVENTS;
                        case "[TimingPoints]" -> Section.TIMING_POINTS;
                        case "[HitObjects]" -> Section.HIT_OBJECTS;
                        default -> Section.NONE;
                    };
                    continue;
                }

                // General
                if (section == Section.GENERAL) parseGeneral(beatmap, line, file);

                // Metadata
                if (section == Section.METADATA) parseMetaData(beatmap, line);

                // HitObjects
                if (section == Section.HIT_OBJECTS) parseHitObjects(beatmap, line);
            }

            // Get note count
            Map<Boolean, Long> noteCounts = beatmap.getNotes().stream()
                    .collect(Collectors.groupingBy(
                            Note::isHoldNote,
                            Collectors.counting()
                    ));
            beatmap.setHoldNoteCount(Math.toIntExact(noteCounts.getOrDefault(true, 0L)));
            beatmap.setRegularNoteCount(Math.toIntExact(noteCounts.getOrDefault(false, 0L)));
        } catch (IOException e) {
            throw new IOException("Failed to parse beatmap: " + e);
        }

        // Get the duration of the song
        Duration songDuration = new Media(beatmap.getAudioPath()).getDuration();
        beatmap.setAudioLength(songDuration);

        return beatmap;
    }

    private static void parseGeneral(Beatmap beatmap, String line, File file) throws IOException {
        if (line.startsWith("AudioFilename:")) {
            String audioFileName = line.substring(14).trim();
            Path audioPath = Paths.get(file.getParent(), audioFileName);

            // ALERT, major hack right here haha. Because we are converting .ogg to .mp3
            // We need to make sure that the audio path points to the mp3 and not the .ogg
            // that has already been deleted after conversion
            if (audioFileName.endsWith(".ogg")) {
                // Construct the new file name with ".mp3" extension
                String newAudioFileName = audioFileName.substring(0, audioFileName.length() - 4) + ".mp3";
                audioPath = Paths.get(file.getParent(), newAudioFileName);
            }

            beatmap.setAudioPath(audioPath.toUri().toASCIIString());
        }
        if (line.startsWith("Mode:")) {
            boolean isMania = line.substring(5).trim().equals("3");
            if (!isMania) throw new IOException("Only mania beatmaps are supported");
        }
    }

    private static void parseMetaData(Beatmap beatmap, String line) {
        if (line.startsWith("Title:")) beatmap.setTitle(line.substring(6).trim());
        if (line.startsWith("Version:")) beatmap.setDiffName(line.substring(8).trim());
        if (line.startsWith("BeatmapID:")) beatmap.setBeatmapID(Integer.parseInt(line.substring(10).trim()));
        if (line.startsWith("Artist:")) beatmap.setArtist(line.substring(7).trim());
        if (line.startsWith("Creator:")) beatmap.setCreator(line.substring(8).trim());
    }

    private static void parseHitObjects(Beatmap beatmap, String line) throws IOException {
        String[] parts = line.split(",");
        if (parts.length < 5) return;

        try {
            int x = Integer.parseInt(parts[0]);
//            int y = Integer.parseInt(parts[1]); // Don't need this as well
            int time = Integer.parseInt(parts[2]);
            int type = Integer.parseInt(parts[3]);
//            int hitSound = Integer.parseInt(parts[4]); // I don't need hit sound for now

            int laneNumber = calculateLaneNumber(x);

            // If the lane number is not being mapped correctly, that means it's a 5k+ beatmap
            // Which we currently can't support, so just throw an error
            if (laneNumber > 4) {
                throw new IOException("Only 4K beatmaps supported");
            }

            Integer endTime = getEndTime(type, parts);

            beatmap.addNote(endTime == null ? new Note(laneNumber, time) : new Note(laneNumber, time, endTime));
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + e.getMessage());
        }
    }

    private static int calculateLaneNumber(int x) {
        return (int) Math.floor((double) (x * GameConfig.NUM_LANES) / 512);
    }

    private static Integer getEndTime(int type, String[] parts) {
        Integer endTime = null;
        boolean isHoldNote = (type & 128) != 0;

        if (isHoldNote && parts.length > 5) {
            // parts[5] is the "hitSample" field, I don't really need it for now
            String hitSample = parts[5];
            String[] hitSampleParts = hitSample.split(":", -1);
            if (hitSampleParts.length >= 1 && !hitSampleParts[0].isEmpty()) {
                try {
                    endTime = Integer.parseInt(hitSampleParts[0]);
                } catch (NumberFormatException ignored) {}
            }
        }
        return endTime;
    }
}
