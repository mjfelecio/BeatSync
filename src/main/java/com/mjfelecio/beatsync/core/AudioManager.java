package com.mjfelecio.beatsync.core;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {
    private Media currentMusic;
    private MediaPlayer player;

    public void setCurrentMusic(Media newMusic) {
        this.currentMusic = newMusic;
        this.player = new MediaPlayer(this.currentMusic);
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void loadMusic(String audioPath) {
        Media media = new Media(audioPath);
        setCurrentMusic(media);
    }

    public void play() {
        if (player != null) {
            player.play();
        }
    }

    public void resume() {
        play();
    }

    public void pause() {
        if (player != null) {
            player.pause();
        }
    }

    public void stop() {
        if (player != null) {
            player.stop();
        }
    }

    public long getCurrentTime() {
        if (player != null && player.getCurrentTime() != null) {
            return (long) player.getCurrentTime().toMillis();
        }
        return 0;
    }

    public boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void dispose() {
        if (player != null) {
            player.dispose();
        }
    }
}
