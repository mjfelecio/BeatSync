module com.mjfelecio.beatsync {
    requires javafx.controls;
    requires javafx.media;
    requires java.sql;

    exports com.mjfelecio.beatsync;
    exports com.mjfelecio.beatsync.core;
    exports com.mjfelecio.beatsync.config;
    exports com.mjfelecio.beatsync.input;
    exports com.mjfelecio.beatsync.gameplay;
    exports com.mjfelecio.beatsync.object;
    exports com.mjfelecio.beatsync.parser;
    exports com.mjfelecio.beatsync.state;
    exports com.mjfelecio.beatsync.rendering;
    exports com.mjfelecio.beatsync.audio;
    exports com.mjfelecio.beatsync.judgement;
    exports com.mjfelecio.beatsync.ui;
    exports com.mjfelecio.beatsync.utils;
}