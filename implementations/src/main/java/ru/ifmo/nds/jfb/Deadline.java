package ru.ifmo.nds.jfb;

import java.time.Duration;

public class Deadline {
    private final long endTime;
    private final Timer timer;

    private boolean exceeded = false;

    private Deadline(long endTime, Timer timer) {
        this.endTime = endTime;
        this.timer = timer;
    }


    public static Deadline fromNow(Duration duration) {
        return fromNow(Timer.SYSTEM_TIMER, duration);
    }

    public static Deadline fromNow(Timer timer, Duration duration) {
        return new Deadline(timer.millis() + duration.toMillis(), timer);
    }

    public boolean isExceeded() {
        return exceeded = timer.millis() > endTime;
    }

    public boolean wasExceeded() {
        return exceeded;
    }
}

interface Timer {
    long millis();

    Timer SYSTEM_TIMER = () -> System.nanoTime() / 1_000_000;
}
