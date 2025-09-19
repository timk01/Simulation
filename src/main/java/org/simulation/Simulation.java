package org.simulation;

import org.map.WorldMap;
import org.simulation.Action.*;

import java.util.List;

public class Simulation {

    private WorldMap map;
    private int moves;
    private Renderer renderer;
    private List<InitAction> oneTimeActions;
    private List<TurnAction> eachTurnActions;

    public Simulation(WorldMap map, Renderer renderer,
                      List<InitAction> init, List<TurnAction> turn) {
        this.map = map;
        this.renderer = renderer;
        this.oneTimeActions = init;
        this.eachTurnActions = turn;
        this.moves = 0;
    }
    public void nextTurn() {
        for (TurnAction action : eachTurnActions) {
            action.update(map);
        }
        moves++;
    }

    public void startSimulation() {
        for (InitAction action : oneTimeActions) {
            action.initiate(map);
        }
    }

    public void pauseSimulation() {

    }

    public static void main(String[] args) throws InterruptedException {
        WorldMap worldMap = new WorldMap(20, 20);
        Renderer renderer = new Renderer(worldMap, 30);

        List<InitAction> initActions = List.of(new InitGrass(), new InitObstacles(), new InitCreatures());
        List<TurnAction> turnActions  = List.of(new MoveCreatures(), new CleanDeadAction(), new GrowGrass());

        Simulation simulation = new Simulation(worldMap, renderer, initActions, turnActions);

        simulation.startSimulation();
        renderer.render(worldMap);

        boolean running = true;
        while (running) {
            Thread.sleep(500);
            simulation.nextTurn();
            renderer.render(worldMap);
            running = false;
        }
    }
}
