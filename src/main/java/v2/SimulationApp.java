package v2;

import v2.config.StarterSimulationPreset;
import v2.console.ConsoleCommandSource;
import v2.controller.Controller;
import v2.dialogue.ConsoleConfig;
import v2.dialogue.PrintUtil;
import v2.map.WorldMap;
import v2.renderer.Renderer;

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
