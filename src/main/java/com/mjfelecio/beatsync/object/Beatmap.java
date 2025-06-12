package com.mjfelecio.beatsync.object;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;

// Represents a diff in a single beatmap such as Easy, Normal, etc.
// TODO: We have a lot of properties here, rethink on whether they are actually needed and make sense
public class Beatmap {
    private String title;
    private String diffName;
    private String artist;
    private String creator;
    private Duration audioLength; // The length of the audio of the beatmap
    private int beatmapID;
    private String audioPath;
    private String imagePath;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Duration getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(Duration audioLength) {
        this.audioLength = audioLength;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void resetNotesState() {
        notes.forEach(n -> {
            n.setMiss(false);
            n.setHit(false);
            n.setHeld(false);
        });
    }

    @Override
    public String toString() {
        return String.format("Title: %s, DiffName: %s, Notes: %d", title, diffName, notes.size());
    }
}