package org.simulation.Action;

import org.entity.Grass;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.simulation.config.GrassConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitGrass implements InitAction {
    private static final Logger log = LoggerFactory.getLogger(InitGrass.class);


    private final int grassCount;
    private final double capShare;
    private final GrassConfig cfg;

    public InitGrass(GrassConfig cfg) {
        this.cfg = cfg;
        this.grassCount = cfg.getGrassCount();
        this.capShare = cfg.getCapShare();
    }

    public InitGrass() {
        this(new GrassConfig());
    }

    @Override
    public void initiate(WorldMap map) {
        int realGrassQuantityToPlace = getRealEntityQuantityToPlace(map, this.grassCount, this.capShare);

        placeRandomEntities(map, realGrassQuantityToPlace, Grass::new);
        if (realGrassQuantityToPlace < grassCount) {
            log.warn("[PLACEMENT][Grass] truncated by cap: requested={}, placed={}, capLeft={}",
                    grassCount, realGrassQuantityToPlace, map.getRoomLeftUnderCap());
        }
    }

    public int getGrassCount() {
        return grassCount;
    }

    public double getCapShare() {
        return capShare;
    }

    public GrassConfig getCfg() {
        return cfg;
    }
}
