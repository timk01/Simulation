package org.simulation.config;

import org.entity.Entity;
import org.map.Location;

import java.util.HashMap;
import java.util.Map;

public class MapConfig {
    private static final double MAX_OCCUPANCY_RATIO = 0.75;
    private static final double DEFAULT_OCCUPANCY_RATIO = 0.65;
    private static final int DEFAULT_WIDTH = 20;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int MINIMUM_THRESHOLD = 10;

    private final int width;
    private final int height;
    private final double occupancyRatio;

    public MapConfig(int width, int height, double occupancyRatio) {
        this.width = (width < MINIMUM_THRESHOLD) ? DEFAULT_WIDTH : width;
        this.height = (height < MINIMUM_THRESHOLD) ? DEFAULT_HEIGHT : height;
        this.occupancyRatio = (occupancyRatio <= 0 || occupancyRatio > MAX_OCCUPANCY_RATIO) ?
                DEFAULT_OCCUPANCY_RATIO : occupancyRatio;
    }

    public MapConfig(int width, int height) {
        this(width, height, DEFAULT_OCCUPANCY_RATIO);
    }

    public MapConfig() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_OCCUPANCY_RATIO);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getOccupancyRatio() {
        return occupancyRatio;
    }
}
