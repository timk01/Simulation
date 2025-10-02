package org.simulation.config;

enum MapPreset {
        SMALL(12, 12, 0.45),
        MEDIUM(20, 20, 0.5),
        LARGE(30, 30, 0.55);

        private final int width;
        private final int height;
        private final double occupancyRatio;

    MapPreset(int width, int height, double occupancyRatio) {
        this.width = width;
        this.height = height;
        this.occupancyRatio = occupancyRatio;
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

    public MapConfig toConfig() {
        return new MapConfig(width, height, occupancyRatio);
    }
}