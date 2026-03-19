package simulation.console;

import simulation.Simulation;
import simulation.controller.Controller;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleSimulationCommandSource implements SimulationCommandSource {
    private final Simulation simulation;
    private final Thread simThread;
    private final Scanner scanner;

    public ConsoleSimulationCommandSource(Simulation simulation,
                                          Thread simThread,
                                          Scanner scanner) {
        this.simulation = simulation;
        this.simThread = simThread;
        this.scanner = scanner;
    }

    @Override
    public void runProgram() {
        PrintUtil.mapPreviewMsg();

        while (true) {
            if (!simulation.isRunning()) {
                simulation.stop();
                return;
            }

            Character key = getGuessedCharOrNull();
            if (key == null) {
                simulation.stop();
                simThread.interrupt();
                return;
            }

            SimulationCommand simulationCommand = ConsoleControls.COMMANDS.get(key);
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

    private Character getGuessedCharOrNull() {
        String word;
        do {
            word = readTrimmedOrNull();
            if (word == null) {
                return null;
            }

            String properInput = word.trim().toLowerCase(ConsoleControls.RU);
            if (properInput.length() == 1 && isKeyAllowed(properInput.charAt(0), ConsoleControls.COMMANDS)) {
                return properInput.charAt(0);
            }
            PrintUtil.printInvalidInput();
        } while (true);
    }

    private <E extends Enum<E>> boolean isKeyAllowed(char c, Map<Character, E> table) {
        return table.containsKey(c);
    }

    private String readTrimmedOrNull() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}
