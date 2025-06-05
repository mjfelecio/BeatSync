package com.mjfelecio.beatsync.audio;

import java.util.Objects;

public enum AudioType {
    HITSOUND("/com/mjfelecio/beatsync/audio/hitsound.wav");

    private final String uri;

    AudioType(String resourcePath) {
        this.uri = Objects.requireNonNull(
                AudioType.class.getResource(resourcePath),
                "Missing audio resource: " + resourcePath
        ).toExternalForm();
    }

    public String getUri() {
        return uri;
    }
}
