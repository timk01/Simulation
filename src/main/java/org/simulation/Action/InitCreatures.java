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

    private final int herbivoreCount;
    private final int predatorCount;

    public InitCreatures(int herbivoreCount, int predatorCount) {
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
/*        int freeCapacity = map.getFreeCapacity();
        if (herbivoreCount + predatorCount >= freeCapacity / )*/
        placeRandomEntities(map, herbivoreCount, Herbivore::new);
        placeRandomEntities(map, predatorCount, Predator::new);
    }

}
