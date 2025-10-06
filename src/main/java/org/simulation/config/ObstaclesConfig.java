package org.simulation.config;

public class ObstaclesConfig {
    private static final int DEFAULT_OBSTACLES = 30;
    private static final int MINIMUM_OBSTACLES = 10;
    private static final double DEFAULT_OCCUPANCY_RATIO = 0.1;
    private static final double OBSTACLES_MAX_OCCUPANCY_RATIO = 0.15;

    private final int totalObstacles;
    private final double capShare;


    public ObstaclesConfig(int totalObstacles, double occupancyRatio) {
        if (totalObstacles < 0) {
            throw new IllegalArgumentException("totalObstacles quantity cannot be less than zero " + totalObstacles);
        }
        this.totalObstacles = (totalObstacles < MINIMUM_OBSTACLES) ? DEFAULT_OBSTACLES : totalObstacles;
        this.capShare = (occupancyRatio <= 0 || occupancyRatio > OBSTACLES_MAX_OCCUPANCY_RATIO) ?
                DEFAULT_OCCUPANCY_RATIO : occupancyRatio;
    }

    public ObstaclesConfig(int totalObstacles) {
        this(totalObstacles, DEFAULT_OCCUPANCY_RATIO);
    }

    public ObstaclesConfig() {
        this(DEFAULT_OBSTACLES, DEFAULT_OCCUPANCY_RATIO);
    }

    public int getTotalObstacles() {
        return totalObstacles;
    }

    public double getCapShare() {
        return capShare;
    }
}
