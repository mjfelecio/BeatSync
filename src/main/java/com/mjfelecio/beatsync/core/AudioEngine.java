package com.mjfelecio.beatsync.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioEngine {
    private Media music;
    private MediaPlayer mediaPlayer;

    public void setMusic(Media newMusic) {
        this.music = newMusic;
        this.mediaPlayer = new MediaPlayer(this.music);
    }

    public MediaPlayer getPlayer() {
        return mediaPlayer;
    }
}
