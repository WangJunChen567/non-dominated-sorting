package ru.ifmo.nds.jfb;

public class SplitMergeMetricB {
    private final int goodFrom;
    private final int goodUntil;
    private final int weakFrom;
    private final int weakUntil;
    private final long time;

    public SplitMergeMetricB(int goodFrom, int goodUntil, int weakFrom, int weakUntil, long time) {
        this.goodFrom = goodFrom;
        this.goodUntil = goodUntil;
        this.weakFrom = weakFrom;
        this.weakUntil = weakUntil;
        this.time = time;
    }

    @Override
    public String toString() {
        return goodFrom + " " + goodUntil + " " + weakFrom + " " + weakUntil + " " + time;
    }
}
