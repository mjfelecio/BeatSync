package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.core.GameState;
import com.mjfelecio.beatsync.core.NoteManager;
import com.mjfelecio.beatsync.parser.obj.Beatmap;

public class GameplayLogic {
    private final GameState gameState;
    private NoteManager noteManager;

    public GameplayLogic(GameState gameState) {
        this.gameState = gameState;
    }

    public void loadBeatmap(Beatmap beatmap) {
        this.noteManager = new NoteManager(beatmap.getNotes());
    }

    public void update(long currentTime, long deltaTime) {
        noteManager.updateNotesPosition(currentTime);
        // Remove notes that are too far past
        noteManager.cullExpiredNotes(currentTime);
    }

}