package com.mjfelecio.beatsync.audio;

import java.util.Objects;

public enum SoundEffect {
    HITSOUND("/com/mjfelecio/beatsync/sound-effects/hitsound.wav"),
    HOLDBREAK("/com/mjfelecio/beatsync/sound-effects/holdbreak.wav"),
    ENTER_GAMEPLAY("/com/mjfelecio/beatsync/sound-effects/enter-gameplay.wav"),
    BUTTON_HOVER("/com/mjfelecio/beatsync/sound-effects/button-hover.wav"),
    APPLAUSE("/com/mjfelecio/beatsync/sound-effects/applause.wav"),
    PLAY_BEATMAP("/com/mjfelecio/beatsync/sound-effects/play-beatmap.wav"),
    RESULTS_SWOOSH("/com/mjfelecio/beatsync/sound-effects/results-swoosh.wav"),
    SCENE_CHANGE("/com/mjfelecio/beatsync/sound-effects/scene-change.wav"),
    SELECT("/com/mjfelecio/beatsync/sound-effects/select.wav"),
    NOTIFICATION_SUCCESS("/com/mjfelecio/beatsync/sound-effects/notification-success.wav"),
    NOTIFICATION_INFO("/com/mjfelecio/beatsync/sound-effects/notification-info.wav"),
    NOTIFICATION_ERROR("/com/mjfelecio/beatsync/sound-effects/notification-error.wav");

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
