package v2.console;

import v2.Simulation;
import v2.controller.Controller;
import v2.dialogue.*;

import java.util.Scanner;

public class ConsoleCommandSource implements CommandSource {
    private final Controller controller;
    private final Simulation simulation;
    private final Thread simThread;
    private final Scanner scanner;
    private final InputChecker ic = new InputChecker();

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
        PrintUtil.greetings();
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
            switch (chosenCommand) {
                case STOP -> {
                    PrintUtil.printSpecificCommand(chosenCommand);
//                    PrintUtil.printCommandPrompt();
                    simulation.stop();
                    return;
                }
                case STEP -> {
                    PrintUtil.printSpecificCommand(chosenCommand);
//                    PrintUtil.printCommandPrompt();
                    simulation.nextTurn();
                }
                case PAUSE -> {
                    PrintUtil.printSpecificCommand(chosenCommand);
//                    PrintUtil.printCommandPrompt();
                    simulation.pauseSimulation();
                }
                case RESUME -> {
                    PrintUtil.printSpecificCommand(chosenCommand);
//                    PrintUtil.printCommandPrompt();
                    simulation.resumeSimulation();
                }
            }
        }
    }

    public String toLowerRus(String s) {
        return ic.toLowerRus(s);
    }

    private Boolean parseYesNoSymbol(String s) {
        return ic.parseYesNoSymbol(s);
    }

    private Character getGuessedCharOrNull() {
        String word;
        do {
            word = readTrimmedOrNull();
            if (word == null) {
                return null;
            }

            String properInput = toLowerRus(word);
            if (properInput.length() == 1 && InputChecker.isKeyAllowed(properInput.charAt(0), ConsoleConfig.COMMANDS)) {
                return properInput.charAt(0);
            }
            PrintUtil.printInvalidInput();
        } while (true);
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


    private String readTrimmedOrNull() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }


    public Integer askIntValue(int min, int max) {
        String number;

        while (true) {
            PrintUtil.printAskNumberOrEnter();
            number = readTrimmedOrNull();
            if (number == null || number.isEmpty()) {
                return null;
            }
            try {
                int i = Integer.parseInt(number);
                if (i >= min && i <= max) {
                    return i;
                } else {
                    PrintUtil.printOutOfRange(min, max);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void close() {
        try {
            scanner.close();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("IllegalStateException is thrown while closing scanner: " + e);
        }
    }
/*    public record StartOptions(ChosenMap map, SimulationSettings settings) {

    }

    public StartOptions collectStartOptions() {
        ChosenMap chosen = askMap();

        SimulationSettings settings = new SimulationSettings();

        Integer delay = null;
        System.out.println("Изменить задержку между ходами? [д/н] --> по умолчанию "
                + SimulationSettings.defaultDelay() + " мс (нужно неотрицательное число)");
        if (askToStart()) {
            Integer ms = askIntValue(0, SimulationSettings.maxDelay());
            if (ms != null) {
                settings.setDelay(ms);
            }
        }

        System.out.println("Задать лимит ходов? [д/н] --> по умолчанию бесконечно");
        if (askToStart()) {
            Integer n = askIntValue(0, SimulationSettings.maxMovesLimit());
            if (n != null) {
                settings.setMaxMoves(n);
            }
        }

        return new StartOptions(chosen, settings);
    }*/
    /*    public ChosenMap askMap() {
            String word;

            while (true) {
                PrintUtil.printMapInfo();
                System.out.print("> ");
                word = readTrimmedOrNull();
                if (word == null || word.isEmpty()) {
                    return ConsoleConfig.DEFAULT_MAP;
                }

                if (word.length() > 1) {
                    System.out.println("Некорректный ввод ->");
                    continue;
                }

                char number = word.charAt(0);
                if (!Character.isDigit(number)) {
                    System.out.println("Некорректный ввод ->");
                    continue;
                }

                ChosenMap chosenMapCommand = ConsoleConfig.MAP.get(number);
                if (chosenMapCommand != null) {
                    return chosenMapCommand;
                }
                System.out.println("Некорректный ввод ->");
            }
        }*/
}
