package org.simulation;

import java.util.concurrent.atomic.AtomicInteger;

public class SimulationSettings {
    private static final int DEFAULT_DELAY = 3000;
    private static final int MAX_DELAY = 60000;
    private static final int MAX_MOVES_LIMIT = 10_000;

    private final AtomicInteger delay = new AtomicInteger(DEFAULT_DELAY);
    private final AtomicInteger maxMoves = new AtomicInteger(0);

    public static int defaultDelay() {
        return DEFAULT_DELAY;
    }

    public static int maxDelay() {
        return MAX_DELAY;
    }

    public static int maxMovesLimit() {
        return MAX_MOVES_LIMIT;
    }

    public int getDelay() {
        return delay.get();
    }

    public void setDelay(int delay) {
        this.delay.set(Math.max(0, Math.min(MAX_DELAY, delay)));
    }

    public int getMaxMoves() {
        return maxMoves.get();
    }

    public void setMaxMoves(int maxMoves) {
        this.maxMoves.set(Math.max(0, maxMoves));
    }
}
