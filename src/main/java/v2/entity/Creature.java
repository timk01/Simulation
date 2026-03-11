package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;

import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class Creature extends Entity {
    private int speed;
    private int hp;

    List<Location> listOfClosestLocations;

    public void makeMove(WorldMap map, Location oldLocation) {
        Location nextLocation = whereToStep(map, oldLocation);
        if (oldLocation.equals(nextLocation)) {
            return;
        }
        checkLocations(map, oldLocation);
        map.removeEntity(oldLocation);
        boolean hasPut = map.tryAddEntity(nextLocation, this);
        if (!hasPut) {
            map.tryAddEntity(oldLocation, this);
        }
    }

    private void checkLocations(WorldMap map, Location oldLocation) {
        Optional<Entity> entity = map.getEntity(oldLocation);
        if (!(entity.isPresent() && entity.get().equals(this))) {
            throw new IllegalStateException("the creaturecreature is expected at " + oldLocation);
        }
    }

    private Location whereToStep(WorldMap map, Location location) {
        listOfClosestLocations = location.neighbourLocations();
        for (Location nearby : listOfClosestLocations) {
            if (map.isInsideMap(nearby) && map.isCellFree(nearby)) {
                return nearby;
            }
        }
        return location;
    }
}
