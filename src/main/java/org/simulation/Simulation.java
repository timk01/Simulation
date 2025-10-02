package org.simulation;

import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.map.path.PathFinder;
import org.simulation.Action.*;
import org.simulation.config.*;

import java.util.List;
import java.util.Map;

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
        MapConfig mapConfig = new MapConfig(20, 20, 0.6);
        GrassConfig grassConfig = new GrassConfig();
        ObstaclesConfig obstaclesConfig = new ObstaclesConfig();
        CreaturesConfig creaturesConfig = new CreaturesConfig();
        SimulationConfig simulationConfig = new SimulationConfig(
                mapConfig,
                grassConfig,
                obstaclesConfig,
                creaturesConfig
        );

        WorldMap worldMap = new WorldMap(simulationConfig.getMapConfig());
        Renderer renderer = new Renderer(worldMap);
        PathFinder pathFinder = new PathFinder();

        List<InitAction> initActions = List.of(
                new InitGrass(),
                new InitObstacles(),
                new InitCreatures()
        );
        Statistic statistic = new Statistic();

        List<TurnAction> turnActions = List.of(
                new TickAction(statistic),
                new MoveCreatures(statistic, pathFinder),
                new FlushGrassEatenAction(),
                new CleanDeadAction(statistic),
                new GrowGrass()
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
            Thread.sleep(150);
            simulation.nextTurn();
            //statistic.printConsistencyCheck(worldMap);
            renderer.render(worldMap, simulation.getMoves(), statistic);
            if (simulation.moves >= 30) {
                running = false;
            }
        }

        simulation.finishSimulation();
    }
}
