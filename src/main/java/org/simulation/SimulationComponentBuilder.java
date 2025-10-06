package org.simulation;

import org.map.WorldMap;
import org.map.path.PathFinder;
import org.simulation.Action.*;
import org.simulation.config.SimulationConfig;
import org.simulation.config.preset.CreaturesPreset;
import org.simulation.config.preset.GrassPreset;
import org.simulation.config.preset.MapPreset;
import org.simulation.config.preset.ObstaclesPreset;

import java.util.List;

public class SimulationComponentBuilder {
    public static Simulation.Result assemble(MapPreset mapPreset, SimulationSettings settings) {
        SimulationConfig cfg = buildConfig(mapPreset);
        return linkConfig(cfg, settings);
    }

    public static SimulationConfig buildConfig(MapPreset mapPreset) {
        GrassPreset grassPreset = switch (mapPreset) {
            case SMALL -> GrassPreset.SMALL;
            case MEDIUM -> GrassPreset.MEDIUM;
            case LARGE -> GrassPreset.LARGE;
        };
        ObstaclesPreset obstaclesPreset = switch (mapPreset) {
            case SMALL -> ObstaclesPreset.SMALL;
            case MEDIUM -> ObstaclesPreset.MEDIUM;
            case LARGE -> ObstaclesPreset.LARGE;
        };
        CreaturesPreset creaturesPreset = switch (mapPreset) {
            case SMALL -> CreaturesPreset.SMALL;
            case MEDIUM -> CreaturesPreset.MEDIUM;
            case LARGE -> CreaturesPreset.LARGE;
        };

        return new SimulationPreset(
                grassPreset,
                obstaclesPreset,
                creaturesPreset,
                mapPreset
        ).toConfig();
    }

    public static Simulation.Result linkConfig(SimulationConfig config, SimulationSettings simulationSettings) {
        WorldMap worldMap = new WorldMap(config.getMapConfig());
        Renderer renderer = new Renderer(worldMap);
        PathFinder pathFinder = new PathFinder();

        List<InitAction> initActions = List.of(
                new InitGrass(config.getGrassConfig()),
                new InitObstacles(config.getObstaclesConfig()),
                new InitCreatures(config.getCreaturesConfig())
        );
        Statistic statistic = new Statistic();

        List<TurnAction> turnActions = List.of(
                new TickAction(statistic),
                new MoveCreatures(statistic, pathFinder),
                new FlushGrassEatenAction(),
                new CleanDeadAction(statistic),
                new GrowGrass(config.getGrassConfig())
        );

        List<FinishAction> finishActions = List.of(
                new ShowReportAction(statistic)
        );

        Controller controller = new Controller();
        Simulation simulation = new Simulation(
                worldMap,
                statistic,
                renderer,
                initActions,
                turnActions,
                finishActions,
                controller,
                simulationSettings
        );
        return new Simulation.Result(controller, simulation, renderer);
    }
}
