package simulation.actions;

import simulation.entity.EntityFactory;
import simulation.map.Location;
import simulation.map.WorldMap;

import java.util.ArrayList;
import java.util.List;

final public class ActionHelper {
    private final EntityFactory entityFactory;

    public ActionHelper(EntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    EntityFactory getEntityFactory() {
        return entityFactory;
    }

    List<Location> fillEmptyLocationsList(WorldMap worldMap) {
        List<Location> freeLocations = new ArrayList<>();
        Location location;
        for (int y = 0; y < worldMap.getHeight(); y++) {
            for (int x = 0; x < worldMap.getWidth(); x++) {
                location = new Location(x, y);
                if (worldMap.getEntity(location).isEmpty()) {
                    freeLocations.add(location);
                }
            }
        }
        return freeLocations;
    }
}
