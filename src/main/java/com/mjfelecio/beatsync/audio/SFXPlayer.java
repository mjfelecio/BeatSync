package com.mjfelecio.beatsync.audio;

import javafx.scene.media.AudioClip;

import java.util.EnumMap;

public class SFXPlayer {
    private static SFXPlayer instance;

    private final EnumMap<SoundEffect, AudioClip> effects = new EnumMap<>(SoundEffect.class);
    private double volume = 1.0; // 0.0 to 1.0

    private SFXPlayer() {
        for (SoundEffect type : SoundEffect.values()) {
            load(type);
        }
    }

    public static SFXPlayer getInstance() {
        if (instance == null) {
            instance = new SFXPlayer();
        }
        return instance;
    }

    private void load(SoundEffect type) {
        AudioClip clip = new AudioClip(type.getUri());
        clip.setVolume(volume);
        effects.put(type, clip);
    }

    public void play(SoundEffect type) {
        AudioClip clip = effects.get(type);
        if (clip != null) {
            clip.play();
        }
    }

    public void setVolume(int volume) {
        this.volume = volume / 100.0;
        effects.values().forEach(clip -> clip.setVolume(this.volume));
    }

    public double getVolume() {
        return volume;
    }
}
