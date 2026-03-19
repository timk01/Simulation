package simulation.console;

import java.util.Optional;
import java.util.Scanner;

public class ConsoleStartupInputSource implements StartupInputSource {
    private final Scanner scanner;

    public ConsoleStartupInputSource(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
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
        if (charAt == ConsoleControls.YES_BUTTON) {
            return true;
        }
        if (charAt == ConsoleControls.NO_BUTTON) {
            return false;
        }
        return null;
    }

    private String toLowerRus(String string) {
        if (string == null) {
            return null;
        }
        return string.trim().toLowerCase(ConsoleControls.RU);
    }

    @Override
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
