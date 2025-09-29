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

    default int getRealEntityQuantityToPlace(WorldMap map, int requestedRoom, double share) {
        int roomLeft = map.getRoomLeftUnderCap();
        int allowedByCap = (int) Math.ceil(roomLeft * share);
        if (allowedByCap == 0 && roomLeft > 0 && requestedRoom > 0) {
            allowedByCap = 1;
        }
        return Math.min(requestedRoom, allowedByCap);
    }
}
