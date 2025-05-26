package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.core.GameState;
import com.mjfelecio.beatsync.core.NoteManager;
import com.mjfelecio.beatsync.parser.obj.Beatmap;

import java.util.List;

public class GameplayLogic {
    private final GameState gameState;
    private NoteManager noteManager;

    public GameplayLogic(GameState gameState) {
        this.gameState = gameState;
    }

    public void loadBeatmap(Beatmap beatmap) {
        this.noteManager = new NoteManager(beatmap.getNotes());
    }

}