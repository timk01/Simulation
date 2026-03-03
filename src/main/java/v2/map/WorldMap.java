package v2.map;

import v2.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        if (!checkBorders(location)) {
            throw new IllegalArgumentException("addEntity on: " + location + " - location out of bounds");
        }

        if (isEmpty(location)) {
            map.put(location, entity);
            return true;
        } else {
            return false;
        }
    }

    private boolean checkBorders(Location location) {
        return (location.x() >= 0 && location.x() < width) &&
                (location.y() >= 0 && location.y() < height);
    }

    private boolean isEmpty(Location location) {
        return getEntity(location).isEmpty();
    }

    public Optional<Entity> getEntity(Location location) {
        return Optional.ofNullable(map.get(location));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

