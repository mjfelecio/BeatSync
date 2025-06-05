package com.mjfelecio.beatsync.audio;

import javafx.scene.media.AudioClip;

import java.util.Objects;


public enum SoundEffect {
    HIT_SOUND("/com/mjfelecio/beatsync/audio/hitsound.wav");

    private final String uri;
    private AudioClip clip;

    SoundEffect(String resourcePath) {
        this.uri = Objects.requireNonNull(
                SoundEffect.class.getResource(resourcePath),
                "Missing audio resource: " + resourcePath
        ).toExternalForm();
    }

    public AudioClip getClip() {
        if (clip == null) {
            clip = new AudioClip(uri);
        }
        return clip;
    }
}
