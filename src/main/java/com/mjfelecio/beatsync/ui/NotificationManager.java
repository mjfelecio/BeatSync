package com.mjfelecio.beatsync.ui;

import com.mjfelecio.beatsync.utils.FontProvider;
import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.util.EnumMap;
import java.util.Map;

public class NotificationManager {
    private static final VBox notificationBox = new VBox(10);
    private static final Map<Notification, String> colorMap = new EnumMap<>(Notification.class);

    static {
        colorMap.put(Notification.SUCCESS, "#0f0");
        colorMap.put(Notification.INFO, "#0ff");
        colorMap.put(Notification.ERROR, "#f00");

        notificationBox.setAlignment(Pos.TOP_RIGHT);
        notificationBox.setPickOnBounds(false);
        notificationBox.setMouseTransparent(true);
        notificationBox.setPadding(new Insets(20, 20, 0, 0)); // top-right margin
    }

    public static void showNotification(Scene scene, Notification type, String message) {
        if (!colorMap.containsKey(type)) {
            throw new IllegalArgumentException("Unknown notification type: " + type);
        }

        Label label = new Label(message);
        label.setFont(FontProvider.ARCADE_R.getFont(16));
        label.setStyle(
                "-fx-background-color: black;" +
                        "-fx-border-color: " + colorMap.get(type) + ";" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: #CCCCCC;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-padding: 10 20 10 20;"
        );

        StackPane wrapper = new StackPane(label);
        wrapper.setOpacity(0);
        notificationBox.getChildren().add(wrapper);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), wrapper);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Wait
        PauseTransition stay = new PauseTransition(Duration.seconds(2));

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), wrapper);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> notificationBox.getChildren().remove(wrapper));

        SequentialTransition sequence = new SequentialTransition(fadeIn, stay, fadeOut);
        sequence.play();

        StackPane root = (StackPane) scene.getRoot();

        if (!root.getChildren().contains(notificationBox)) {
            StackPane.setAlignment(notificationBox, Pos.TOP_RIGHT);
            root.getChildren().add(notificationBox);
        }
    }
}
