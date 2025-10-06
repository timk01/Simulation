package org.simulation;

import org.map.WorldMap;
import org.map.path.PathFinder;
import org.simulation.Action.*;
import org.simulation.config.*;
import org.simulation.config.preset.CreaturesPreset;
import org.simulation.config.preset.GrassPreset;
import org.simulation.config.preset.MapPreset;
import org.simulation.config.preset.ObstaclesPreset;
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

    public static void shutdownUi() {
        SwingUtilities.invokeLater(() -> {
            for (java.awt.Window w : java.awt.Window.getWindows()) {
                try {
                    w.dispose();
                } catch (Exception ignored) {
                    log.warn("while closing window in shutdownUi --> exception " + ignored);
                }
            }
        });
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

    public void setKeepWindowOpenOnFinish(boolean keep) {
        renderer.setKeepWindowOpenOnFinish(keep);
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

    @Override
    public void run() {
        log.info("Simulation thread started");
        startSimulation();
        getRender();

        try {
            while (running) {
                try {
                    controller.awaitPermission();
                } catch (InterruptedException e) {
                    if (!running) {
                        log.info("Simulation interrupted after STOP");
                        break;
                    } else {
                        log.warn("awaitPermission interrupted while running; continue loop");
                        continue;
                    }
                }

                if (!running) {
                    log.info("Stopping: running=false detected before turn");
                    break;
                }

                log.debug("Turn {}", getMoves() + 1);
                nextTurn();
                getRender();

                int maxMoves = settings.getMaxMoves();
                if (maxMoves > 0 && getMoves() >= maxMoves) {
                    log.info("Reached maxMoves={} (moves={}), stopping", maxMoves, getMoves());
                    running = false;
                    break;
                }

                if (!controller.isPaused()) {
                    int delay = settings.getDelay();
                    if (delay > 0) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            log.warn("Sleep interrupted; stopping");
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }

                }
            }
            log.info("Simulation finished; totalMoves={}", moves);
        } finally {
            finishSimulation();
        }
    }

    private static Result getSimulationStarted(MapPreset mapPreset, SimulationSettings simulationSettings) {
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

        SimulationConfig config = new SimulationPreset(
                grassPreset,
                obstaclesPreset,
                creaturesPreset,
                mapPreset
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
                statistic,
                renderer,
                initActions,
                turnActions,
                finishActions,
                controller,
                simulationSettings
        );
        return new Result(controller, simulation, renderer);
    }

    public static void main(String[] args) throws InterruptedException {
        ConsoleCommandSource commandSource = new ConsoleCommandSource();

        boolean continueSimulation = true;
        while (continueSimulation) {
            System.out.println("Начать новую симуляцию? Введите 'д' для начала или 'н' для выхода.");
            if (!commandSource.askToStart()) {
                continueSimulation = false;
                continue;
            }

            ConsoleCommandSource.StartOptions startOptions = commandSource.collectStartOptions();

            MapPreset mapPreset = switch (startOptions.map()) {
                case SMALL -> MapPreset.SMALL;
                case MEDIUM -> MapPreset.MEDIUM;
                case LARGE -> MapPreset.LARGE;
            };

            SimulationSettings settings = startOptions.settings();

            Result result = getSimulationStarted(mapPreset, settings);

            Renderer renderer = result.renderer();
            renderer.setKeepWindowOpenOnFinish(true);

            final Simulation sim = result.simulation();
            sim.pauseSimulation();
            Thread t = new Thread(sim, "epicSimulation");

            renderer.setOnClose(() -> {
                log.info("renderer.onClose -> stop");
                renderer.setKeepWindowOpenOnFinish(false);
                sim.stop();
                t.interrupt();
                shutdownUi();
            });

            CommandSource realCommandSource = new ConsoleCommandSource(result.controller(), result.simulation(), t);

            t.start();
            try {
                realCommandSource.runProgram();
            } finally {
                t.interrupt();
                t.join();

                Renderer finalRenderer = result.renderer();
                finalRenderer.render(result.simulation().getMap(),
                        result.simulation().getMoves(),
                        result.simulation().getStatistic());
                result.renderer().dispose();
            }
        }
        commandSource.close();
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

    private record Result(Controller controller, Simulation simulation, Renderer renderer) {
    }
}
