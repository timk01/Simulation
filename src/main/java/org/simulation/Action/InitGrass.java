package org.simulation.Action;

import org.entity.Grass;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.simulation.config.GrassConfig;

public class InitGrass implements InitAction {
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
            System.out.printf("[PLACEMENT][Grass] requested=%d, placed=%d, capLeft=%d%n",
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
