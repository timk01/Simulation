package org.simulation.config.preset;

import org.simulation.config.GrassConfig;

public enum GrassPreset {
    SMALL(12, 0.15),
    MEDIUM(40, 0.2),
    LARGE(80, 0.25);

    private final int grassCount;
    private final double capShare;

    GrassPreset(int grassCount, double occupancyRatio) {
        this.grassCount = grassCount;
        this.capShare = occupancyRatio;
    }

    public int getGrassCount() {
        return grassCount;
    }

    public double getCapShare() {
        return capShare;
    }

    public GrassConfig toConfig() {
        return new GrassConfig(grassCount, capShare);
    }
}
