package v2.map;

import v2.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldMap {
    private final Map<Location, Entity> map = new HashMap<>();
    private final int width;
    private final int height;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void addEntity(Location location, Entity entity) {
        map.put(location, entity);
    }

    public Entity getEntity(Location location) {
        return map.get(location);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

