package org.simulation;

import org.entity.Entity;
import org.map.Location;
import org.map.WorldMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public interface InitAction {
    void initiate(WorldMap map);

    default void placeRandomEntities(WorldMap map, int count,
                                     Supplier<? extends Entity> factory) {
        int needToPlace = Math.min(count, map.getFreeCapacity());
        if (needToPlace <= 0) {
            return;
        }

        List<Location> emptyLocations = map.listEmptyLocationsShuffled();

        int placed = 0;
        for (int i = 0; i < emptyLocations.size() && placed < needToPlace; i++) {
            map.placeEntity(emptyLocations.get(i), factory.get());
            placed++;
        }
    }

    default int getRealEntityQuantityToPlace(WorldMap map, int requestedRoom, double share) {
        int initialCapacity = map.getInitialCapacity();
        int perTypeCap = (int) Math.floor(initialCapacity * share);
        int allowedByType = Math.max(0, perTypeCap); 
        int allowedByMap = map.getRoomLeftUnderCap();
        return Math.min(requestedRoom, Math.min(allowedByType, allowedByMap));
    }
}
