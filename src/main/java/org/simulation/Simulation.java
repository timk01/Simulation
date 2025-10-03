package org.simulation;

import org.map.WorldMap;
import org.map.path.PathFinder;
import org.simulation.Action.*;
import org.simulation.config.*;
import org.simulation.config.preset.CreaturesPreset;
import org.simulation.config.preset.GrassPreset;
import org.simulation.config.preset.MapPreset;
import org.simulation.config.preset.ObstaclesPreset;

import java.util.List;

public class Simulation {

    private final WorldMap map;
    private final Statistic statistic;

    private int moves;

    private final Renderer renderer;
    private final List<InitAction> oneTimeActions;
    private final List<TurnAction> eachTurnActions;
    private final List<FinishAction> finishActions;

    public Simulation(WorldMap map, Renderer renderer,
                      List<InitAction> init, List<TurnAction> turn, List<FinishAction> finishActions,
                      Statistic statistic) {
        this.map = map;
        this.renderer = renderer;
        this.oneTimeActions = init;
        this.eachTurnActions = turn;
        this.finishActions = finishActions;
        this.statistic = statistic;
        this.moves = 0;
    }

    public int getMoves() {
        return moves;
    }

    public void nextTurn() {
        for (TurnAction action : eachTurnActions) {
            action.update(map);
        }
        statistic.printConsistencyCheck(map);
        moves++;
    }

    public void startSimulation() {
        for (InitAction action : oneTimeActions) {
            action.initiate(map);
        }
        statistic.captureInitial(map);
    }

    public void finishSimulation() {
        for (FinishAction finishAction : finishActions) {
            finishAction.finish(map, renderer);
        }
        //finishActions.forEach(a -> a.finish(map, renderer));
        //посмотрть консьюмеры (забыл)
    }

    public void pauseSimulation() {

    }

    public static void main(String[] args) throws InterruptedException {
        SimulationConfig config = new SimulationPreset(
                GrassPreset.MEDIUM,
                ObstaclesPreset.MEDIUM,
                CreaturesPreset.MEDIUM,
                MapPreset.MEDIUM
        ).toConfig();

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

        Simulation simulation = new Simulation(
                worldMap,
                renderer,
                initActions,
                turnActions,
                finishActions,
                statistic
        );

        simulation.startSimulation();
        renderer.render(worldMap, simulation.getMoves(), statistic);

        boolean running = true;
        while (running) {
            Thread.sleep(500);
            simulation.nextTurn();
            //statistic.printConsistencyCheck(worldMap);
            renderer.render(worldMap, simulation.getMoves(), statistic);
            if (simulation.moves >= 30) {
                running = false;
            }
        }

        simulation.finishSimulation();
    }

    record SimulationPreset(GrassPreset grass, ObstaclesPreset obstacles, CreaturesPreset creatures, MapPreset map) {
        public SimulationPreset {
        }

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
            double obstShare  = obstaclesConfig.getCapShare();
            double herbShare  = creaturesConfig.getHerbivoresCapShare();
            double predShare  = creaturesConfig.getPredatorsCapShare();
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
}
