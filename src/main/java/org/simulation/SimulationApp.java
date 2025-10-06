package org.simulation;

import org.simulation.config.preset.MapPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import static org.simulation.Simulation.getSimulationStarted;

public class SimulationApp {
    private static final Logger log = LoggerFactory.getLogger(SimulationApp.class);

    public static void runSimulation() throws InterruptedException {
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

            Simulation.Result result = getSimulationStarted(mapPreset, settings);

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
}
