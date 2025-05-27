package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.core.GameState;
import com.mjfelecio.beatsync.judgement.JudgementProcessor;
import com.mjfelecio.beatsync.object.Note;
import com.mjfelecio.beatsync.core.NoteManager;
import com.mjfelecio.beatsync.judgement.JudgementResult;
import com.mjfelecio.beatsync.object.Beatmap;

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

    public void update(long currentTime, long deltaTime) {
        noteManager.updateNotesPosition(currentTime);
        // Remove notes that are too far past
        noteManager.cullExpiredNotes(currentTime);
    }

    public void handleLanePress(int laneNumber, long currentTime) {
        Note hitNote = noteManager.getHittableNote(laneNumber, currentTime);
        if (hitNote != null) {
            JudgementResult judgement = JudgementProcessor.judge(hitNote, currentTime);
            processJudgement(judgement);
            hitNote.setHit(true);
        }
    }

    private void processJudgement(JudgementResult judgementResult) {
        switch (judgementResult) {
            case JudgementResult.PERFECT, JudgementResult.GOOD -> {
                gameState.incrementCombo();
//                gameState.addScore(100); // will think about the scoring system later
            }
            case JudgementResult.MISS -> {
                gameState.resetCombo();
            }
        }
        gameState.setJudgement(judgementResult.toString());
    }

    public List<Note> getVisibleNotes() {
        return noteManager.getVisibleNotes();
    }
}