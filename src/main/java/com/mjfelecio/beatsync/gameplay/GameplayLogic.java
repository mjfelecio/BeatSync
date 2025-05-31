package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.state.GameState;
import com.mjfelecio.beatsync.judgement.JudgementProcessor;
import com.mjfelecio.beatsync.object.Note;
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
        noteManager.updateVisibleNotes(currentTime);
        noteManager.cullExpiredNotes(currentTime);
        noteManager.setMissCallback(this::handleMissedNote);
    }

    private void handleMissedNote(Note note) {
        processJudgement(JudgementResult.MISS);
    }

    public void handleLanePress(int laneNumber, long currentTime) {
        Note hitNote = noteManager.getHittableNote(laneNumber, currentTime);

        if (hitNote != null) {
            JudgementResult judgement = JudgementProcessor.judge(hitNote.getStartTime(), currentTime);
            processJudgement(judgement);

            if (judgement == JudgementResult.MISS) {
                hitNote.setMiss(true);
            } else if (hitNote.isHoldNote()) {
                hitNote.setHeld(true);
            } else {
                hitNote.setHit(true);
            }
        }
    }

    public void handleLaneRelease(int laneNumber, long currentTime) {
        Note note = noteManager.getHittableHeldNote(laneNumber, currentTime);

        if (note != null && note.isHeld()) {
            note.setHeld(false);
            JudgementResult tailJudgement = JudgementProcessor.judgeTail(note.getEndTime(), currentTime);
            processJudgement(tailJudgement);

            if (tailJudgement == JudgementResult.MISS) {
                note.setMiss(true);
            } else {
                note.setHit(true);
            }
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