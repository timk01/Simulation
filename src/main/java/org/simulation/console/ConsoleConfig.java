package org.simulation.console;

import java.util.Locale;
import java.util.Map;

public class ConsoleConfig {
    public static final long MIN_PAUSE_BETWEEN_MOVES = 300L;
    public static final int NON_BLOCKING_POLL_MS = 50;
    public static final Locale RU = Locale.forLanguageTag("ru");

    public static final ChosenMap DEFAULT_MAP = ChosenMap.MEDIUM;

    public static final Map<Character, ChosenCommand> COMMANDS = Map.of('ф', ChosenCommand.PAUSE,
            'ы', ChosenCommand.RESUME,
            'в', ChosenCommand.STEP,
            'ц', ChosenCommand.STOP
    );

    public static final Map<Character, ChosenMap> MAP = Map.of(
            '1', ChosenMap.SMALL,
            '2', ChosenMap.MEDIUM,
            '3', ChosenMap.LARGE
    );
}
