package simulation.config;

import simulation.map.WorldMap;

public class WorldMapFactory {
    public WorldMap getWorldMap(MapSize size) {
        return switch (size) {
            case SMALL -> new WorldMap(12, 12);
            case MEDIUM -> new WorldMap(20, 20);
            case LARGE -> new WorldMap(30, 30);
        };
    }
}
