package simulation;

import simulation.console.*;
import simulation.controller.Controller;

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
        ConsoleSimulationCommandSource commandSource = new ConsoleSimulationCommandSource(simulation, simThread, scanner);
        simulate(simulation, simThread, commandSource);
    }

    private void simulate(Simulation simulation,
                          Thread simThread,
                          SimulationCommandSource commandSource) throws InterruptedException {
        simulation.pauseSimulation();
        simThread.start();
        try {
            commandSource.runProgram();
        } finally {
            simulation.stop();
            simThread.interrupt();
            simThread.join();
        }
    }
}
