package com.mjfelecio.beatsync.utils;

import javafx.scene.text.Font;

public enum FontProvider {
    ARCADE_R("/com/mjfelecio/beatsync/assets/fonts/ARCADE_R.TTF");

    private final String fontPath;

    FontProvider(String fontPath) {
        this.fontPath = fontPath;
    }

    public Font getFont(int size) {
        return Font.loadFont(getClass().getResourceAsStream(this.fontPath), size);
    }
}
