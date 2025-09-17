package org.map;

import org.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class WorldMap {
    private Map<Location, Entity> cells = new HashMap<>();

    public void placeEntity(Location location, Entity entity) {
        cells.put(location, entity);
    }

    public Entity getEntityByLocation(Location location) {
        return cells.get(location);
    }

    public boolean checkLocation(Location location) {
        return cells.containsKey(location);
    }

    public void removeEntity(Location location) {
        cells.remove(location);
    }
}
