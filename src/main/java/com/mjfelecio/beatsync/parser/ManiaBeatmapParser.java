package com.mjfelecio.beatsync.parser;

import com.mjfelecio.beatsync.parser.obj.Beatmap;
import com.mjfelecio.beatsync.parser.obj.HitObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

                // Metadata
                if (section == Section.METADATA) {
                    if (line.startsWith("Title:")) beatmap.setTitle(line.substring(6).trim());
                    if (line.startsWith("Artist:")) beatmap.setArtist(line.substring(7).trim());
                    if (line.startsWith("Version:")) beatmap.setVersion(line.substring(8).trim());
                    if (line.startsWith("Creator:")) beatmap.setCreator(line.substring(8).trim());
                }

                // HitObjects
                if (section == Section.HIT_OBJECTS) {
                    String[] parts = line.split(",");
                    if (parts.length < 5) continue;

                    try {
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);
                        int time = Integer.parseInt(parts[2]);
                        int type = Integer.parseInt(parts[3]);
                        int hitSound = Integer.parseInt(parts[4]);

                        Integer endTime = getEndTime(type, parts);

                        beatmap.addNote(new HitObject(x, y, time, type, hitSound, endTime));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number: " + e.getMessage());
                    }
                }
            }
        }

        return beatmap;
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
