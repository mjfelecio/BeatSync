package com.mjfelecio.beatsync.judgement;

public class JudgementProcessor {

    // Judge the press of a note based on the noteTime and the current time
    // If we want to judge a normal note, we pass the startTime as the noteTime
    // If we want to judge a hold note, we pass the endTime instead,
    // so we can judge when it was released
    public static JudgementResult judge(long noteTime, long currentTime) {
        JudgementResult judgementResult;
        long deltaTime = Math.abs(currentTime - noteTime);

        if (deltaTime <= JudgementWindow.PERFECT.getMillis()) judgementResult = JudgementResult.PERFECT;
        else if (deltaTime <= JudgementWindow.GREAT.getMillis()) judgementResult = JudgementResult.GREAT;
        else if (deltaTime <= JudgementWindow.MEH.getMillis()) judgementResult = JudgementResult.MEH;
        else judgementResult = JudgementResult.MISS;

        return judgementResult;
    }

    public static JudgementResult judgeTail(long noteTime, long currentTime) {
        JudgementResult judgementResult;
        long deltaTime = Math.abs(currentTime - noteTime);

        if (deltaTime <= JudgementWindow.PERFECT_TAIL.getMillis()) judgementResult = JudgementResult.PERFECT;
        else if (deltaTime <= JudgementWindow.GREAT_TAIL.getMillis()) judgementResult = JudgementResult.GREAT ;
        else if (deltaTime <= JudgementWindow.MEH_TAIL.getMillis()) judgementResult = JudgementResult.MEH;
        else judgementResult = JudgementResult.MISS;

        return judgementResult;
    }
}
