package simulation.config;

public class EntityStartValuesFactory {
    public EntityStartValues geStartEntitiesValues(MapSize size) {
        return switch (size) {
            case SMALL -> new EntityStartValues(
                    5, 5, 12, 10, 5
            );
            case MEDIUM -> new EntityStartValues(
                    15, 15, 40, 25, 15
            );
            case LARGE -> new EntityStartValues(
                    25, 25, 80, 40, 25
            );
        };
    }
}