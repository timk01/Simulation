package simulation;

import simulation.actions.*;
import simulation.config.StarterSimulationPreset;
import simulation.controller.Controller;
import simulation.dialogue.PrintUtil;
import simulation.entity.EntityFactory;
import simulation.map.WorldMap;
import simulation.path.PathFinder;
import simulation.renderer.Renderer;

import java.util.List;

public class Simulation implements Runnable {
    private final WorldMap worldMap;
    private final Renderer consoleRenderer;
    private final Controller controller;

    private final List<Action> initActions;
    private final List<Action> turnActions;

    private int turnCounter;
    private volatile boolean running = true;

    public Simulation(WorldMap map, Renderer renderer, Controller controller, StarterSimulationPreset simulationPreset) {
        this.worldMap = map;
        this.consoleRenderer = renderer;
        this.controller = controller;

        EntityFactory entityFactory = new EntityFactory(simulationPreset.getEntityStatsPreset());
        ActionHelper actionHelper = new ActionHelper(entityFactory);

        this.initActions = List.of(
                new PopulateMapAction(actionHelper, simulationPreset.getEntitiesQuantityPreset()));

        this.turnActions = List.of(
                new MoveCreaturesAction(new PathFinder()),
                new KeepPopulationStableAction(actionHelper, simulationPreset.getRepopulatePreset()));
    }

    @Override
    public void run() {
        startSimulation();
    }

    private void startSimulation() {
        makeTurn(initActions);
        PrintUtil.printHelp();

        while (running) {
            try {
                controller.awaitPermission();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (!running) {
                return;
            }

            incrementCounter();
            PrintUtil.printStatus(turnCounter);
            makeTurn(turnActions);
            delay();
            if (!running || Thread.currentThread().isInterrupted()) {
                return;
            }
            PrintUtil.printCommandPrompt();
        }
    }

    private void makeTurn(List<Action> actions) {
        for (Action action : actions) {
            action.execute(worldMap);
        }
        consoleRenderer.draw();
    }

    private void incrementCounter() {
        turnCounter++;
    }

    private void delay() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void nextTurn() {
        controller.oneMoreMove();
    }

    public void stop() {
        running = false;
        controller.resume();
    }

    public void resumeSimulation() {
        controller.resume();
    }

    public void pauseSimulation() {
        controller.pause();
    }
}
