package v2.config;

public enum StarterSimulationPreset {
    SMALL(
            WorldMapPreset.SMALL,
            EntitiesPreset.SMALL,
            EntityStatsPreset.SMALL,
            RepopulatePreset.SMALL
    ),
    MEDIUM(
            WorldMapPreset.MEDIUM,
            EntitiesPreset.MEDIUM,
            EntityStatsPreset.MEDIUM,
            RepopulatePreset.MEDIUM
    ),
    LARGE(
            WorldMapPreset.LARGE,
            EntitiesPreset.LARGE,
            EntityStatsPreset.LARGE,
            RepopulatePreset.LARGE
    );

    private final WorldMapPreset mapPreset;
    private final EntitiesPreset entitiesPreset;
    private final EntityStatsPreset entityStatsPreset;
    private final RepopulatePreset repopulatePreset;

    StarterSimulationPreset(WorldMapPreset mapPreset,
                            EntitiesPreset entitiesPreset,
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

    public EntitiesPreset getEntitiesPreset() {
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

