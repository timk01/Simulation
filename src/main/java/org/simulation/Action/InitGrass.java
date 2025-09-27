package org.simulation.Action;

import org.entity.Grass;
import org.map.WorldMap;
import org.simulation.InitAction;

public class InitGrass implements InitAction {
    private static final int DEFAULT_GRASS = 40;
    private static final int MINIMUM_GRASS = 5;
    private static final double GRASS_SHARE_OF_ROOM = 0.2;

    private final int grassCount;

    public InitGrass(int counter) {
        if (counter < 0) {
            throw new IllegalArgumentException("grass quantity cannot be less than zero");
        }
        this.grassCount = (counter < MINIMUM_GRASS) ? DEFAULT_GRASS : counter;
    }

    public InitGrass() {
        this(DEFAULT_GRASS);
    }

    @Override
    public void initiate(WorldMap map) {
        int realGrassQuantityToPlace = getRealGrassQuantityToPlace(map);

        placeRandomEntities(map, realGrassQuantityToPlace, Grass::new);
        if (realGrassQuantityToPlace < grassCount) {
            System.out.printf("[PLACEMENT][Grass] requested=%d, placed=%d, capLeft=%d%n",
                    grassCount, realGrassQuantityToPlace, map.getRoomLeftUnderCap());
        }
    }

    private int getRealGrassQuantityToPlace(WorldMap map) {
        int roomLeft = map.getRoomLeftUnderCap();
        int allowedByCap = (int) Math.ceil(roomLeft * GRASS_SHARE_OF_ROOM);
        if (allowedByCap == 0 && roomLeft > 0 && grassCount > 0) {
            allowedByCap = 1;
        }
        return Math.min(grassCount, allowedByCap);
    }
}
