package com.mjfelecio.beatsync.object;

import java.util.ArrayList;
import java.util.List;

// Represents a diff in a single beatmap such as Easy, Normal, etc.
public class Beatmap {
    private String title;
    private String diffName;
    private int beatmapID;
    private String audioPath;
    private final List<Note> notes = new ArrayList<>();
    private int regularNoteCount;
    private int holdNoteCount;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }

    public String getDiffName() {
        return diffName;
    }

    public void setDiffName(String diffName) {
        this.diffName = diffName;
    }

    public int getBeatmapID() {
        return beatmapID;
    }

    public void setBeatmapID(int beatmapID) {
        this.beatmapID = beatmapID;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public int getRegularNoteCount() {
        return regularNoteCount;
    }

    public int getHoldNoteCount() {
        return holdNoteCount;
    }

    public void setRegularNoteCount(int regularNoteCount) {
        this.regularNoteCount = regularNoteCount;
    }

    public void setHoldNoteCount(int holdNoteCount) {
        this.holdNoteCount = holdNoteCount;
    }

    @Override
    public String toString() {
        return String.format("Title: %s, DiffName: %s, Notes: %d", title, diffName, notes.size());
    }
}