package ru.ifmo.nds.jfb;

public class SplitMergeMetricA {
    private final int from;
    private final int until;
    private final long time;

    public SplitMergeMetricA(int from, int until, long time) {
        this.from = from;
        this.until = until;
        this.time = time;
    }

    @Override
    public String toString() {
        return from + " " + until + " " + time;
    }
}
