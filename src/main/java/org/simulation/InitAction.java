package org.simulation;

import org.entity.Entity;
import org.map.Location;
import org.map.WorldMap;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public interface InitAction {
    void initiate(WorldMap map);

    default void placeRandomEntities(WorldMap map, Random random, int count,
                                     Function<Random, ? extends Entity> factory) {
        int placed = 0;
        while (placed < count) {
            Location loc = map.getRandomLocation(random);
            if (!map.checkLocation(loc)) {
                map.placeEntity(loc, factory.apply(random));
                placed++;
            }
        }
    }

    default void placePair(WorldMap map, Random random, int counter, int max,
                           Function<Random, ? extends Entity> first,
                           Function<Random, ? extends Entity> second) {
        int totalToPlace = counter * 2;
        if (totalToPlace > max) {
            int reduced = max / 2;
            placeRandomEntities(map, random, reduced, first);
            placeRandomEntities(map, random, max - reduced, second);
        } else {
            placeRandomEntities(map, random, counter, first);
            placeRandomEntities(map, random, counter, second);
        }
    }
}
