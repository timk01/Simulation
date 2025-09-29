package org.simulation.Action;

import org.entity.Herbivore;
import org.entity.Predator;
import org.map.WorldMap;
import org.simulation.InitAction;

import java.util.Map;

public class InitCreatures implements InitAction {
    private static final int DEFAULT_HERBIVORES = 15;
    private static final int DEFAULT_PREDATORS = 15;
    private static final int MINIMUM_CREATURES = 5;
    private static final double HERBIVORES_SHARE_OF_ROOM = 0.2;
    private static final double PREDATORS_SHARE_OF_ROOM = 0.2;

    private final int herbivoreCount;
    private final int predatorCount;

    public InitCreatures(int herbivoreCount, int predatorCount) {
        if (herbivoreCount < 0 || predatorCount < 0) {
            throw new IllegalArgumentException("herbivores or predators quantity cannot be less than zero");
        }
        this.herbivoreCount = (herbivoreCount < MINIMUM_CREATURES) ? DEFAULT_HERBIVORES : herbivoreCount;
        this.predatorCount = (predatorCount < MINIMUM_CREATURES) ? DEFAULT_PREDATORS : predatorCount;
    }

    public InitCreatures() {
        this(DEFAULT_HERBIVORES, DEFAULT_PREDATORS);
    }

    public int getHerbivoreCount() {
        return herbivoreCount;
    }

    public int getPredatorCount() {
        return predatorCount;
    }

    @Override
    public void initiate(WorldMap map) {
        int realHerbivoresQuantityToPlace = getRealEntityQuantityToPlace(map, herbivoreCount, HERBIVORES_SHARE_OF_ROOM);
        placeRandomEntities(map, realHerbivoresQuantityToPlace, Herbivore::new);

        int realPredatorsQuantityToPlace = getRealEntityQuantityToPlace(map, predatorCount, PREDATORS_SHARE_OF_ROOM);
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
