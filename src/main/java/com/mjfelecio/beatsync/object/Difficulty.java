package com.mjfelecio.beatsync.object;

import java.util.ArrayList;
import java.util.List;

// Represents a diff in a single beatmap such as Easy, Normal, etc.
public class Difficulty {
    private String title;
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

    @Override
    public String toString() {
        return String.format("Title: %s, Notes: %d", title, notes.size());
    }
}