package v2.config;

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
    private final EntitiesQuantityPreset entitiesPreset;
    private final EntityStatsPreset entityStatsPreset;
    private final RepopulatePreset repopulatePreset;

    StarterSimulationPreset(WorldMapPreset mapPreset,
                            EntitiesQuantityPreset entitiesPreset,
                            EntityStatsPreset entityStatsPreset,
                            RepopulatePreset repopulatePreset) {
        this.mapPreset = mapPreset;
        this.entitiesPreset = entitiesPreset;
        this.entityStatsPreset = entityStatsPreset;
        this.repopulatePreset = repopulatePreset;
    }

    public WorldMapPreset getMapPreset() {
        return mapPreset;
    }

    public EntitiesQuantityPreset getEntitiesPreset() {
        return entitiesPreset;
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

