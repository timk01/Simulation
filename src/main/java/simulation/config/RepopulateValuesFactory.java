package simulation.config;

public class RepopulateValuesFactory {
    public RepopulateValues getRepopulateValues(MapSize size) {
        return switch (size) {
            case SMALL -> (new RepopulateValues(8, 4));
            case MEDIUM -> (new RepopulateValues(20, 10));
            case LARGE -> (new RepopulateValues(30, 15));
        };
    }
}