package com.mjfelecio.beatsync.input;

import com.mjfelecio.beatsync.config.GameConfig;

public class InputState {
    private final boolean[] lanePressed = new boolean[GameConfig.NUM_LANES];

    public void pressLane(int lane) {
        setLanePressed(lane, true);
    }

    public void releaseLane(int lane) {
        setLanePressed(lane, false);
    }

    public void setLanePressed(int lane, boolean state) {
        if (lane < 0) return;
        this.lanePressed[lane] = state;
    }

    public boolean isPressed(int lane) {
        return lanePressed[lane];
    }

    public boolean[] getLanePressed() {
        return lanePressed;
    }
}
