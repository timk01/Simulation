package simulation.config;

/**
 * Stats - for any anity characterestics, for now they are SAME, but are supposed to become bigger by logic:
 * bigger map = different stats
 */

public class EntityCharacteristicsFactory {
    public EntityCharacteristics geEntityStartCharacteristics(MapSize size) {
        return switch (size) {
            case SMALL -> (new EntityCharacteristics(
                    new EntityCharacteristics.HerbivoreStats(2, 10, 20),
                    new EntityCharacteristics.PredatorStats(3, 10, 20, 8),
                    new EntityCharacteristics.GrassStats(5))
            );
            case MEDIUM -> (new EntityCharacteristics(
                    new EntityCharacteristics.HerbivoreStats(2, 10, 20),
                    new EntityCharacteristics.PredatorStats(3, 10, 20, 8),
                    new EntityCharacteristics.GrassStats(5))
            );
            case LARGE -> (new EntityCharacteristics(
                    new EntityCharacteristics.HerbivoreStats(2, 10, 20),
                    new EntityCharacteristics.PredatorStats(3, 10, 20, 8),
                    new EntityCharacteristics.GrassStats(5))
            );
        };
    }
}