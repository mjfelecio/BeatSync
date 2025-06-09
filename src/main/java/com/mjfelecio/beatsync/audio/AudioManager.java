package com.mjfelecio.beatsync.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {
    private MediaPlayer player;

    public MediaPlayer getPlayer() {
        return player;
    }

    public void loadMusic(String audioPath) {
        Media music = new Media(audioPath);
        this.player = new MediaPlayer(music);
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

    public void setVolume(int volume) {
        player.setVolume(volume);
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
