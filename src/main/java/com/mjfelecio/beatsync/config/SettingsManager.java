package com.mjfelecio.beatsync.config;

import com.mjfelecio.beatsync.judgement.JudgementMode;

public class SettingsManager {
    private static final SettingsManager instance = new SettingsManager();

    private int scrollSpeed;
    private JudgementMode judgementMode;
    private int musicVolume;
    private int effectsVolume;

    private SettingsManager() {
        // Default settings
        this.scrollSpeed = 1000;
        this.judgementMode = JudgementMode.NORMAL;
        this.musicVolume = 100;
        this.effectsVolume = 100;
    }

    public static SettingsManager getInstance() {
        return instance;
    }


    public int getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public JudgementMode getJudgementMode() {
        return judgementMode;
    }

    public void setJudgementMode(JudgementMode judgementMode) {
        this.judgementMode = judgementMode;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public int getEffectsVolume() {
        return effectsVolume;
    }

    public void setEffectsVolume(int effectsVolume) {
        this.effectsVolume = effectsVolume;
    }
}
