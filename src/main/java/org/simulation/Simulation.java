package org.simulation;

import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.Action.*;

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
        WorldMap worldMap = new WorldMap(20, 20);
        Renderer renderer = new Renderer(worldMap);


        List<InitAction> initActions = List.of(
                new InitGrass(),
                new InitObstacles(),
                new InitCreatures()
        );
        Statistic statistic = new Statistic(initActions);

        List<TurnAction> turnActions = List.of(
                new TickAction(statistic),
                new MoveCreatures(statistic),
                new FlushGrassEatenAction(statistic),
                new CleanDeadAction(statistic),
                new GrowGrass()
        );

        List<FinishAction> finishActions = List.of(
                new ShowReportAction(statistic, worldMap)
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
            Thread.sleep(1500);
            simulation.nextTurn();
            //statistic.printConsistencyCheck(worldMap);
            renderer.render(worldMap, simulation.getMoves(), statistic);
            if (simulation.moves >= 20) {
                running = false;
            }
        }

        simulation.finishSimulation();
    }
}
