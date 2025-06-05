package com.mjfelecio.beatsync.audio;

import javafx.scene.media.AudioClip;

import java.util.EnumMap;

public class SoundManager {
    private static SoundManager instance;

    private final EnumMap<AudioType, AudioClip> effects = new EnumMap<>(AudioType.class);
    private double volume = 1.0; // 0.0 to 1.0

    private SoundManager() {
        for (AudioType type : AudioType.values()) {
            load(type);
        }
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void load(AudioType type) {
        AudioClip clip = new AudioClip(type.getUri());
        clip.setVolume(volume);
        effects.put(type, clip);
    }

    public void play(AudioType type) {
        AudioClip clip = effects.get(type);
        if (clip != null) {
            clip.play();
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
        effects.values().forEach(clip -> clip.setVolume(volume));
    }

    public double getVolume() {
        return volume;
    }
}
