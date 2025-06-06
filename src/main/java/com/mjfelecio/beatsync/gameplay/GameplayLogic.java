package com.mjfelecio.beatsync.gameplay;

import com.mjfelecio.beatsync.audio.AudioType;
import com.mjfelecio.beatsync.audio.SoundManager;
import com.mjfelecio.beatsync.judgement.ScoreManager;
import com.mjfelecio.beatsync.object.Beatmap;
import com.mjfelecio.beatsync.judgement.JudgementProcessor;
import com.mjfelecio.beatsync.object.Note;
import com.mjfelecio.beatsync.judgement.JudgementResult;

import java.util.List;

public class GameplayLogic {
    private final GameSession gameSession;
    private NoteManager noteManager;
    private ScoreManager scoreManager;

    public GameplayLogic(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public void loadBeatmap(Beatmap beatmap) {
        this.noteManager = new NoteManager(beatmap.getNotes());
        this.scoreManager = new ScoreManager(beatmap.getRegularNoteCount(), beatmap.getHoldNoteCount());
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
                // If a tail was missed, play this hold break sfx
                SoundManager.getInstance().play(AudioType.HOLDBREAK);
            } else {
                note.setHit(true);
            }
        }
    }

    private void processJudgement(JudgementResult judgementResult) {
        switch (judgementResult) {
            case PERFECT -> {
                gameSession.incrementCombo();
                gameSession.incrementPerfectCount();
            }
            case GREAT -> {
                gameSession.incrementCombo();
                gameSession.incrementGreatCount();
            }
            case MEH -> {
                gameSession.incrementCombo();
                gameSession.incrementMehCount();
            }
            case JudgementResult.MISS -> {
                gameSession.resetCombo();
                gameSession.incrementMissCount();
            }
        }

        scoreManager.registerJudgement(judgementResult);

        // Store the results in the current session
        gameSession.setScore(scoreManager.getScore());
        gameSession.setAccuracy(scoreManager.getAccuracy());
        gameSession.setJudgement(judgementResult.toString());
        gameSession.setMaxCombo(scoreManager.getMaxScore());
        gameSession.setRank(scoreManager.getRank());
    }

    public List<Note> getVisibleNotes() {
        return noteManager.getVisibleNotes();
    }
}