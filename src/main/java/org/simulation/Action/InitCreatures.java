package org.simulation.Action;

import org.entity.Herbivore;
import org.entity.Predator;
import org.entity.Rock;
import org.entity.Tree;
import org.map.WorldMap;
import org.simulation.InitAction;

import java.util.Random;

public class InitCreatures implements InitAction {
    private int counter;

    public InitCreatures(int counter) {
        this.counter = counter;
    }

    public InitCreatures() {
        this(10);
    }
    @Override
    public void initiate(WorldMap map) {
        int max = map.getInitialCapacity();

        Random random = new Random();

        placePair(map, random, counter, max, Herbivore::new, Predator::new);
    }

}
