package org.simulation.Action;

import org.entity.Entity;
import org.entity.Herbivore;
import org.map.WorldMap;
import org.simulation.TurnAction;

public class FlushGrassEatenAction implements TurnAction {

    @Override
    public void update(WorldMap map) {
        for (Entity e : map.getCells().values()) {
            if (e instanceof Herbivore h && h.getConsumedGrass() > 0) {
                    h.resetConsumedGrass();
            }
        }
    }
}