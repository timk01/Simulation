package simulation.config;

public class SimulationConfigFactory {
    public SimulationConfig getSimulationConfig(MapSize size) {
        if (size == null) {
            throw new IllegalArgumentException("map size cannot be null");
        }

        WorldMapFactory worldMapFactory = new WorldMapFactory();
        EntityStartValuesFactory entityStartValuesFactory = new EntityStartValuesFactory();
        EntityCharacteristicsFactory entityCharacteristicsFactory = new EntityCharacteristicsFactory();
        RepopulateValuesFactory repopulateValuesFactory = new RepopulateValuesFactory();

        SimulationConfig config = new SimulationConfig(
                worldMapFactory.getWorldMap(size),
                entityCharacteristicsFactory.geEntityStartCharacteristics(size),
                entityStartValuesFactory.geStartEntitiesValues(size),
                repopulateValuesFactory.getRepopulateValues(size));

        SimulationConfigValidator.validate(config);
        return config;
    }
}
