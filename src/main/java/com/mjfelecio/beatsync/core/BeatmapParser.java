package com.mjfelecio.beatsync.core;

import org.hydev.obp.Beatmap;

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

        this.beatmap.getObjects().forEach(h -> {
            notes.add(new Note(h.getX(), h.getTime()));
        });

        return notes;
    }


}
