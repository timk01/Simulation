package org.simulation;

import org.simulation.console.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleCommandSource implements CommandSource {
    private static final Logger log = LoggerFactory.getLogger(ConsoleCommandSource.class);

    private final Controller controller;
    private final Simulation simulation;
    private final Thread simThread;
    private final ConsoleIO io;
    private final InputChecker ic = new InputChecker();

    public ConsoleCommandSource(ConsoleIO io) {
        this.io = io;
        this.simThread = null;
        this.controller = null;
        this.simulation = null;
    }

    public ConsoleCommandSource(Controller controller, Simulation simulation, Thread simThread, ConsoleIO io) {
        this.io = io;
        this.controller = controller;
        this.simulation = simulation;
        this.simThread = simThread;
    }

    @Override
    public void runProgram() {
        log.info("Console command loop started");
        PrintUtil.greetings();
        PrintUtil.printHelp();

        while (true) {
            if (!simulation.isRunning()) {
                log.info("Simulation is no longer running — exiting console loop");
                simulation.stopAndClose();
                PrintUtil.printFinalInfo();
                return;
            }

            System.out.print("> ");
            Character key = getGuessedCharOrNull();
            log.debug("Command key read: '{}'", key);
            if (key == null) {
                simulation.stop();
                simThread.interrupt();
                PrintUtil.printFinalInfo();
                return;
            }

            ChosenCommand chosenCommand = ConsoleConfig.COMMANDS.get(key);
            switch (chosenCommand) {
                case STOP -> {
                    log.info("Command: STOP");
                    System.out.println("⏹ stop");
                    simulation.stopAndClose();
                    PrintUtil.printFinalInfo();
                    return;
                }
                case STEP -> {
                    log.info("Command: STEP");
                    System.out.println("⏭ step");
                    int before = simulation.getMoves();
                    controller.oneMoreMove();

                    simulation.awaitMoveIncrement(before, ConsoleConfig.MIN_PAUSE_BETWEEN_MOVES);
                    PrintUtil.printStatus(simulation.getMoves());
                }
                case PAUSE -> {
                    log.info("Command: PAUSE");
                    System.out.println("⏸ pause");
                    controller.pause();
                    PrintUtil.printStatus(simulation.getMoves());
                }
                case RESUME -> {
                    log.info("Command: RESUME");
                    System.out.println("▶ resume");
                    controller.resume();
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
            System.out.println("Команда (ф=пауза, ы=продолжить, в=ход, ц=выход)");
            word = (simulation == null)
                    ? io.readLineOrNull()
                    : io.readLineOrNullNonBlockingUntil(() -> !simulation.isRunning());
            if (word == null) {
                return null;
            }

            String properInput = toLowerRus(word);
            log.trace("Command raw input: '{}', normalized: '{}'", word, properInput);
            if (properInput.length() == 1 && InputChecker.isKeyAllowed(properInput.charAt(0), ConsoleConfig.COMMANDS)) {
                return properInput.charAt(0);
            }
            log.warn("Invalid command input: '{}'", word);
            System.out.println("Некорректный ввод ->");
        } while (true);
    }

    public boolean askToStart() {
        while (true) {
            String str = io.readLineOrNull();
            if (str == null) {
                log.info("askToStart: EOF -> return false");
                return false;
            }

            Boolean properSymbol = parseYesNoSymbol(str);
            if (properSymbol != null) {
                log.debug("askToStart: parsed={} from input='{}'", properSymbol, str);
                return properSymbol;
            }
            log.warn("askToStart: invalid input='{}'", str);
            System.out.println("Некорректный ввод. Пожалуйста, введите 'д' или 'н'.");
        }
    }

    public ChosenMap askMap() {
        while (true) {
            PrintUtil.printMapInfo();
            System.out.print("> ");
            String s = io.readLineOrNull();
            if (s == null || s.trim().isEmpty()) {
                log.info("askMap: default -> {}", ConsoleConfig.DEFAULT_MAP);
                return ConsoleConfig.DEFAULT_MAP;
            }

            if (s.length() > 1) {
                log.warn("askMap: multi-char '{}'", s);
                System.out.println("Некорректный ввод ->");
                continue;
            }

            char number = s.charAt(0);
            if (!Character.isDigit(number)) {
                log.warn("askMap: not a digit '{}'", s);
                System.out.println("Некорректный ввод ->");
                continue;
            }

            ChosenMap chosenMapCommand = ConsoleConfig.MAP.get(number);
            if (chosenMapCommand != null) {
                log.info("askMap: chosen {}", chosenMapCommand);
                return chosenMapCommand;
            }
            log.warn("askMap: digit '{}' not in options", number);
            System.out.println("Некорректный ввод ->");
        }
    }

    public Integer askIntValue(int min, int max) {
        while (true) {
            System.out.println("Нужно ввести число (enter = оставить по-умолчанию)");
            String askedStr = io.readLineOrNull();
            if (askedStr == null || askedStr.isEmpty()) {
                log.info("askIntValue: default (null) chosen");
                return null;
            }
            String trimmedStr = askedStr.trim();
            try {
                int i = Integer.parseInt(trimmedStr);
                if (i >= min && i <= max) {
                    log.info("askIntValue: accepted {}", i);
                    return i;
                } else {
                    System.out.printf("Число вне диапазона (%d..%d). Повторите ввод.%n", min, max);
                    log.warn("askIntValue: out of range {} ({}..{})", i, min, max);
                }
            } catch (NumberFormatException ignored) {
                log.warn("askIntValue: not a number '{}'", trimmedStr);
            }
        }
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

        log.info("Start options: map={}, delayMs={}, maxMoves={}", chosen, settings.getDelay(), settings.getMaxMoves());

        return new StartOptions(chosen, settings);
    }

    public void close() {
        io.close();
    }
}
