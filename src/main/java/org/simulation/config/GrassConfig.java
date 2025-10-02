package org.simulation.config;

public class GrassConfig {
    private static final int DEFAULT_GRASS = 50;
    private static final int MINIMUM_GRASS = 5;
    private static final double DEFAULT_OCCUPANCY_RATIO = 0.2;
    private static final double GRASS_MAX_OCCUPANCY_RATIO = 0.3;

    private final int grassCount;
    private final double occupancyRatio;


    public GrassConfig(int grassCount, double occupancyRatio) {
        if (grassCount < 0) {
            throw new IllegalArgumentException("grass quantity cannot be less than zero");
        }
        this.grassCount = (grassCount < MINIMUM_GRASS) ? DEFAULT_GRASS : grassCount;
        this.occupancyRatio = (occupancyRatio <= 0 || occupancyRatio > GRASS_MAX_OCCUPANCY_RATIO) ?
                DEFAULT_OCCUPANCY_RATIO : occupancyRatio;
    }

    public GrassConfig(int grassCount) {
        this(grassCount, DEFAULT_OCCUPANCY_RATIO);
    }

    public GrassConfig() {
        this(DEFAULT_GRASS, DEFAULT_OCCUPANCY_RATIO);
    }

    public int getGrassCount() {
        return grassCount;
    }

    public double getOccupancyRatio() {
        return occupancyRatio;
    }
}
