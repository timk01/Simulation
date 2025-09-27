package org.simulation;

import org.entity.Entity;
import org.map.Location;
import org.map.WorldMap;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public interface InitAction {
    void initiate(WorldMap map);

    default void placeRandomEntities(WorldMap map, int count,
                                     Supplier<? extends Entity> factory) {
        int placed = 0;
        while (placed < count) {
            Location loc = map.getRandomLocation();
            if (!map.checkLocation(loc)) {
                map.placeEntity(loc, factory.get());
                placed++;
            }
        }
    }

    default void placePair(WorldMap map, int counter, int max,
                           Supplier<? extends Entity> first,
                           Supplier<? extends Entity> second) {
        int totalToPlace = counter * 2;
        if (totalToPlace > max) {
            int reduced = max / 2;
            placeRandomEntities(map, reduced, first);
            placeRandomEntities(map, max - reduced, second);
        } else {
            placeRandomEntities(map, counter, first);
            placeRandomEntities(map, counter, second);
        }
    }
}
