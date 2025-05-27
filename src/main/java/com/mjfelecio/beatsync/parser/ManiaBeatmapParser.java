package com.mjfelecio.beatsync.parser;

import com.mjfelecio.beatsync.object.Note;
import com.mjfelecio.beatsync.object.Beatmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    [Difficulty]
        Approach Rate (Maybe, needs more research)

    [Events]
        Breaks

    [TimingPoints]

    [HitObjects] - ALL
    * */

public class ManiaBeatmapParser {
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
                    switch (line) {
                        case "[General]": section = Section.GENERAL; break;
                        case "[Metadata]": section = Section.METADATA; break;
                        case "[Difficulty]": section = Section.DIFFICULTY; break;
                        case "[Events]": section = Section.EVENTS; break;
                        case "[TimingPoints]": section = Section.TIMING_POINTS; break;
                        case "[HitObjects]": section = Section.HIT_OBJECTS; break;
                        default: section = Section.NONE;
                    }
                    continue;
                }

                // General
                if (section == Section.GENERAL) parseGeneral(beatmap, line);

                // Metadata
                if (section == Section.METADATA) parseMetaData(beatmap, line);

                // HitObjects
                if (section == Section.HIT_OBJECTS) parseHitObjects(beatmap, line);
            }
        }

        return beatmap;
    }

    private static void parseGeneral(Beatmap beatmap, String line) {
        if (line.startsWith("AudioFilename:")) beatmap.setAudioPath(line.substring(14).trim());
    }

    private static void parseMetaData(Beatmap beatmap, String line) {
        if (line.startsWith("Title:")) beatmap.setTitle(line.substring(6).trim());
        if (line.startsWith("Artist:")) beatmap.setArtist(line.substring(7).trim());
        if (line.startsWith("Version:")) beatmap.setVersion(line.substring(8).trim());
        if (line.startsWith("Creator:")) beatmap.setCreator(line.substring(8).trim());
    }

    private static void parseHitObjects(Beatmap beatmap, String line) {
        String[] parts = line.split(",");
        if (parts.length < 5) return;

        try {
            int x = Integer.parseInt(parts[0]);
//            int y = Integer.parseInt(parts[1]); // Don't need this as well
            int time = Integer.parseInt(parts[2]);
            int type = Integer.parseInt(parts[3]);
//            int hitSound = Integer.parseInt(parts[4]); // I don't need hit sound for now

            int laneNumber = getLaneNumber(x);
            Integer endTime = getEndTime(type, parts);

            beatmap.addNote(endTime == null ? new Note(laneNumber, time) : new Note(laneNumber, time, endTime));
        } catch (NumberFormatException e) {
            System.err.println("Invalid number: " + e.getMessage());
        }
    }

    private static int getLaneNumber(int x) {
        int lane = -1;
        switch (x) {
            case 64 -> lane = 0;
            case 192 -> lane = 1;
            case 320 -> lane = 2;
            case 448 -> lane = 3;
        }

        return lane;
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
