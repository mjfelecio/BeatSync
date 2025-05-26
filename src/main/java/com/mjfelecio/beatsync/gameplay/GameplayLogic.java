//package com.mjfelecio.beatsync.gameplay;
//
//import com.mjfelecio.beatsync.core.GameState;
//import com.mjfelecio.beatsync.judgement.JudgementProcessor;
//import com.mjfelecio.beatsync.core.Note;
//import com.mjfelecio.beatsync.core.NoteManager;
//import com.mjfelecio.beatsync.parser.obj.Beatmap;
//
//import java.util.List;
//
//public class GameplayLogic {
//    private final GameState gameState;
//    private NoteManager noteManager;
//    private JudgementProcessor judgementSystem;
//
//    public GameplayLogic(GameState gameState) {
//        this.gameState = gameState;
//        this.judgementSystem = new JudgementProcessor();
//    }
//
//    public void loadBeatmap(Beatmap beatmap) {
//        this.noteManager = new NoteManager(beatmap.getNotes());
//    }
//
//    public void update(long currentTime, long deltaTime) {
//        noteManager.updateNotesPosition(currentTime);
//        // Remove notes that are too far past
//        noteManager.cullExpiredNotes(currentTime);
//    }
//
//    public void handleLanePress(int laneNumber, long currentTime) {
//        Note hitNote = noteManager.getHittableNote(laneNumber, currentTime);
//        if (hitNote != null) {
//            String judgement = judgementSystem.judge(hitNote, currentTime);
//            processJudgement(judgement);
//            hitNote.setHit(true);
//        }
//    }
//
//    private void processJudgement(String judgement) {
//        switch (judgement) {
//            case "Perfect", "Good" -> {
//                gameState.incrementCombo();
////                gameState.addScore(100); // will think about the scoring system later
//            }
//            case "Miss" -> {
//                gameState.resetCombo();
//            }
//        }
//        gameState.setJudgement(judgement);
//    }
//
//    public List<Note> getVisibleNotes() {
//        return noteManager.getVisibleNotes();
//    }
//}