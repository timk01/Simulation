package simulation.config;

public enum StarterSimulationPreset {
    SMALL(
            WorldMapPreset.SMALL,
            EntitiesQuantityPreset.SMALL,
            EntityStatsPreset.SMALL,
            RepopulatePreset.SMALL
    ),
    MEDIUM(
            WorldMapPreset.MEDIUM,
            EntitiesQuantityPreset.MEDIUM,
            EntityStatsPreset.MEDIUM,
            RepopulatePreset.MEDIUM
    ),
    LARGE(
            WorldMapPreset.LARGE,
            EntitiesQuantityPreset.LARGE,
            EntityStatsPreset.LARGE,
            RepopulatePreset.LARGE
    );

    private final WorldMapPreset mapPreset;
    private final EntitiesQuantityPreset entitiesQuantityPreset;
    private final EntityStatsPreset entityStatsPreset;
    private final RepopulatePreset repopulatePreset;

    StarterSimulationPreset(WorldMapPreset mapPreset,
                            EntitiesQuantityPreset entitiesQuantityPreset,
                            EntityStatsPreset entityStatsPreset,
                            RepopulatePreset repopulatePreset) {
        this.mapPreset = mapPreset;
        this.entitiesQuantityPreset = entitiesQuantityPreset;
        this.entityStatsPreset = entityStatsPreset;
        this.repopulatePreset = repopulatePreset;
        validateOverallCrossMapStats();
    }

    private void validateOverallCrossMapStats() {
        validateGrassThreshold();
        validateHerbivoreThreshold();
        validateEntitiesFitMap();
    }

    private void validateHerbivoreThreshold() {
        if (repopulatePreset.getHerbivoreMin() > entitiesQuantityPreset.getStartHerbivoresQuantity()) {
            throw new IllegalArgumentException(
                    "minimal quantity of herbivore to grow must be less or equal of its started quantity"
            );
        }
    }

    private void validateGrassThreshold() {
        if (repopulatePreset.getGrassMin() > entitiesQuantityPreset.getStartGrassQuantity()) {
            throw new IllegalArgumentException(
                    "minimal quantity of grass to grow must be less or equal of its started quantity"
            );
        }
    }

    private void validateEntitiesFitMap() {
        int mapArea = mapPreset.getHeight() * mapPreset.getWidth();
        int entitiesCount = entitiesQuantityPreset.getStartTreeQuantity() + entitiesQuantityPreset.getStartStonesQuantity()
                + entitiesQuantityPreset.getStartGrassQuantity() + entitiesQuantityPreset.getStartHerbivoresQuantity()
                + entitiesQuantityPreset.getStartPredatorsQuantity();
        if (entitiesCount > mapArea) {
            throw new IllegalArgumentException(
                    "mapArea should be more than overall entity count"
            );
        }
    }

    public WorldMapPreset getMapPreset() {
        return mapPreset;
    }

    public EntitiesQuantityPreset getEntitiesQuantityPreset() {
        return entitiesQuantityPreset;
    }

    public EntityStatsPreset getEntityStatsPreset() {
        return entityStatsPreset;
    }

    public RepopulatePreset getRepopulatePreset() {
        return repopulatePreset;
    }

    public static StarterSimulationPreset presetFromKey(int key) {
        return switch (key) {
            case 1 -> SMALL;
            case 2 -> MEDIUM;
            case 3 -> LARGE;
            default -> throw new IllegalStateException("Unexpected value: " + key);
        };
    }
}

