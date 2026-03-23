package simulation;

import simulation.console.ConsoleSymbols;
import simulation.console.ConsoleCommands;
import simulation.console.PrintUtil;
import simulation.console.SimulationCommand;

import java.util.Scanner;

public class SimulationApp {
    private final Simulation simulation;
    private final Scanner scanner;

    public SimulationApp(Simulation simulation, Scanner scanner) {
        this.simulation = simulation;
        this.scanner = scanner;
    }

    public void runSimulation() throws InterruptedException {
        Thread simThread = new Thread(simulation, "epicSimulation");
        simulate(simulation, simThread, new ConsoleCommands(scanner));
    }

    private void simulate(Simulation simulation,
                          Thread simThread,
                          ConsoleCommands commandSource) throws InterruptedException {
        simulation.pauseSimulation();
        simThread.start();
        try {
            runProgram(simulation, simThread, commandSource);
        } finally {
            simulation.stop();
            simThread.interrupt();
            simThread.join();
        }
    }

    private void runProgram(Simulation simulation,
                            Thread simThread,
                            ConsoleCommands commandSource) {
        PrintUtil.mapPreviewMsg();

        while (true) {
            if (!simulation.isRunning()) {
                simulation.stop();
                return;
            }

            Character key = commandSource.readValidCommandCharOrNull();
            if (key == null) {
                simulation.stop();
                return;
            }

            SimulationCommand simulationCommand = ConsoleSymbols.COMMANDS.get(key);
            PrintUtil.printSpecificCommand(simulationCommand);
            switch (simulationCommand) {
                case STOP -> {
                    simulation.stop();
                    return;
                }
                case STEP -> {
                    simulation.nextTurn();
                }
                case PAUSE -> {
                    simulation.pauseSimulation();
                }
                case RESUME -> {
                    simulation.resumeSimulation();
                }
            }
        }
    }
}
