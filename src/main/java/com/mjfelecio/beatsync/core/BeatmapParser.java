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
            int laneNumber = getLaneNumber(h.getX());
            notes.add(new Note(laneNumber, h.getTime()));
        });

        return notes;
    }

    public int getLaneNumber(int x) {
        int lane = 0;
        switch (x) {
            case 64 -> lane = 1;
            case 192 -> lane = 2;
            case 320 -> lane = 3;
            case 448 -> lane = 4;
        }

        return lane;
    }
}
