package org.map;

import org.entity.Entity;
import org.entity.Grass;
import org.entity.Rock;
import org.entity.Tree;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class WorldMap {
    private static final double MAX_OCCUPANCY_RATIO = 0.5;
    private static final int DEFAULT_WIDTH = 20;
    private static final int DEFAULT_HEIGHT = 20;
    private static final int MINIMUM_THRESHOLD = 10;

    private int worldMapVersion;
    private final int width;
    private final int height;
    private final Map<Location, Entity> cells = new HashMap<>();

    public WorldMap(int width, int height) {
        this.width = (width < MINIMUM_THRESHOLD) ? DEFAULT_WIDTH : width;
        this.height = (height < MINIMUM_THRESHOLD) ? DEFAULT_HEIGHT : height;
    }

    public WorldMap() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getWorldMapVersion() {
        return worldMapVersion;
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

    public Location getRandomLocation() {
        return new Location(
                ThreadLocalRandom.current().nextInt(width),
                ThreadLocalRandom.current().nextInt(height)
        );
    }

    private int getCap() {
        return (int) Math.floor(getInitialCapacity() * MAX_OCCUPANCY_RATIO);
    }

    public int getRoomLeftUnderCap() {
        return Math.max(0, getCap() - getOccupiedCapacity());
    }

    public int getFreeCapacity() {
        return getInitialCapacity() - getOccupiedCapacity();
    }

    public void placeEntity(Location location, Entity newEntity) {
        if (location == null) {
            throw new NullPointerException("placeEntity: location cannot be null");
        }
        if (newEntity == null) {
            throw new NullPointerException("placeEntity: entity cannot be null");
        }
        if (!isInside(location)) {
            throw new IllegalArgumentException("placeEntity on: " + location + " - location out of bounds");
        }
        Entity oldEntity = cells.put(location, newEntity);

        boolean wasRelevantBefore = relevantForMapPath(oldEntity);
        boolean isRelevantNow = relevantForMapPath(newEntity);

        if (wasRelevantBefore != isRelevantNow) {
            worldMapVersion++;
        }
    }

    private boolean isInside(Location location) {
        return location.x() >= 0 && location.y() >= 0
                && location.x() < width && location.y() < height;
    }

    public Entity getEntityByLocation(Location location) {
        return cells.get(location);
    }

    public boolean checkLocation(Location location) {
        return cells.containsKey(location);
    }

    public void removeEntity(Location location) {
        if (location == null) {
            throw new NullPointerException("removeEntity: location cannot be null");
        }
        Entity oldEntity = cells.remove(location);

        if (relevantForMapPath(oldEntity)) {
            worldMapVersion++;
        }
    }

    private boolean relevantForMapPath(Entity e) {
        return e instanceof Rock || e instanceof Tree || e instanceof Grass;
    }
}
