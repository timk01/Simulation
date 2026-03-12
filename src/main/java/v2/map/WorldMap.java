package v2.map;

import v2.entity.Entity;
import v2.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static v2.entity.EntityType.ROCK;

public class WorldMap {
    private final Map<Location, Entity> map = new HashMap<>();
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean tryAddEntity(Location location, Entity entity) {
        if (location == null) {
            throw new NullPointerException("addEntity: location cannot be null");
        }
        if (entity == null) {
            throw new NullPointerException("addEntity: entity cannot be null");
        }

        if (!isInsideMap(location)) {
            throw new IllegalArgumentException("addEntity on: " + location + " - location out of bounds");
        }

        if (isCellFree(location)) {
            map.put(location, entity);
            return true;
        } else {
            return false;
        }
    }

    public boolean isInsideMap(Location location) {
        return (location.x() >= 0 && location.x() < width) &&
                (location.y() >= 0 && location.y() < height);
    }

    public boolean isCellFree(Location location) {
        return getEntity(location).isEmpty();
    }

    public Optional<Entity> getEntity(Location location) {
        return Optional.ofNullable(map.get(location));
    }

    public void removeEntity(Location location) {
        if (location == null) {
            throw new NullPointerException("removeEntity: location cannot be null");
        }
        map.remove(location);
    }

    public int countEntityPerType(EntityType type) {
        return  (int) map.values().stream()
                .filter(Objects::nonNull)
                .filter((entity) -> type.matches(entity))
                .count();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

