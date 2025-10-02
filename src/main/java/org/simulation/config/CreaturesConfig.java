package org.simulation.config;

public class CreaturesConfig {
    private static final int DEFAULT_HERBIVORES = 15;
    private static final int DEFAULT_PREDATORS = 15;
    private static final int MINIMUM_CREATURES = 5;
    private static final double DEFAULT_HERBIVORES_OCCUPANCY_RATIO = 0.1;
    private static final double HERBIVORES_MAX_OCCUPANCY_RATIO = 0.15;
    private static final double DEFAULT_PREDATORS_OCCUPANCY_RATIO = 0.1;
    private static final double PREDATORS_MAX_OCCUPANCY_RATIO = 0.15;

    private final int herbivoreCount;
    private final int predatorCount;
    private final double herbivoresCapShare;
    private final double predatorsCapShare;

    public CreaturesConfig(int herbivoreCount, int predatorCount,
                           double herbivoresOccupancyRatio, double predatorsOccupancyRatio) {
        if (herbivoreCount < 0 || predatorCount < 0
                || herbivoresOccupancyRatio < 0 || predatorsOccupancyRatio < 0) {
            throw new IllegalArgumentException("herbivores or predators quantity / share cannot be less than zero");
        }
        this.herbivoreCount = (herbivoreCount < MINIMUM_CREATURES) ? DEFAULT_HERBIVORES : herbivoreCount;
        this.predatorCount = (predatorCount < MINIMUM_CREATURES) ? DEFAULT_PREDATORS : predatorCount;
        this.herbivoresCapShare = (herbivoresOccupancyRatio <= 0 ||
                herbivoresOccupancyRatio > HERBIVORES_MAX_OCCUPANCY_RATIO) ?
                DEFAULT_HERBIVORES_OCCUPANCY_RATIO : herbivoresOccupancyRatio;
        this.predatorsCapShare = (predatorsOccupancyRatio <= 0 ||
                predatorsOccupancyRatio > PREDATORS_MAX_OCCUPANCY_RATIO) ?
                DEFAULT_PREDATORS_OCCUPANCY_RATIO : predatorsOccupancyRatio;
    }

    public CreaturesConfig(int herbivoreCount, int predatorCount) {
        this(herbivoreCount, predatorCount, DEFAULT_HERBIVORES_OCCUPANCY_RATIO, DEFAULT_PREDATORS_OCCUPANCY_RATIO);
    }

    public CreaturesConfig() {
        this(DEFAULT_HERBIVORES, DEFAULT_PREDATORS, DEFAULT_HERBIVORES_OCCUPANCY_RATIO, DEFAULT_PREDATORS_OCCUPANCY_RATIO);
    }

    public int getHerbivoreCount() {
        return herbivoreCount;
    }

    public int getPredatorCount() {
        return predatorCount;
    }

    public double getHerbivoresCapShare() {
        return herbivoresCapShare;
    }

    public double getPredatorsCapShare() {
        return predatorsCapShare;
    }
}
