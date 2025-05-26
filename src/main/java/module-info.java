module com.mjfelecio.beatsync {
    requires javafx.controls;
    requires javafx.media;
    requires OsuBeatmapStudio;

    exports com.mjfelecio.beatsync;
    exports com.mjfelecio.beatsync.core;
    exports com.mjfelecio.beatsync.config;
    exports com.mjfelecio.beatsync.input;
    exports com.mjfelecio.beatsync.parser.obj;
}