package v2;

import v2.console.ConsoleCommandSource;
import v2.controller.Controller;
import v2.map.WorldMap;
import v2.renderer.Renderer;

import java.util.Scanner;

public class SimulationApp {
    public void runSimulation() throws InterruptedException {
        boolean continueSimulation = true;
        Scanner scanner = new Scanner(System.in);
        ConsoleCommandSource commandSource = new ConsoleCommandSource(scanner);

        while (continueSimulation) {
            System.out.println("Начать новую симуляцию? Введите 'д' для начала или 'н' для выхода.");
            if (!commandSource.askToStart()) {
                continueSimulation = false;
                continue;
            }

            WorldMap worldMap = new WorldMap(10, 10);
            Renderer renderer = new Renderer(worldMap);
            Controller controller = new Controller();
            Simulation simulation = new Simulation(worldMap, renderer, controller);
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
