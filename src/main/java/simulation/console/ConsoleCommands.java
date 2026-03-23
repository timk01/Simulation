package simulation.console;

import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleCommands {
    private final Scanner scanner;

    public ConsoleCommands(Scanner scanner) {
        this.scanner = scanner;
    }

    public Character readValidCommandCharOrNull() {
        String word;
        do {
            word = readTrimmedOrNull();
            if (word == null) {
                return null;
            }

            String properInput = word.trim().toLowerCase(ConsoleSymbols.RU);
            if (properInput.length() == 1 && isKeyAllowed(properInput.charAt(0), ConsoleSymbols.COMMANDS)) {
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
        if (charAt == ConsoleSymbols.YES_BUTTON) {
            return true;
        }
        if (charAt == ConsoleSymbols.NO_BUTTON) {
            return false;
        }
        return null;
    }

    private String toLowerRus(String string) {
        if (string == null) {
            return null;
        }
        return string.trim().toLowerCase(ConsoleSymbols.RU);
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

    private String readTrimmedOrNull() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }
}
