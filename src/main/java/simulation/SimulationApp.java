package simulation;

import simulation.console.*;
import simulation.controller.Controller;
import simulation.map.WorldMap;
import simulation.path.BFSPathFinder;
import simulation.path.PathFinder;
import simulation.presets.StarterSimulationPreset;
import simulation.renderer.ConsoleRenderer;
import simulation.renderer.Renderer;

import java.util.Optional;
import java.util.Scanner;

public class SimulationApp {
    public void runSimulation() throws InterruptedException {
        boolean continueSimulation = true;
        Scanner scanner = new Scanner(System.in);
        StartupInputSource consoleStartupInputSource = new ConsoleStartupInputSource(scanner);
        PrintUtil.printGreetings();
        SimulationCommandSource commandSource;

        while (continueSimulation) {
            PrintUtil.printYesNoAtSimulationStart();
            if (!consoleStartupInputSource.askToStart()) {
                continueSimulation = false;
                PrintUtil.printBye();
                continue;
            }

            PrintUtil.printMapInfo();
            StarterSimulationPreset preset = getSimulationPreset(consoleStartupInputSource);

            WorldMap worldMap = new WorldMap(preset.getMapPreset().getWidth(), preset.getMapPreset().getHeight());
            Renderer renderer = new ConsoleRenderer(worldMap);
            Controller controller = new Controller();
            PathFinder pathFinder = new BFSPathFinder();
            Simulation simulation = new Simulation(worldMap, renderer, controller, preset, pathFinder);
            Thread simThread = new Thread(simulation, "epicSimulation");
            commandSource = new ConsoleSimulationCommandSource(simulation, simThread, scanner);

            simulate(simulation, simThread, commandSource, controller);
        }
        scanner.close();
    }

    private StarterSimulationPreset getSimulationPreset(StartupInputSource inputSource) {
        Optional<Integer> chosenWorld = inputSource.askIntOrEnter(
                ConsoleControls.SMALL_PRESET_KEY,
                ConsoleControls.LARGE_PRESET_KEY
        );
        return chosenWorld
                .map(StarterSimulationPreset::presetFromKey)
                .orElse(StarterSimulationPreset.MEDIUM);
    }

    private void simulate(Simulation simulation,
                          Thread simThread,
                          SimulationCommandSource commandSource,
                          Controller controller) throws InterruptedException {
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
}
