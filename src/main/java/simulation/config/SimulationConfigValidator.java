package simulation.config;

import simulation.map.WorldMap;

public final class SimulationConfigValidator {
    private SimulationConfigValidator() {
    }

    public static void validate(WorldMap worldMap) {
        if (worldMap == null) {
            throw new IllegalArgumentException("worldMap cannot be null");
        }
        if (worldMap.getWidth() <= 0 || worldMap.getHeight() <= 0) {
            throw new IllegalArgumentException("map size quantities must be positive");
        }
    }

    public static void validate(EntityStartValues values) {
        if (values == null) {
            throw new IllegalArgumentException("entityStartValues cannot be null");
        }
        if (values.startTreeQuantity() < 0
                || values.startStonesQuantity() < 0
                || values.startGrassQuantity() < 0
                || values.startHerbivoresQuantity() < 0
                || values.startPredatorsQuantity() < 0) {
            throw new IllegalArgumentException("entity start quantities cannot be negative");
        }
    }

    public static void validate(EntityCharacteristics values) {
        if (values == null) {
            throw new IllegalArgumentException("entityCharacteristics cannot be null");
        }
        if (values.getHerbivore() == null || values.getPredator() == null || values.getGrass() == null) {
            throw new IllegalArgumentException("entity characteristics parts cannot be null");
        }
        if (values.getHerbivore().speed() <= 0
                || values.getHerbivore().hp() <= 0
                || values.getHerbivore().maxHp() <= 0) {
            throw new IllegalArgumentException("herbivore stats (speed, hp, maxHp) must be positive");
        }
        if (values.getPredator().speed() <= 0
                || values.getPredator().hp() <= 0
                || values.getPredator().maxHp() <= 0
                || values.getPredator().attack() <= 0) {
            throw new IllegalArgumentException("predator stats (speed, hp, maxHp, attack) must be positive");
        }
        if (values.getGrass().nutrition() <= 0) {
            throw new IllegalArgumentException("grass stats (nutrition) must be positive");
        }
    }

    public static void validate(RepopulateValues values) {
        if (values == null) {
            throw new IllegalArgumentException("repopulateValues cannot be null");
        }
        if (values.getGrassMin() <= 0 || values.getHerbivoreMin() <= 0) {
            throw new IllegalArgumentException("grassMin and herbivoreMin must be positive");
        }
    }

    public static void validate(SimulationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("simulationConfig cannot be null");
        }

        validate(config.worldMap());
        validate(config.entityCharacteristics());
        validate(config.entityStartValues());
        validate(config.repopulateValues());

        validateGrassThreshold(config);
        validateHerbivoreThreshold(config);
        validateEntitiesFitMap(config);
    }

    private static void validateGrassThreshold(SimulationConfig config) {
        if (config.repopulateValues().getGrassMin() > config.entityStartValues().startGrassQuantity()) {
            throw new IllegalArgumentException(
                    "minimal quantity of grass to grow must be less or equal to its started quantity"
            );
        }
    }

    private static void validateHerbivoreThreshold(SimulationConfig config) {
        if (config.repopulateValues().getHerbivoreMin() > config.entityStartValues().startHerbivoresQuantity()) {
            throw new IllegalArgumentException(
                    "minimal quantity of herbivore to grow must be less or equal to its started quantity"
            );
        }
    }

    private static void validateEntitiesFitMap(SimulationConfig config) {
        int mapArea = config.worldMap().getHeight() * config.worldMap().getWidth();
        int entitiesCount = config.entityStartValues().startTreeQuantity()
                + config.entityStartValues().startStonesQuantity()
                + config.entityStartValues().startGrassQuantity()
                + config.entityStartValues().startHerbivoresQuantity()
                + config.entityStartValues().startPredatorsQuantity();

        if (entitiesCount > mapArea) {
            throw new IllegalArgumentException("mapArea should be more than overall entity count");
        }
    }
}