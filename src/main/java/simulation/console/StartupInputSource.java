package simulation.console;

import java.util.Optional;

public interface StartupInputSource {
    boolean askToStart();

    Optional<Integer> askIntOrEnter(int min, int max);
}
