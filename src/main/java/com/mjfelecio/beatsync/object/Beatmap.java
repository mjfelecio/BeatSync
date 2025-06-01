package com.mjfelecio.beatsync.object;

import java.util.List;

public class Beatmap {
    private String title;
    private String artist;
    private String creator;
    // I'm gonna assume that image and audio is the same for all difficulties for now
    // Handling edge cases will be difficult, so I'mma be handpicking maps to present
    // in order to not show those edge cases lol
    private String imagePath;
    private String audioPath;
    private List<Difficulty> difficulties;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getCreator() {
        return creator;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public List<Difficulty> getDifficulties() {
        return difficulties;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void setDifficulties(List<Difficulty> difficulties) {
        this.difficulties = difficulties;
    }

    @Override
    public String toString() {
        return String.format("Title: %s, Artist: %s, Creator: %s", title, artist, creator);
    }
}