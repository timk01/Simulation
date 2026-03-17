package simulation.map;

import simulation.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WorldMap {
    private final Map<Location, Entity> entityMap = new HashMap<>();
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean tryAddEntity(Location location, Entity entity) {
        checkEntityAndLocation(location, entity);

        if (isCellFree(location)) {
            entityMap.put(location, entity);
            return true;
        } else {
            return false;
        }
    }

    private void checkEntityAndLocation(Location location, Entity entity) {
        if (location == null) {
            throw new NullPointerException("addEntity: location cannot be null");
        }
        if (entity == null) {
            throw new NullPointerException("addEntity: entity cannot be null");
        }

        if (!isInsideMap(location)) {
            throw new IllegalArgumentException("addEntity on: " + location + " - location out of bounds");
        }
    }

    public boolean isInsideMap(Location location) {
        return (location.x() >= 0 && location.x() < width)
                && (location.y() >= 0 && location.y() < height);
    }

    public boolean isCellFree(Location location) {
        return getEntity(location).isEmpty();
    }

    public Optional<Entity> getEntity(Location location) {
        return Optional.ofNullable(entityMap.get(location));
    }

    public void removeEntity(Location location) {
        if (location == null) {
            throw new NullPointerException("removeEntity: location cannot be null");
        }
        entityMap.remove(location);
    }

    public Map<Location, Entity> getMapSnapshot() {
        return new HashMap<>(entityMap);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

