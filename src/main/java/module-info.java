module com.mjfelecio.beatsync {
    requires javafx.controls;
    requires javafx.fxml;
    requires OsuBeatmapStudio;


    opens com.mjfelecio.beatsync to javafx.fxml;
    exports com.mjfelecio.beatsync;
}