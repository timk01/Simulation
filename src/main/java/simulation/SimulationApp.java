package simulation;

import simulation.config.StarterSimulationPreset;
import simulation.console.ConsoleCommandSource;
import simulation.controller.Controller;
import simulation.dialogue.ConsoleConfig;
import simulation.dialogue.PrintUtil;
import simulation.map.WorldMap;
import simulation.renderer.Renderer;

import java.util.Optional;
import java.util.Scanner;

public class SimulationApp {
    public void runSimulation() throws InterruptedException {
        boolean continueSimulation = true;
        Scanner scanner = new Scanner(System.in);
        ConsoleCommandSource commandSource = new ConsoleCommandSource(scanner);
        PrintUtil.greetings();

        while (continueSimulation) {
            PrintUtil.printYesNoAtSimulationStart();
            if (!commandSource.askToStart()) {
                continueSimulation = false;
                continue;
            }

            PrintUtil.printMapInfo();
            StarterSimulationPreset preset = getSimulationPreset(commandSource);

            WorldMap worldMap = new WorldMap(preset.getMapPreset().getWidth(), preset.getMapPreset().getHeight());
            Renderer renderer = new Renderer(worldMap);
            Controller controller = new Controller();
            Simulation simulation = new Simulation(worldMap, renderer, controller, preset);
            Thread simThread = new Thread(simulation, "epicSimulation");
            commandSource = new ConsoleCommandSource(controller, simulation, simThread, scanner);

            simulate(simulation, simThread, commandSource, controller);
        }
        commandSource.close();
    }

    private StarterSimulationPreset getSimulationPreset(ConsoleCommandSource commandSource) {
        Optional<Integer> chosenWorld = commandSource.askIntOrEnter(
                ConsoleConfig.SMALL_PRESET_KEY,
                ConsoleConfig.LARGE_PRESET_KEY
        );
        return chosenWorld
                .map(StarterSimulationPreset::presetFromKey)
                .orElse(StarterSimulationPreset.MEDIUM);
    }

    private void simulate(Simulation simulation,
                          Thread simThread,
                          ConsoleCommandSource commandSource,
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
