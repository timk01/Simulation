package simulation.console;

import java.util.Locale;
import java.util.Map;

public final class ConsoleControls {
    public static final int SMALL_PRESET_KEY = 1;
    public static final int MEDIUM_PRESET_KEY = 2;
    public static final int LARGE_PRESET_KEY = 3;

    public static final char PAUSE_BUTTON = 'ф';
    public static final char RESUME_BUTTON = 'ы';
    public static final char STEP_BUTTON = 'в';
    public static final char STOP_BUTTON = 'ц';

    public static final char YES_BUTTON = 'д';
    public static final char NO_BUTTON = 'н';

    public static final Locale RU = Locale.forLanguageTag("ru");

    private ConsoleControls() {
    }

    public static final Map<Character, SimulationCommand> COMMANDS = Map.of(
            PAUSE_BUTTON, SimulationCommand.PAUSE,
            RESUME_BUTTON, SimulationCommand.RESUME,
            STEP_BUTTON, SimulationCommand.STEP,
            STOP_BUTTON, SimulationCommand.STOP
    );
}
