package org.simulation.Action;

import org.entity.Rock;
import org.entity.Tree;
import org.map.WorldMap;
import org.simulation.InitAction;

public class InitObstacles implements InitAction {
    private static final int DEFAULT_OBSTACLES = 20;
    private static final int MINIMUM_OBSTACLES = 10;
    private static final double OBSTACLES_SHARE_OF_ROOM = 0.2;


    private final int totalObstacles;

    public InitObstacles(int totalObstacles) {
        if (totalObstacles < 0) {
            throw new IllegalArgumentException("totalObstacles quantity cannot be less than zero");
        }
        this.totalObstacles = (totalObstacles < MINIMUM_OBSTACLES) ? DEFAULT_OBSTACLES : totalObstacles;

    }

    public InitObstacles() {
        this(DEFAULT_OBSTACLES);
    }

    @Override
    public void initiate(WorldMap map) {
        int realObstaclesQuantityToPlace = getRealEntityQuantityToPlace(map, totalObstacles, OBSTACLES_SHARE_OF_ROOM);

        if (realObstaclesQuantityToPlace == 0) {
            System.out.printf("[PLACEMENT][Obstacles] requested=%d, placed=0, capLeft=%d%n",
                    totalObstacles, map.getRoomLeftUnderCap());
            return;
        }

        int rocksToPlace = realObstaclesQuantityToPlace / 2 + (realObstaclesQuantityToPlace % 2);
        placeRandomEntities(map, rocksToPlace, Rock::new);

        int treesToPlace = realObstaclesQuantityToPlace / 2;
        placeRandomEntities(map, treesToPlace, Tree::new);
        if (realObstaclesQuantityToPlace < totalObstacles) {
            System.out.printf("[PLACEMENT][Obstacles] requested=%d, placed=%d, capLeft=%d%n",
                    totalObstacles, realObstaclesQuantityToPlace, map.getRoomLeftUnderCap());
        }
    }
}
