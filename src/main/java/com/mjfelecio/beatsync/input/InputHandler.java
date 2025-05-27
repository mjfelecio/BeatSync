package com.mjfelecio.beatsync.input;

import com.mjfelecio.beatsync.gameplay.GameplayLogic;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class InputHandler {
    private final GameplayLogic gameplayLogic;
    private final InputState inputState;
    private final Map<KeyCode, Integer> keyToLaneMap;

    public InputHandler(GameplayLogic gameplayLogic) {
        this.gameplayLogic = gameplayLogic;
        this.inputState = new InputState();
        this.keyToLaneMap = createKeyMapping();
    }

    public void handleKeyPress(KeyCode keyCode, long currentTime) {
        Integer lane = keyToLaneMap.get(keyCode);
        if (lane != null) {
            inputState.pressLane(lane);
            gameplayLogic.handleLanePress(lane, currentTime);
        }
    }

    public void handleKeyRelease(KeyCode keyCode) {
        Integer lane = keyToLaneMap.get(keyCode);
        if (lane != null) {
            inputState.releaseLane(lane);
        }
    }

    private Map<KeyCode, Integer> createKeyMapping() {
        Map<KeyCode, Integer> mapping = new HashMap<>();
        mapping.put(KeyCode.D, 0);
        mapping.put(KeyCode.F, 1);
        mapping.put(KeyCode.J, 2);
        mapping.put(KeyCode.K, 3);
        return mapping;
    }

    public InputState getCurrentInput() { return inputState; }
}