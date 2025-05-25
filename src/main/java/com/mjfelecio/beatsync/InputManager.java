package com.mjfelecio.beatsync;

import com.mjfelecio.beatsync.core.Constants;
import javafx.scene.input.KeyCode;

public class InputManager {
    private boolean[] lanePressed = new boolean[Constants.NUM_LANES];

    public void press(KeyCode code) {
        switch (code) {
            case D -> setLanePressed(0, true);
            case F -> setLanePressed(1, true);
            case J -> setLanePressed(2, true);
            case K -> setLanePressed(3, true);
        }
    }

    public void release(KeyCode code) {
        switch (code) {
            case D -> setLanePressed(0, false);
            case F -> setLanePressed(1, false);
            case J -> setLanePressed(2, false);
            case K -> setLanePressed(3, false);
        }
    }

    public void setLanePressed(int lane, boolean state) {
        this.lanePressed[lane] = state;
    }

    public boolean isPressed(int lane) {
        return lanePressed[lane];
    }

    public boolean[] getLanePressed() {
        return lanePressed;
    }
}
