package com.mjfelecio.beatsync.input;

import com.mjfelecio.beatsync.config.GameConfig;

public class InputState {
    private final boolean[] lanePressed = new boolean[GameConfig.NUM_LANES];
    private final long[] timePressed = new long[GameConfig.NUM_LANES];
    private final boolean[] pressConsumed = new boolean[GameConfig.NUM_LANES]; // Track if press was already detected

    public boolean isPressNotHold(int lane) {
        // Check if lane is currently pressed and we haven't consumed this press yet
        if (!lanePressed[lane] || pressConsumed[lane]) {
            return false;
        }

        final long HOLD_THRESHOLD_NANO = 100_000_000L; // 100ms in nanoseconds
        long timeElapsed = System.nanoTime() - timePressed[lane];

        // If it's a fresh press (very recent), mark as consumed and return true
        if (timeElapsed < HOLD_THRESHOLD_NANO) {
            pressConsumed[lane] = true;
            return true;
        }

        // If too much time has passed, it's now considered a hold
        return false;
    }

    public void pressLane(int lane) {
        if (lane < 0 || lane >= lanePressed.length) return;

        // Only update if it wasn't already pressed (prevents retriggering)
        if (!lanePressed[lane]) {
            lanePressed[lane] = true;
            timePressed[lane] = System.nanoTime();
            pressConsumed[lane] = false; // Reset consumption flag for new press
        }
    }

    public void releaseLane(int lane) {
        if (lane < 0 || lane >= lanePressed.length) return;

        lanePressed[lane] = false;
        timePressed[lane] = 0;
        pressConsumed[lane] = false; // Reset for next press
    }

    public boolean isLanePressed(int lane) {
        if (lane < 0 || lane >= lanePressed.length) return false;
        return lanePressed[lane];
    }
}