package org.simulation.Action;

import org.entity.Entity;
import org.entity.Grass;
import org.entity.Rock;
import org.entity.Tree;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.InitAction;

import java.util.Random;
import java.util.function.Supplier;

public class InitObstacles implements InitAction {

    private int counter;

    public InitObstacles(int counter) {
        this.counter = counter;
    }

    public InitObstacles() {
        this(10);
    }

    @Override
    public void initiate(WorldMap map) {
        int max = map.getInitialCapacity();

        Random random = new Random();

        placePair(map, random, counter, max, r -> new Rock(), r -> new Tree());
    }
}
