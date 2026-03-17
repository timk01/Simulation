package simulation.console;

import simulation.Simulation;
import simulation.controller.Controller;
import simulation.dialogue.ChosenCommand;
import simulation.dialogue.ConsoleConfig;
import simulation.dialogue.PrintUtil;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleCommandSource implements CommandSource {
    private final Controller controller;
    private final Simulation simulation;
    private final Thread simThread;
    private final Scanner scanner;

    public ConsoleCommandSource(Controller controller, Simulation simulation, Thread simThread, Scanner scanner) {
        this.scanner = scanner;
        this.controller = controller;
        this.simulation = simulation;
        this.simThread = simThread;
    }

    public ConsoleCommandSource(Scanner scanner) {
        this.scanner = scanner;
        this.simThread = null;
        this.controller = null;
        this.simulation = null;
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

            ChosenCommand chosenCommand = ConsoleConfig.COMMANDS.get(key);
            PrintUtil.printSpecificCommand(chosenCommand);
            switch (chosenCommand) {
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

            String properInput = word.trim().toLowerCase(ConsoleConfig.RU);
            if (properInput.length() == 1 && isKeyAllowed(properInput.charAt(0), ConsoleConfig.COMMANDS)) {
                return properInput.charAt(0);
            }
            PrintUtil.printInvalidInput();
        } while (true);
    }

    private <E extends Enum<E>> boolean isKeyAllowed(char c, Map<Character, E> table) {
        return table.containsKey(c);
    }

    public boolean askToStart() {
        String symbol;

        while (true) {
            symbol = readTrimmedOrNull();
            if (symbol == null) {
                return false;
            }

            Boolean properSymbol = parseYesNoSymbol(symbol);
            if (properSymbol != null) {
                return properSymbol;
            }
            PrintUtil.printInvalidYesNoInput();
        }
    }

    private Boolean parseYesNoSymbol(String string) {
        if (string == null) {
            return null;
        }
        String loweredString = toLowerRus(string);
        if (loweredString.length() != 1) {
            return null;
        }
        char charAt = loweredString.charAt(0);
        if (charAt == ConsoleConfig.YES_BUTTON) {
            return true;
        }
        if (charAt == ConsoleConfig.NO_BUTTON) {
            return false;
        }
        return null;
    }

    private String toLowerRus(String string) {
        if (string == null) {
            return null;
        }
        return string.trim().toLowerCase(ConsoleConfig.RU);
    }

    private String readTrimmedOrNull() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    public Optional<Integer> askIntOrEnter(int min, int max) {
        while (true) {
            PrintUtil.printAskNumberOrEnter();
            String value = readTrimmedOrNull();

            if (value == null || value.isEmpty()) {
                return Optional.empty();
            }

            if (value.length() > 1) {
                PrintUtil.printInvalidInput();
                continue;
            }

            char ch = value.charAt(0);

            if (!Character.isDigit(ch)) {
                PrintUtil.printInvalidInput();
                continue;
            }

            int number = Character.getNumericValue(ch);

            if (number >= min && number <= max) {
                return Optional.of(number);
            }

            PrintUtil.printOutOfRange(min, max);
        }
    }

    public void close() {
        try {
            scanner.close();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("IllegalStateException is thrown while closing scanner: " + e);
        }
    }
}
