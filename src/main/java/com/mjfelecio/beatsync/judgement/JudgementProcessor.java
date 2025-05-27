package com.mjfelecio.beatsync.judgement;

import com.mjfelecio.beatsync.object.Note;

public class JudgementProcessor {

    public static JudgementResult judge(Note n, long currentTime) {
        JudgementResult judgementResult;
        long deltaTime = Math.abs(currentTime - n.getStartTime());

        if (deltaTime <= JudgementWindow.PERFECT.getMillis()) judgementResult = JudgementResult.PERFECT;
        else if (deltaTime <= JudgementWindow.GOOD.getMillis()) judgementResult = JudgementResult.GOOD ;
        else judgementResult = JudgementResult.MISS;

        return judgementResult;
    }
}
