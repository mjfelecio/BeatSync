package com.mjfelecio.beatsync.audio;

import java.util.Objects;

public enum SoundEffect {
    HITSOUND("/com/mjfelecio/beatsync/audio/hitsound.wav"),
    HOLDBREAK("/com/mjfelecio/beatsync/audio/holdbreak.wav");

    private final String uri;

    SoundEffect(String resourcePath) {
        this.uri = Objects.requireNonNull(
                SoundEffect.class.getResource(resourcePath),
                "Missing audio resource: " + resourcePath
        ).toExternalForm();
    }

    public String getUri() {
        return uri;
    }
}
