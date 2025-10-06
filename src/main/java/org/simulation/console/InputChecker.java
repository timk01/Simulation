package org.simulation.console;

import java.util.Map;

public final class InputChecker {
    public InputChecker() {
    }

    public String toLowerRus(String s) {
        if (s == null) {
            return null;
        }

        return s.trim().toLowerCase(ConsoleConfig.RU);
    }

    public Boolean parseYesNoSymbol(String s) {
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

    public static <E extends Enum<E>> boolean isKeyAllowed(char c, Map<Character, E> table) {
        return table.containsKey(c);
    }
}
