package org.simulation;

import org.map.WorldMap;
import org.simulation.Action.Statistic;
import org.simulation.config.preset.MapPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Simulation implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Simulation.class);
    private final Object moveLock = new Object();

    private final WorldMap map;
    private final Statistic statistic;

    private final Renderer renderer;
    private final List<InitAction> oneTimeActions;
    private final List<TurnAction> eachTurnActions;
    private final List<FinishAction> finishActions;
    private final Controller controller;
    private final SimulationSettings settings;
    private volatile boolean running = true;
    private final AtomicInteger moves = new AtomicInteger(0);

    public Simulation(WorldMap map,
                      Statistic statistic,
                      Renderer renderer,
                      List<InitAction> oneTimeActions,
                      List<TurnAction> eachTurnActions,
                      List<FinishAction> finishActions,
                      Controller controller,
                      SimulationSettings settings) {
        this.map = map;
        this.statistic = statistic;
        this.renderer = renderer;
        this.oneTimeActions = oneTimeActions;
        this.eachTurnActions = eachTurnActions;
        this.finishActions = finishActions;
        this.controller = controller;
        this.settings = settings;
    }

    public int getMoves() {
        return moves.get();
    }

    public WorldMap getMap() {
        return map;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public boolean isRunning() {
        return running;
    }

    public void nextTurn() {
        for (TurnAction action : eachTurnActions) {
            action.update(map);
        }
        synchronized (moveLock) {
            moves.incrementAndGet();
            moveLock.notifyAll();
        }
    }

    public void finishSimulation() {
        for (FinishAction finishAction : finishActions) {
            finishAction.finish(map, renderer);
        }
    }

    public void pauseSimulation() {
        controller.pause();
    }

    public void stop() {
        log.info("Stop requested");
        running = false;
        controller.resume();
        synchronized (moveLock) {
            moveLock.notifyAll();
        }
    }

    public void stopAndClose() {
        renderer.setKeepWindowOpenOnFinish(false);
        stop();
    }

    public void startSimulation() {
        for (InitAction action : oneTimeActions) {
            action.initiate(map);
        }
        statistic.captureInitial(map);
        log.info("Simulation initialized: map {}x{}", map.getWidth(), map.getHeight());
    }

    private void getRender() {
        SwingUtilities.invokeLater(
                () -> renderer.render(map, moves.get(), statistic)
        );
    }

    public boolean awaitMoveIncrement(int prev, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        synchronized (moveLock) {
            while (isRunning() && moves.get() == prev) {
                long wait = deadline - System.currentTimeMillis();
                if (wait <= 0) {
                    return false;
                }
                try {
                    moveLock.wait(wait);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return moves.get() != prev;
        }
    }

    private void awaitPermissionOrBreak() {
        try {
            controller.awaitPermission();
        } catch (InterruptedException e) {
            if (!running) {
                log.info("Simulation interrupted after STOP");
            } else {
                log.warn("awaitPermission interrupted while running; continue loop");
            }
        }
    }

    private boolean doOneTurnAndRender() {
        if (!running) {
            log.info("Stopping: running=false detected before turn");
            return false;
        }
        log.debug("Turn {}", getMoves() + 1);
        nextTurn();
        getRender();
        return !stopIfReachedMaxMoves();
    }

    private boolean stopIfReachedMaxMoves() {
        int maxMoves = settings.getMaxMoves();
        if (maxMoves > 0 && getMoves() >= maxMoves) {
            log.info("Reached maxMoves={} (moves={}), stopping", maxMoves, getMoves());
            running = false;
            return true;
        }
        return false;
    }

    private void applyDelayIfNeeded() {
        if (!controller.isPaused()) {
            int delay = settings.getDelay();
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    log.warn("Sleep interrupted; stopping");
                    Thread.currentThread().interrupt();
                    running = false;
                }
            }
        }
    }

    @Override
    public void run() {
        log.info("Simulation thread started");
        startSimulation();
        getRender();

        try {
            while (running) {
                awaitPermissionOrBreak();
                if (!running) {
                    break;
                }
                if (!doOneTurnAndRender()) {
                    break;
                }
                applyDelayIfNeeded();
            }
            log.info("Simulation finished; totalMoves={}", moves);
        } finally {
            finishSimulation();
        }
    }

    static Result getSimulationStarted(MapPreset mapPreset, SimulationSettings simulationSettings) {
        return SimulationComponentBuilder.assemble(mapPreset, simulationSettings);
    }

    public static void main(String[] args) throws InterruptedException {
        SimulationApp.runSimulation();
    }

    public record Result(Controller controller, Simulation simulation, Renderer renderer) {
    }
}
