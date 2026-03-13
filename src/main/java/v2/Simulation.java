package v2;

import v2.actions.*;
import v2.controller.Controller;
import v2.dialogue.PrintUtil;
import v2.entity.EntityFactory;
import v2.map.WorldMap;
import v2.path.PathFinder;
import v2.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;


public class Simulation implements Runnable {
    private final WorldMap worldMap;
    private final Renderer consoleRenderer;
    private List<Action> initActions = new ArrayList<>();
    private List<Action> turnActions = new ArrayList<>();
    private int turnCounter;

    private final ActionHelper actionHelper;
    private final EntityFactory entityFactory;

    private final PathFinder pathFinder;

    private final Controller controller;

    private volatile boolean running = true;

    public Simulation(WorldMap map, Renderer renderer, Controller controller) {
        this.worldMap = map;
        this.consoleRenderer = renderer;

        this.entityFactory = new EntityFactory();
        this.actionHelper = new ActionHelper(entityFactory);
        this.initActions = List.of(
                new PopulateMapAction(actionHelper));
        this.pathFinder = new PathFinder();
        this.turnActions = List.of(
                new MoveCreaturesAction(pathFinder),
                new KeepPopulationStableAction(actionHelper));

        this.controller = controller;
    }

    public void startSimulation() {
        makeTurn(initActions);
        PrintUtil.printHelp();

        while (running) {
            try {
                controller.awaitPermission();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            incrementCounter();
            makeTurn(turnActions);
            delay();
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            PrintUtil.printStatus(turnCounter);
            PrintUtil.printCommandPrompt();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void nextTurn() {
        controller.oneMoreMove();
    }

    public void makeTurn(List<Action> actions) {
        for (Action action : actions) {
            action.execute(worldMap);
        }
        consoleRenderer.draw();
    }

    private void incrementCounter() {
        turnCounter++;
    }

    public void stop() {
        running = false;
        controller.resume();
    }

    public void delay() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        startSimulation();
    }

    public void resumeSimulation() {
        controller.resume();
    }

    public void pauseSimulation() {
        controller.pause();
    }
}
