package v2.actions;

import v2.config.RepopulatePreset;
import v2.entity.Entity;
import v2.entity.EntityFactory;
import v2.entity.EntityType;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KeepPopulationStableAction implements Action {
    private final ActionHelper actionHelper;
    private final RepopulatePreset repopulatePreset;

    public KeepPopulationStableAction(ActionHelper actionHelper, RepopulatePreset repopulatePreset) {
        this.actionHelper = actionHelper;
        this.repopulatePreset = repopulatePreset;
    }

    @Override
    public void execute(WorldMap map) {
        List<Location> emptyLocations = actionHelper.fillEmptyLocationsList(map);
        Collections.shuffle(emptyLocations);

        int grassThreshold = repopulatePreset.getGrassMin();
        int herbivoreThreshold = repopulatePreset.getHerbivoreMin();
        Map<Location, Entity> mapSnapshot = map.getMapSnapshot();
        int currentGrass = countEntityPerType(mapSnapshot, EntityType.GRASS);
        int currentHerbivores = countEntityPerType(mapSnapshot, EntityType.HERBIVORE);

        int needGrass = Math.max(0, grassThreshold - currentGrass);
        int needHerbivores = Math.max(0, herbivoreThreshold - currentHerbivores);
        if (needGrass + needHerbivores > emptyLocations.size()) {
            throw new IllegalArgumentException("need more fields than can populated!");
        }
        if (needGrass > 0) {
            populate(needGrass, map, actionHelper.getEntityFactory(), emptyLocations, EntityType.GRASS);
        }
        if (needHerbivores > 0) {
            populate(needHerbivores, map, actionHelper.getEntityFactory(), emptyLocations, EntityType.HERBIVORE);
        }
    }

    private int countEntityPerType(Map<Location, Entity> snapshot, EntityType type) {
        return (int) snapshot.values().stream()
                .filter(Objects::nonNull)
                .filter((entity) -> type.matches(entity))
                .count();
    }

    private void populate(int threshold,
                          WorldMap worldMap,
                          EntityFactory entityFactory,
                          List<Location> emptyLocations,
                          EntityType type) {
        for (int i = 0; i < threshold; i++) {
            worldMap.tryAddEntity(emptyLocations.get(i), entityFactory.createEntity(type));
        }
        if (threshold > 0) {
            emptyLocations.subList(0, threshold).clear();
        }
    }
}
