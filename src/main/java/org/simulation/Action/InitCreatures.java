package org.simulation.Action;

import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.simulation.config.CreaturesConfig;

public class InitCreatures implements InitAction {
    private final int herbivoreCount;
    private final int predatorCount;
    private final double herbivoresCapShare;
    private final double predatorsCapShare;
    private final CreaturesConfig cfg;

    public InitCreatures(CreaturesConfig cfg) {
        this.cfg = cfg;
        this.herbivoreCount = cfg.getHerbivoreCount();
        this.predatorCount = cfg.getPredatorCount();
        this.herbivoresCapShare = cfg.getHerbivoresCapShare();
        this.predatorsCapShare = cfg.getPredatorsCapShare();
    }

    public InitCreatures() {
        this(new CreaturesConfig());
    }

    public int getHerbivoreCount() {
        return herbivoreCount;
    }

    public int getPredatorCount() {
        return predatorCount;
    }

    @Override
    public void initiate(WorldMap map) {
        int realHerbivoresQuantityToPlace = getRealEntityQuantityToPlace(map, this.herbivoreCount, this.herbivoresCapShare);
        placeRandomEntities(map, realHerbivoresQuantityToPlace, Herbivore::new);

        int realPredatorsQuantityToPlace = getRealEntityQuantityToPlace(map, this.predatorCount, this.predatorsCapShare);
        placeRandomEntities(map, realPredatorsQuantityToPlace, Predator::new);

        if (realHerbivoresQuantityToPlace < herbivoreCount) {
            System.out.printf("[PLACEMENT][Herbivores] requested=%d, placed=%d, capLeft=%d%n",
                    herbivoreCount, realHerbivoresQuantityToPlace, map.getRoomLeftUnderCap());
        }
        if (realPredatorsQuantityToPlace < predatorCount) {
            System.out.printf("[PLACEMENT][Predators] requested=%d, placed=%d, capLeft=%d%n",
                    predatorCount, realPredatorsQuantityToPlace, map.getRoomLeftUnderCap());
        }
    }
}
