package v2.console;

import v2.Simulation;
import v2.controller.Controller;
import v2.dialogue.*;
import v2.settings.SimulationSettings;

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
        PrintUtil.printHelp();

        while (true) {
            if (!simulation.isRunning()) {
                simulation.stop();
                return;
            }

            //System.out.print("> " + System.lineSeparator());
            //System.out.println("Команда (ф=пауза, ы=продолжить, в=ход, ц=выход)");
            Character key = getGuessedCharOrNull();
            if (key == null) {
                simulation.stop();
                simThread.interrupt();
                return;
            }

            ChosenCommand chosenCommand = ConsoleConfig.COMMANDS.get(key);
            switch (chosenCommand) {
                case STOP -> {
                    System.out.println("⏹ stop");
                    simulation.stop();
                    return;
                }
                case STEP -> {
                    System.out.println("⏭ step");
                    simulation.nextTurn();
                }
                case PAUSE -> {
                    System.out.println("⏸ pause");
                    simulation.pauseSimulation();
                }
                case RESUME -> {
                    System.out.println("▶ resume");
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
            System.out.println("Некорректный ввод ->");
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
            System.out.println("Некорректный ввод. Пожалуйста, введите 'д' или 'н'.");
        }
    }

    public ChosenMap askMap() {
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
    }

    public Integer askIntValue(int min, int max) {
        String number;

        while (true) {
            System.out.println("Нужно ввести число (enter = оставить по-умолчанию)");
            number = readTrimmedOrNull();
            if (number == null || number.isEmpty()) {
                return null;
            }
            try {
                int i = Integer.parseInt(number);
                if (i >= min && i <= max) {
                    return i;
                } else {
                    System.out.printf("Число вне диапазона (%d..%d). Повторите ввод.%n", min, max);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private String readTrimmedOrNull() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    public record StartOptions(ChosenMap map, SimulationSettings settings) {

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
    }

    public void close() {
        try {
            scanner.close();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("IllegalStateException is thrown while closing scanner: " + e);
        }
    }
}
