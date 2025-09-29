package org.simulation.Action;

import org.entity.Grass;
import org.map.WorldMap;
import org.simulation.InitAction;

public class InitGrass implements InitAction {
    private static final int DEFAULT_GRASS = 40;
    private static final int MINIMUM_GRASS = 5;
    private static final double GRASS_SHARE_OF_ROOM = 0.2;

    private final int grassCount;

    public InitGrass(int grassCount) {
        if (grassCount < 0) {
            throw new IllegalArgumentException("grass quantity cannot be less than zero");
        }
        this.grassCount = (grassCount < MINIMUM_GRASS) ? DEFAULT_GRASS : grassCount;
    }

    public InitGrass() {
        this(DEFAULT_GRASS);
    }

    @Override
    public void initiate(WorldMap map) {
        int realGrassQuantityToPlace = getRealEntityQuantityToPlace(map, grassCount, GRASS_SHARE_OF_ROOM);

        placeRandomEntities(map, realGrassQuantityToPlace, Grass::new);
        if (realGrassQuantityToPlace < grassCount) {
            System.out.printf("[PLACEMENT][Grass] requested=%d, placed=%d, capLeft=%d%n",
                    grassCount, realGrassQuantityToPlace, map.getRoomLeftUnderCap());
        }
    }
}
