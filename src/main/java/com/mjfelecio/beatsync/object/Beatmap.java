package com.mjfelecio.beatsync.object;

import java.util.ArrayList;
import java.util.List;

// Represents a diff in a single beatmap such as Easy, Normal, etc.
public class Beatmap {
    private String title;
    private String diffName;
    private final List<Note> notes = new ArrayList<>();

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

    @Override
    public String toString() {
        return String.format("Title: %s, DiffName: %s, Notes: %d", title, diffName, notes.size());
    }
}