package com.mjfelecio.beatsync.parser.obj;

import com.mjfelecio.beatsync.core.Note;

import java.util.ArrayList;
import java.util.List;

public class Beatmap {
    private String title;
    private String artist;
    private String version;
    private String creator;
    private List<Note> notes = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getVersion() {
        return version;
    }

    public String getCreator() {
        return creator;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void addNote(Note note) {
        this.notes.add(note);
    }

    @Override
    public String toString() {
        return String.format("Title: %s, Artist: %s, Version: %s, Creator: %s, Notes: %d",
                title, artist, version, creator, notes.size());
    }
}