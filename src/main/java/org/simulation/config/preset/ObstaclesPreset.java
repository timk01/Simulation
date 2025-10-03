package org.simulation.config.preset;

import org.simulation.config.ObstaclesConfig;

public enum ObstaclesPreset {
    SMALL(10, 0.08),
    MEDIUM(30, 0.1),
    LARGE(50, 0.12);

    private final int totalObstacles;
    private final double capShare;

    ObstaclesPreset(int totalObstacles, double capShare) {
        this.totalObstacles = totalObstacles;
        this.capShare = capShare;
    }

    public int getTotalObstacles() {
        return totalObstacles;
    }

    public double getCapShare() {
        return capShare;
    }

    public ObstaclesConfig toConfig() {
        return new ObstaclesConfig(totalObstacles, capShare);
    }
}
