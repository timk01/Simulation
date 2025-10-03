package org.map;

import org.entity.Entity;
import org.entity.Grass;
import org.entity.Rock;
import org.entity.Tree;
import org.simulation.config.MapConfig;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WorldMap {
    private final int width;
    private final int height;
    private final double occupancyRatio;

    private int worldMapVersion;
    private final Map<Location, Entity> cells;
    private final MapConfig cfg;

    public WorldMap(MapConfig cfg) {
        this.cfg = cfg;
        this.width = cfg.getWidth();
        this.height = cfg.getHeight();
        this.occupancyRatio = cfg.getOccupancyRatio();
        this.cells = new HashMap<>((int) Math.floor(width * height * occupancyRatio));
    }

    public WorldMap() {
        this(new MapConfig());
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWorldMapVersion() {
        return worldMapVersion;
    }

    public Map<Location, Entity> getCells() {
        return cells;
    }

    public int getInitialCapacity() {
        return this.width * this.height;
    }

    public int getOccupiedCapacity() {
        return cells.size();
    }

    public Location getRandomLocation() {
        return new Location(
                ThreadLocalRandom.current().nextInt(this.width),
                ThreadLocalRandom.current().nextInt(this.height)
        );
    }

    private int getCap() {
        return (int) Math.floor(getInitialCapacity() * this.occupancyRatio);
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

    public boolean isInside(Location location) {
        return location.x() >= 0 && location.y() >= 0
                && location.x() < this.width && location.y() < this.height;
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

    public List<Location> listEmptyLocations() {
        List<Location> empty = new ArrayList<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Location loc = new Location(x, y);
                if (!checkLocation(loc)) {
                    empty.add(loc);
                }
            }
        }
        return empty;
    }

    public List<Location> listEmptyLocationsShuffled() {
        List<Location> empty = listEmptyLocations();
        Collections.shuffle(empty, ThreadLocalRandom.current());
        return empty;
    }
}

