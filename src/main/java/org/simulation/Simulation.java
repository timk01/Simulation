package org.simulation;

import org.map.WorldMap;
import org.map.path.PathFinder;
import org.simulation.Action.*;
import org.simulation.config.*;
import org.simulation.config.preset.CreaturesPreset;
import org.simulation.config.preset.GrassPreset;
import org.simulation.config.preset.MapPreset;
import org.simulation.config.preset.ObstaclesPreset;

import javax.swing.*;
import java.util.List;

public class Simulation implements Runnable {

    private final WorldMap map;
    private final Statistic statistic;


    private int moves;

    private final Renderer renderer;
    private final List<InitAction> oneTimeActions;
    private final List<TurnAction> eachTurnActions;
    private final List<FinishAction> finishActions;
    private final Controller controller;
    private volatile boolean running = true;

    public Simulation(WorldMap map,
                      Renderer renderer,
                      Statistic statistic,
                      List<InitAction> oneTimeActions,
                      List<TurnAction> eachTurnActions,
                      List<FinishAction> finishActions,
                      Controller controller) {
        this.map = map;
        this.statistic = statistic;
        this.renderer = renderer;
        this.oneTimeActions = oneTimeActions;
        this.eachTurnActions = eachTurnActions;
        this.finishActions = finishActions;
        this.controller = controller;
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
        controller.pause();
    }

    public void stop() {
        running = false;
        controller.resume();
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

        Controller controller = new Controller();
        Simulation simulation = new Simulation(
                worldMap,
                renderer,
                statistic,
                initActions,
                turnActions,
                finishActions,
                controller
        );

        Thread t = new Thread(simulation);

        t.start();

        simulation.pauseSimulation();
        //controller.pause();
        controller.oneMoreMove();
        controller.oneMoreMove();
        controller.resume();

        t.join();
    }

    private void getRender() {
        SwingUtilities.invokeLater(
                () -> renderer.render(map, moves, statistic)
        );
    }

    @Override
    public void run() {
        startSimulation();
        getRender();

        while (running) {
            try {
                controller.awaitPermission();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            nextTurn();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            moves++;
            getRender();
/*
            if(true) { //как будто лочит БЕЗ команды со стороны
                pauseSimulation();
            }

            if(true) { //польователь дал команду продолжать дальше
                controller.oneMoreMove();
            }*/

            if (moves >= 30) {
                running = false;
            }
        }

        finishSimulation();
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
