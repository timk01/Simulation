package org.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleCommandSource implements CommandSource {
    private static final long MIN_PAUSE_BETWEEN_MOVES = 300L;
    private static final Logger log = LoggerFactory.getLogger(ConsoleCommandSource.class);

    private final Controller controller;
    private final Simulation simulation;
    private final Thread simThread;
    private final Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    private static final Locale RU = Locale.forLanguageTag("ru");

    private static final Map<Character, Boolean> START = Map.of(
            'д', true,
            'н', false
    );

    private static final Map<Character, ChosenCommand> COMMANDS = Map.of(
            'ф', ChosenCommand.PAUSE,
            'ы', ChosenCommand.RESUME,
            'в', ChosenCommand.STEP,
            'ц', ChosenCommand.STOP
    );

    private static final Map<Character, ChosenMap> MAP = Map.of(
            '1', ChosenMap.SMALL,
            '2', ChosenMap.MEDIUM,
            '3', ChosenMap.LARGE
    );

    public ConsoleCommandSource() {
        this.simThread = null;
        this.controller = null;
        this.simulation = null;
    }

    public ConsoleCommandSource(Controller controller, Simulation simulation, Thread simThread) {
        this.controller = controller;
        this.simulation = simulation;
        this.simThread = simThread;
    }

    @Override
    public void runProgram() {
        log.info("Console command loop started");
        greetings();
        printHelp();

        while (true) {
            if (!simulation.isRunning()) {
                log.info("Simulation is no longer running — exiting console loop");
                ConsoleCommandSource.printFinalInfo();
                return;
            }

            System.out.print("> ");
            Character key = getGuessedCharOrNull();
            log.debug("Command key read: '{}'", key);
            if (key == null) {
                simulation.stop();
                simThread.interrupt();
                ConsoleCommandSource.printFinalInfo();
                return;
            }

            ChosenCommand chosenCommand = COMMANDS.get(key);
            switch (chosenCommand) {
                case STOP -> {
                    log.info("Command: STOP");
                    System.out.println("⏹ stop");
                    simulation.stopAndClose();
                    ConsoleCommandSource.printFinalInfo();
                    return;
                }
                case STEP -> {
                    log.info("Command: STEP");
                    System.out.println("⏭ step");
                    int before = simulation.getMoves();
                    controller.oneMoreMove();

                    simulation.awaitMoveIncrement(before, MIN_PAUSE_BETWEEN_MOVES);
                    printStatus();
                }
                case PAUSE -> {
                    log.info("Command: PAUSE");
                    System.out.println("⏸ pause");
                    controller.pause();
                    printStatus();
                }
                case RESUME -> {
                    log.info("Command: RESUME");
                    System.out.println("▶ resume");
                    controller.resume();
                }
            }
        }
    }

    public void close() {
        try {
            scanner.close();
            log.debug("Scanner closed");
        } catch (Exception e) {
            log.warn("Failed to close console scanner", e);
        }
    }

    private Character getGuessedCharOrNull() {
        String word;
        do {
            System.out.println("Команда (ф=пауза, ы=продолжить, в=ход, ц=выход)");
            word = (simulation == null) ? readLineOrNull() : readLineOrNullNonBlocking();
            if (word == null) {
                return null;
            }

            String properInput = toLowerRus(word);
            log.trace("Command raw input: '{}', normalized: '{}'", word, properInput);
            if (properInput.length() == 1 && isKeyAllowed(properInput.charAt(0), COMMANDS)) {
                return properInput.charAt(0);
            }
            log.warn("Invalid command input: '{}'", word);
            System.out.println("Некорректный ввод ->");
        } while (true);
    }

    private String readLineOrNull() {
        if (!scanner.hasNextLine()) {
            log.debug("Scanner has no next line (EOF)");
            return null;
        }
        try {
            String line = scanner.nextLine();
            log.trace("Read line: '{}'", line);
            return line;
        } catch (NoSuchElementException | IllegalStateException e) {
            log.warn("Scanner read failed", e);
            return null;
        }
    }

    private String readLineOrNullNonBlocking() {
        try {
            while (true) {
                if (simulation != null && !simulation.isRunning()) {
                    return null;
                }
                if (System.in.available() > 0) {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        log.trace("Read line: '{}'", line);
                        return line;
                    }
                }
                Thread.sleep(50);
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } catch (IOException e) {
            log.warn("Console non-blocking read failed", e);
            return null;
        }
    }

    public boolean askToStart() {
        while (true) {
            String str = readLineOrNull();
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

    private Boolean parseYesNoSymbol(String s) {
        if (s == null) {
            return null;
        }
        String t = toLowerRus(s).trim();
        if (t.length() != 1) {
            return null;
        }
        char c = t.charAt(0);
        if (c == 'д') {
            return true;
        }
        if (c == 'н') {
            return false;
        }
        return null;
    }

    public ChosenMap askMap() {
        while (true) {
            printMapInfo();
            System.out.print("> ");
            String s = readLineOrNull();
            if (s == null) {
                log.info("askMap: enter -> default MEDIUM");
                return ChosenMap.MEDIUM;
            }
            s = s.trim();
            if (s.isEmpty()) {
                log.info("askMap: empty -> default MEDIUM");
                return ChosenMap.MEDIUM;
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

            ChosenMap chosenMapCommand = MAP.get(number);
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
            String askedStr = readLineOrNull();
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
                    log.warn("askIntValue: out of range {} ({}..{})", i, min, max);
                }
            } catch (NumberFormatException ignored) {
                log.warn("askIntValue: not a number '{}'", trimmedStr);
            }
        }
    }

    private String toLowerRus(String s) {
        return s.trim().toLowerCase(RU);
    }

    public static <E extends Enum<E>> boolean isKeyAllowed(char c, Map<Character, E> table) {
        return table.containsKey(c);
    }

    private void printStatus() {
        System.out.println("status: moves=" + simulation.getMoves());
    }

    private static void printHelp() {
        System.out.println("""
                Управление (русская раскладка WASD):
                  ф — пауза
                  ы — продолжить, пока не будет сделана пауза или выход
                  в — сделать ровно один ход и ожидать команды (игнорирует бесконтрольный беспаузный режим) 
                  ц — выход
                """);
    }

    private static void printMapInfo() {
        System.out.println("""
                Выберите пресет карты (нужно ввести строго 1 число):
                  1 — маленькая (12×12)
                  2 — средняя  (20×20)
                  3 — большая  (30×30)
                  Enter/пробел — средняя по умолчанию (20×20)
                """);
    }

    private static void greetings() {
        System.out.println("""
                Добро пожаловать в программу симуляция!
                            
                Здесь вы сможете наблюдать за эмуляцией (хотя и ограниченной) животного мира:
                демонстрационное окно откроется справа.
                                
                Помимо неподвижных объектов: камней, деревьев, есть двое видов существ - это травоядные и хищники.
                Травоядные - питаются травой (она постепенно растет), хищники - травоядными и - 
                движутся по карте к своим целям по кратчайшему маршртуру.
                                
                У всех животных постепенно уменьшается здоровье, но трава растет.
                                
                А в конце симуляции - вы можете увидеть статистику.
                            
                В начале игры вы можете выбрать режим и настройки
                - карты, количество движимых/недвижимых объектов на ней, паузы и ходов (см. ниже)
                            
                Желаю нескучно провести время!
                                
                                
                ****************************************************
                """);
    }

    public static void printFinalInfo() {
        System.out.println("""
                Расширенную статистику симуляцию с финальным отчетом вы можете посмотреть в:
                logs/simulation.log
                """);
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

        Integer max = null;
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

    enum ChosenCommand {
        PAUSE,
        RESUME,
        STEP,
        STOP;
    }

    public enum ChosenMap {
        SMALL,
        MEDIUM,
        LARGE
    }
}
