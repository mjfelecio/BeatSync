package com.mjfelecio.beatsync.judgement;

import com.mjfelecio.beatsync.object.Note;

public class JudgementProcessor {

    // Judge the press of a note based on the noteTime and the current time
    // If we want to judge a normal note, we pass the startTime as the noteTime
    // If we want to judge a hold note, we pass the endTime instead,
    // so we can judge when it was released
    public static JudgementResult judge(long noteTime, long currentTime) {
        JudgementResult judgementResult;
        long deltaTime = Math.abs(currentTime - noteTime);

        if (deltaTime <= JudgementWindow.PERFECT.getMillis()) judgementResult = JudgementResult.PERFECT;
        else if (deltaTime <= JudgementWindow.GOOD.getMillis()) judgementResult = JudgementResult.GOOD ;
        else judgementResult = JudgementResult.MISS;

        return judgementResult;
    }

    public static JudgementResult judgeTail(long noteTime, long currentTime) {
        JudgementResult judgementResult;
        long deltaTime = Math.abs(currentTime - noteTime);

        if (deltaTime <= JudgementWindow.PERFECT_TAIL.getMillis()) judgementResult = JudgementResult.PERFECT;
        else if (deltaTime <= JudgementWindow.GOOD_TAIL.getMillis()) judgementResult = JudgementResult.GOOD ;
        else judgementResult = JudgementResult.MISS;

        return judgementResult;
    }
}
