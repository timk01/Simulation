package org.simulation.Action;

import org.entity.Rock;
import org.entity.Tree;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.simulation.config.ObstaclesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitObstacles implements InitAction {
    private static final Logger log = LoggerFactory.getLogger(InitObstacles.class);


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
            log.info("[PLACEMENT][Obstacles] is zero: requested={}, placed=0, capLeft={}",
                    totalObstacles, map.getRoomLeftUnderCap());
            return;
        }

        int rocksToPlace = realObstaclesQuantityToPlace / 2 + (realObstaclesQuantityToPlace % 2);
        placeRandomEntities(map, rocksToPlace, Rock::new);

        int treesToPlace = realObstaclesQuantityToPlace / 2;
        placeRandomEntities(map, treesToPlace, Tree::new);
        if (realObstaclesQuantityToPlace < totalObstacles) {
            log.warn("[PLACEMENT][Obstacles] truncated by cap: requested={}, placed={}, capLeft={}",
                    totalObstacles, realObstaclesQuantityToPlace, map.getRoomLeftUnderCap());
        }
    }
}
