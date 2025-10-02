package org.simulation.config;

import org.entity.Grass;

public class SimulationConfig {
    private final MapConfig mapConfig;
    private final GrassConfig grassConfig;
    private final ObstaclesConfig obstaclesConfig;
    private final CreaturesConfig creaturesConfig;

    public SimulationConfig(MapConfig mapConfig, GrassConfig grassConfig, ObstaclesConfig obstaclesConfig, CreaturesConfig creaturesConfig) {
        this.mapConfig = mapConfig;
        this.grassConfig = grassConfig;
        this.obstaclesConfig = obstaclesConfig;
        this.creaturesConfig = creaturesConfig;
    }

    public MapConfig getMapConfig() {
        return mapConfig;
    }

    public GrassConfig getGrassConfig() {
        return grassConfig;
    }

    public ObstaclesConfig getObstaclesConfig() {
        return obstaclesConfig;
    }

    public CreaturesConfig getCreaturesConfig() {
        return creaturesConfig;
    }
}
