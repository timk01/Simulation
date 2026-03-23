package simulation;

import simulation.console.ConsoleControls;
import simulation.console.ConsoleStartupInputSource;
import simulation.console.PrintUtil;
import simulation.console.StartupInputSource;
import simulation.controller.Controller;
import simulation.map.WorldMap;
import simulation.path.BFSPathFinder;
import simulation.path.PathFinder;
import simulation.config.MapSize;
import simulation.config.SimulationConfig;
import simulation.config.SimulationConfigFactory;
import simulation.renderer.ConsoleRenderer;
import simulation.renderer.Renderer;

import java.util.Scanner;

import static simulation.config.MapSize.*;

public class Starter {
    public static void main(String[] args) throws InterruptedException {
        boolean continueSimulation = true;
        Scanner scanner = new Scanner(System.in);
        StartupInputSource consoleStartupInputSource = new ConsoleStartupInputSource(scanner);
        PrintUtil.printGreetings();

        while (continueSimulation) {
            PrintUtil.printYesNoAtSimulationStart();
            if (!consoleStartupInputSource.askToStart()) {
                continueSimulation = false;
                PrintUtil.printBye();
                continue;
            }

            PrintUtil.printMapInfo();

            Integer userChoose = consoleStartupInputSource.askIntOrEnter(
                    ConsoleControls.SMALL_PRESET_KEY,
                    ConsoleControls.LARGE_PRESET_KEY
            ).orElse(ConsoleControls.MEDIUM_PRESET_KEY);

            Simulation simulation = getSimulation(userChoose);

            new SimulationApp(simulation, scanner).runSimulation();
        }
        scanner.close();
    }

    private static Simulation getSimulation(Integer userChoose) {
        MapSize mapSize = presetFromKey(userChoose);
        SimulationConfigFactory configFactory = new SimulationConfigFactory();
        SimulationConfig config = configFactory.getSimulationConfig(mapSize);
        WorldMap worldMap = config.worldMap();

        Renderer renderer = new ConsoleRenderer(worldMap);
        Controller controller = new Controller();
        PathFinder pathFinder = new BFSPathFinder();
        return new Simulation(worldMap, renderer, controller, config, pathFinder);
    }

    public static MapSize presetFromKey(int key) {
        return switch (key) {
            case 1 -> SMALL;
            case 2 -> MEDIUM;
            case 3 -> LARGE;
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }
}
