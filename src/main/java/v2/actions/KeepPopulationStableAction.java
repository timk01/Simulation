package v2.actions;

import v2.entity.EntityFactory;
import v2.entity.EntityType;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.Collections;
import java.util.List;

public class KeepPopulationStableAction implements Action {
    private ActionHelper actionHelper;

    public KeepPopulationStableAction(ActionHelper actionHelper) {
        this.actionHelper = actionHelper;
    }

    @Override
    public void execute(WorldMap map) {
        List<Location> emptyLocations = actionHelper.fillEmptyLocationsList(map);
        Collections.shuffle(emptyLocations);

        int maxGrass = 5;
        int maxHerbivores = 5;
        int currentGrass = map.countEntityPerType(EntityType.GRASS);
        int currentHerbivores = map.countEntityPerType(EntityType.HERBIVORE);

        int needGrass = Math.max(0, maxGrass - currentGrass);
        int needHerbivores = Math.max(0, maxHerbivores - currentHerbivores);
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

    private void populate(int delta,
                          WorldMap map,
                          EntityFactory entityFactory,
                          List<Location> emptyLocations,
                          EntityType type) {
        for (int i = 0; i < delta; i++) {
            map.tryAddEntity(emptyLocations.get(i), entityFactory.createEntity(type));
            emptyLocations.remove(0);
        }
    }
}
