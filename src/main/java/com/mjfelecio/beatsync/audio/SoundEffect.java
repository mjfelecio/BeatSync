package com.mjfelecio.beatsync.audio;

import java.util.Objects;

public enum SoundEffect {
    HITSOUND("/com/mjfelecio/beatsync/sound-effects/hitsound.wav"),
    HOLDBREAK("/com/mjfelecio/beatsync/sound-effects/holdbreak.wav"),
    BUTTON_SELECT("/com/mjfelecio/beatsync/sound-effects/button-select.wav"),
    BUTTON_HOVER("/com/mjfelecio/beatsync/sound-effects/button-hover.wav"),
    APPLAUSE("/com/mjfelecio/beatsync/sound-effects/applause.wav"),
    NOTIFICATION("/com/mjfelecio/beatsync/sound-effects/notification.wav"),
    PLAY_BEATMAP("/com/mjfelecio/beatsync/sound-effects/play-beatmap.wav"),
    RESULTS_SWOOSH("/com/mjfelecio/beatsync/sound-effects/results-swoosh.wav"),
    SCENE_CHANGE("/com/mjfelecio/beatsync/sound-effects/scene-change.wav"),
    SELECT("/com/mjfelecio/beatsync/sound-effects/select.wav");

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
