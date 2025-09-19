package org.simulation.Action;

import org.entity.Grass;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.InitAction;
import org.simulation.Renderer;

import java.util.Random;

public class InitGrass implements InitAction {

    private int counter;

    public InitGrass(int counter) {
        this.counter = counter;
    }

    public InitGrass() {
        this(40);
    }

    @Override
    public void initiate(WorldMap map) {
        int grassToPlace = Math.min(counter, map.getInitialCapacity());
        Random random = new Random();

        placeRandomEntities(map, random, grassToPlace, r -> new Grass());
    }
}
