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
            int laneNumber = getLaneNumber(h.getX());
            if (h.isHold()) {
                notes.add(new Note(laneNumber, h.getTime(), h.getEndTime()));
                return;
            }

            notes.add(new Note(laneNumber, h.getTime()));
        });

        return notes;
    }

    public int getLaneNumber(int x) {
        int lane = -1;
        switch (x) {
            case 64 -> lane = 0;
            case 192 -> lane = 1;
            case 320 -> lane = 2;
            case 448 -> lane = 3;
        }

        return lane;
    }


}
