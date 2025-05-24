package com.mjfelecio.beatsync.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioEngine {
    private Media music;

    public void setMusic(Media newMusic) {
        this.music = newMusic;
    }

    public MediaPlayer getPlayer() {
        return new MediaPlayer(music);
    }
}
