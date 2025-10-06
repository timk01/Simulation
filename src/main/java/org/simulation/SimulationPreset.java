package org.simulation;

import org.simulation.config.*;
import org.simulation.config.preset.CreaturesPreset;
import org.simulation.config.preset.GrassPreset;
import org.simulation.config.preset.MapPreset;
import org.simulation.config.preset.ObstaclesPreset;

public record SimulationPreset(GrassPreset grass, ObstaclesPreset obstacles, CreaturesPreset creatures, MapPreset map) {
    SimulationConfig toConfig() {
        MapConfig mapCfg = map.toConfig();
        GrassConfig grassCfg = grass.toConfig();
        ObstaclesConfig obstCfg = obstacles.toConfig();
        CreaturesConfig creatCfg = creatures.toConfig();

        checkMapShares(grassCfg, obstCfg, creatCfg, mapCfg);

        return new SimulationConfig(map.toConfig(), grass.toConfig(), obstacles.toConfig(), creatures.toConfig());
    }

    private static void checkMapShares(
            GrassConfig grassConfig,
            ObstaclesConfig obstaclesConfig,
            CreaturesConfig creaturesConfig,
            MapConfig mapConfig
    ) {
        double grassShare = grassConfig.getCapShare();
        double obstShare = obstaclesConfig.getCapShare();
        double herbShare = creaturesConfig.getHerbivoresCapShare();
        double predShare = creaturesConfig.getPredatorsCapShare();
        double shareSum = grassShare + obstShare + herbShare + predShare;
        if (shareSum > mapConfig.getOccupancyRatio()) {
            throw new IllegalArgumentException(String.format(
                    "Выбранные лимиты не помещаются на карту. " +
                            "Уменьшите любую из долей И/ИЛИ увеличьте карту:" +
                            " трава=%.2f, препятствия=%.2f, травоядные=%.2f, хищники=%.2f (сумма=%.2f).",
                    grassShare, obstShare, herbShare, predShare, shareSum
            ));
        }
    }

}