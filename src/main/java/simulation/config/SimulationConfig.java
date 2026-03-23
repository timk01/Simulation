package simulation.config;

import simulation.map.WorldMap;

public record SimulationConfig(WorldMap worldMap, EntityCharacteristics entityCharacteristics,
                               EntityStartValues entityStartValues, RepopulateValues repopulateValues) {

    public SimulationConfig(WorldMap worldMap,
                            EntityCharacteristics entityCharacteristics,
                            EntityStartValues entityStartValues,
                            RepopulateValues repopulateValues) {
        this.worldMap = worldMap;
        this.entityCharacteristics = entityCharacteristics;
        this.entityStartValues = entityStartValues;
        this.repopulateValues = repopulateValues;
        SimulationConfigValidator.validate(this);
    }
}
