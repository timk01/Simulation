package org.map;

import org.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorldMap {

    private final int width;
    private final int height;
    private Map<Location, Entity> cells = new HashMap<>();

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Location, Entity> getCells() {
        return cells;
    }

    public int getInitialCapacity() {
        return width * height;
    }

    public int getOccupiedCapacity() {
        return cells.size();
    }

    public Location getRandomLocation(Random random) {
        return new Location(random.nextInt(width), random.nextInt(height));
    }

    public int getFreeCapacity() {
        return getInitialCapacity() - getOccupiedCapacity();
    }

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
