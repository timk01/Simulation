package v2.dialogue;


import java.util.Locale;
import java.util.Map;

public final class ConsoleConfig {
    public static final char PAUSE_BUTTON = 'ф';
    public static final char RESUME_BUTTON = 'ы';
    public static final char STEP_BUTTON = 'в';
    public static final char STOP_BUTTON = 'ц';

    public static final char YES_BUTTON = 'д';
    public static final char NO_BUTTON = 'н';

    public static final Locale RU = Locale.forLanguageTag("ru");

    private ConsoleConfig() {
    }

    public static final Map<Character, ChosenCommand> COMMANDS = Map.of(
            PAUSE_BUTTON, ChosenCommand.PAUSE,
            RESUME_BUTTON, ChosenCommand.RESUME,
            STEP_BUTTON, ChosenCommand.STEP,
            STOP_BUTTON, ChosenCommand.STOP
    );
}
