package org.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleCommandSource implements CommandSource {
    private static final Logger log = LoggerFactory.getLogger(ConsoleCommandSource.class);

    private final Controller controller;
    private final Simulation simulation;
    private final Thread simThread;
    private final Scanner scanner = new Scanner(System.in);
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
                return;
            }

            System.out.print("> ");
            char key;
            try {
                key = getGuessedChar();
                log.debug("Command key read: '{}'", key);
            } catch (EOFException eof) {
                log.warn("EOF while reading command, stopping simulation", eof);
                simulation.stop();
                simThread.interrupt();
                return;
            }

            ChosenCommand chosenCommand = COMMANDS.get(key);
            switch (chosenCommand) {
                case STOP -> {
                    log.info("Command: STOP");
                    System.out.println("⏹ stop");
                    simulation.stop();
                    simThread.interrupt();
                    return;
                }
                case STEP -> {
                    log.info("Command: STEP");
                    System.out.println("⏭ step");
                    controller.oneMoreMove();
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
                    printStatus();
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

    private char getGuessedNumber() throws EOFException {
        while (true) {
            String word = readLineOrNull();
            if (word == null) {
                throw new EOFException("EOF while guessing number");
            }

            word = word.trim();
            log.trace("Number raw input: '{}'", word);
            if (!word.isEmpty()) {
                char ch = word.charAt(0);
                if (isKeyAllowed(ch, MAP)) {
                    log.debug("Number accepted: '{}'", ch);
                    return ch;
                }
            }
            log.warn("Invalid number input: '{}'", word);
            System.out.println("Некорректный ввод ->");
        }
    }

    private char getGuessedChar() throws EOFException {
        String word;
        do {
            System.out.println("Команда (ф=пауза, ы=продолжить, в=ход, ц=выход)");
            word = readLineOrNull();
            if (word == null) {
                throw new EOFException("EOF while quesssing char");
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
            System.out.println("Нужно ввести число (enter = оставить пресет по-умолчанию)");
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
                            
                Здесь вы сможете наблюдать за эмуляцией (хотя и ограниченной) животного мира - окно откроется справа.
                Помимо неподвижных объектов: камней, деревьев, есть двое видов существ - это травоядные и хищники.
                Травоядные - питаются травой (она постепенно растет), хищники - травоядными.
                У всех животных постепенно уменьшается здоровье, а в конце симуляции - вы можете увидеть статистику.
                            
                Также, если вас на устраивают настройки по умолчанию или выбранный режим
                - карты, количество движимых/недвижимых объектов н ней (см. ниже),
                вы можете попытаться задать свои.
                            
                Желаю нескучно провести время!
                                
                                
                ****************************************************
                """);
    }

    public record StartOptions(ChosenMap map, SimulationSettings settings) {
    }

    public StartOptions collectStartOptions() {
        ChosenMap chosen = askMap();

        SimulationSettings settings = new SimulationSettings();

        Integer delay = null;
        System.out.println("Изменить задержку между ходами? [д/н] --> по умолчанию "
                + SimulationSettings.defaultDelay() + " мс");
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
