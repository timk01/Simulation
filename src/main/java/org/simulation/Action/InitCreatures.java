package org.simulation.Action;

import org.entity.Herbivore;
import org.entity.Predator;
import org.entity.Rock;
import org.entity.Tree;
import org.map.WorldMap;
import org.simulation.InitAction;

import java.util.Random;

public class InitCreatures implements InitAction {
    private final int herbivoreCount;
    private final int predatorCount;

    public InitCreatures(int herbivoreCount, int predatorCount) {
        this.herbivoreCount = herbivoreCount;
        this.predatorCount = predatorCount;
    }

    public InitCreatures() {
        this.herbivoreCount = 15;
        this.predatorCount = 15;
    }

    public int getHerbivoreCount() {
        return herbivoreCount;
    }

    public int getPredatorCount() {
        return predatorCount;
    }

    @Override
    public void initiate(WorldMap map) {
        int max = map.getInitialCapacity();

        Random random = new Random();

        //placePair(map, random, counter, max, Herbivore::new, Predator::new);

        placeRandomEntities(map, random, herbivoreCount, Herbivore::new);
        placeRandomEntities(map, random, predatorCount, Predator::new);
    }

}
