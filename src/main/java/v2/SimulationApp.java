package v2;

import v2.config.StarterSimulationPreset;
import v2.config.WorldMapPreset;
import v2.console.ConsoleCommandSource;
import v2.controller.Controller;
import v2.dialogue.ConsoleConfig;
import v2.dialogue.PrintUtil;
import v2.map.WorldMap;
import v2.renderer.Renderer;

import java.util.Optional;
import java.util.Scanner;

public class SimulationApp {
    private WorldMapPreset starterSimulationPreset;

    private ConsoleConfig config;

    public void runSimulation() throws InterruptedException {
        config = new ConsoleConfig();
        boolean continueSimulation = true;
        Scanner scanner = new Scanner(System.in);
        ConsoleCommandSource commandSource = new ConsoleCommandSource(scanner);

        while (continueSimulation) {
            PrintUtil.printYesNoAtSimulStart();
            if (!commandSource.askToStart()) {
                continueSimulation = false;
                continue;
            }

            PrintUtil.printMapInfo();
            Optional<Integer> chosenWorld = commandSource.askIntOrEnter(
                    ConsoleConfig.SMALL_PRESET_KEY,
                    ConsoleConfig.LARGE_PRESET_KEY
            );
            StarterSimulationPreset preset = chosenWorld
                    .map(StarterSimulationPreset::presetFromKey)
                    .orElse(StarterSimulationPreset.MEDIUM);

            WorldMap worldMap = new WorldMap(preset.getMapPreset().getWidth(), preset.getMapPreset().getHeight());
            Renderer renderer = new Renderer(worldMap);
            Controller controller = new Controller();
            Simulation simulation = new Simulation(worldMap, renderer, controller, preset);
            Thread simThread = new Thread(simulation, "epicSimulation");
            commandSource = new ConsoleCommandSource(controller, simulation, simThread, scanner);

            //ConsoleCommandSource.StartOptions startOptions = commandSource.collectStartOptions();

            //org.simulation.Simulation.Result result = getSimulationStarted(mapPreset, settings);

            simulation.pauseSimulation();
            simThread.start();

            try {
                commandSource.runProgram();
            } finally {
                simulation.stop();
                controller.resume();
                simThread.interrupt();
                simThread.join();
            }
        }
        commandSource.close();
    }
}
