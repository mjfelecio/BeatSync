package com.mjfelecio.beatsync.core;

import com.mjfelecio.beatsync.parser.obj.Beatmap;

import java.io.File;
import java.util.ArrayList;

public class BeatmapParser {
    private Beatmap beatmap;

    public BeatmapParser(Beatmap beatmap) {
        this.beatmap = beatmap;
    }

    public void parse() {

    }

    public ArrayList<Note> parseNotes() {
        ArrayList<Note> notes = new ArrayList<>();

        this.beatmap.getNotes().forEach(h -> {
            if (h.isHold()) {
                notes.add(new Note(h.getX(), h.getTime(), h.getEndTime()));
                return;
            }

            notes.add(new Note(h.getX(), h.getTime()));
        });

        return notes;
    }


}
