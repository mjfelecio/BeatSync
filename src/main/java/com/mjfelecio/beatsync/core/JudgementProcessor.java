package com.mjfelecio.beatsync.core;

public class JudgementProcessor {

    public static JudgementResult judge(Note n, long timeElapsed) {
        JudgementResult judgementResult = JudgementResult.NONE;
        if (isMiss(n, timeElapsed)) {
            judgementResult = JudgementResult.MISS;
        }

        return judgementResult;
    }

    private static boolean isMiss(Note n, long timeElapsed) {
        return !n.isHit() && (timeElapsed - n.getStartTime()) > JudgementWindow.MISS.getMillis();
    }
}
