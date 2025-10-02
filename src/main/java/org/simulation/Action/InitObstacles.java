package org.simulation.Action;

import org.entity.Rock;
import org.entity.Tree;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.simulation.config.ObstaclesConfig;

public class InitObstacles implements InitAction {
    private final int totalObstacles;
    private final double capShare;
    private final ObstaclesConfig cfg;

    public InitObstacles(ObstaclesConfig cfg) {
        this.cfg = cfg;
        this.totalObstacles = cfg.getTotalObstacles();
        this.capShare = cfg.getCapShare();
    }

    public InitObstacles() {
        this(new ObstaclesConfig());
    }

    @Override
    public void initiate(WorldMap map) {
        int realObstaclesQuantityToPlace = getRealEntityQuantityToPlace(map, this.totalObstacles, this.capShare);

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
